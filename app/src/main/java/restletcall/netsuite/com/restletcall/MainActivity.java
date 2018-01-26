package restletcall.netsuite.com.restletcall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import android.widget.ArrayAdapter;


public class MainActivity extends AppCompatActivity {

    String id;
    private ProgressDialog pd;
    private EditText etID;
    private EditText etTitle;
    private Spinner spCustomer;
    private ArrayAdapter  adapter;
    List<Customer> customerList = new ArrayList<Customer>();
    private String url= "https://rest.na2.netsuite.com/app/site/hosting/restlet.nl?script=126&deploy=1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            pd = ProgressDialog.show(MainActivity.this, "Loading.Please wait...", "Wait....", true);
            getCustomer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        etID = (EditText) findViewById(R.id.etID);
        final Button bGet = (Button) findViewById(R.id.bGet);
        final Button bUpdate = (Button) findViewById(R.id.bUpdate);
        final Button bNew = (Button) findViewById(R.id.bNew);
        final Button bPrint = (Button) findViewById(R.id.bPrint);
        etTitle = (EditText) findViewById(R.id.etTitle);
        spCustomer = (Spinner) findViewById(R.id.spCustomer);
        bGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    id = etID.getText().toString();
                    pd = ProgressDialog.show(MainActivity.this, "Loading.Please wait...", "Wait....", true);
                    getRecord();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(id != null){
                        String title = etTitle.getText().toString();
                        Customer customer = (Customer) spCustomer.getSelectedItem();
                        updateRecord( id, title,customer.getId());
                    }else{
                        Toast toast = Toast.makeText(MainActivity.this,"Please get record",Toast.LENGTH_LONG);
                        toast.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        bNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,NewRecordActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        bPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id != null){
                    String title = etTitle.getText().toString();
                    Customer customer = (Customer) spCustomer.getSelectedItem();
                    Intent intent = new Intent(MainActivity.this,PrintPdfActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("title", title);
                    intent.putExtra("customer", customer.toString());
                    MainActivity.this.startActivity(intent);
                }else{
                    Toast toast = Toast.makeText(MainActivity.this,"Please get record",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    void updateRecord(String id, String title, String customerId) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"id\": "+id+",\"title\":\""+title+"\",\"customer\":"+customerId+"}");
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("authorization", "NLAuth nlauth_account=1763428, nlauth_email=hminhduc@icloud.com, nlauth_signature=Netsuite12345")
                .addHeader("content-type","application/json")
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {call.cancel();}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                Log.d("myResponse",myResponse);
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(MainActivity.this, "Update Complete", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        });
    }

    void getCustomer() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://rest.na2.netsuite.com/app/site/hosting/restlet.nl?script=128&deploy=1").newBuilder();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("authorization", "NLAuth nlauth_account=1763428, nlauth_email=hminhduc@icloud.com, nlauth_signature=Netsuite12345")
                .addHeader("content-type","application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {call.cancel();}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                //Log.d("myResponse",myResponse);
                try {
                    JSONArray jsonArray = new JSONArray(myResponse);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject explrObject = jsonArray.getJSONObject(i);
                        String id = explrObject.getString("id");
                        JSONObject valuesObject = explrObject.getJSONObject("values");
                        Customer customer = new Customer(id, valuesObject.getString("entityid"),valuesObject.getString("companyname"), valuesObject.getString("firstName"), valuesObject.getString("lastName"), valuesObject.getString("middleName"));
                        customerList.add(customer);
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            adapter = new CustomerAdapter(MainActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    customerList);
                            spCustomer.setAdapter(adapter);
                            spCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    Customer customer = (Customer)spCustomer.getSelectedItem();
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                            Intent intent = getIntent();
                            Bundle extras = getIntent().getExtras();
                            if (extras != null) {
                                String myResponse = extras.getString("myResponse");
                                try {
                                    JSONObject jsonResponse = new JSONObject(myResponse);
                                    if(jsonResponse.has("error")) {
                                        JSONObject errorResponse = jsonResponse.getJSONObject("error");
                                        // get the value and do something with it
                                        String message = errorResponse.getString("message");
                                        JSONObject jsonMessage = new JSONObject(message);
                                        Toast toast = Toast.makeText(MainActivity.this,jsonMessage.getString("message"),Toast.LENGTH_LONG);
                                        toast.show();
                                        etTitle.setText("");
                                    }else{
                                        id = jsonResponse.getString("id");
                                        etID.setText(id);
                                        JSONObject fields = jsonResponse.getJSONObject("fields");
                                        etTitle.setText(fields.getString("custrecord_title_test"));
                                        final String customerid = fields.getString("custrecord_customer");
                                        for(int i = 0; i < customerList.size(); i++){
                                            if(customerid.equals(customerList.get(i).getId()))
                                                spCustomer.setSelection(i);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void getRecord() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("id", id);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("authorization", "NLAuth nlauth_account=1763428, nlauth_email=hminhduc@icloud.com, nlauth_signature=Netsuite12345")
                .addHeader("content-type","application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {call.cancel();}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        //Log.d("myResponse",myResponse);
                        try {
                            JSONObject jsonResponse = new JSONObject(myResponse);
                            if(jsonResponse.has("error")) {
                                JSONObject errorResponse = jsonResponse.getJSONObject("error");
                                // get the value and do something with it
                                String message = errorResponse.getString("message");
                                JSONObject jsonMessage = new JSONObject(message);
                                Toast toast = Toast.makeText(MainActivity.this,jsonMessage.getString("message"),Toast.LENGTH_LONG);
                                toast.show();
                                etTitle.setText("");
                            }else{
                                JSONObject fields = jsonResponse.getJSONObject("fields");
                                etTitle.setText(fields.getString("custrecord_title_test"));
                                final String customerid = fields.getString("custrecord_customer");
                                for(int i = 0; i < customerList.size(); i++){
                                    if(customerid.equals(customerList.get(i).getId()))
                                        spCustomer.setSelection(i);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }
}

