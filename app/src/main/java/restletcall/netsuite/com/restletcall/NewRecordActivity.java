package restletcall.netsuite.com.restletcall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewRecordActivity extends AppCompatActivity {

    private ProgressDialog pd;
    private EditText etTitle;
    private Spinner spCustomer;
    private ArrayAdapter adapter;
    List<Customer> customerList = new ArrayList<Customer>();
    private String url= "https://rest.na2.netsuite.com/app/site/hosting/restlet.nl?script=126&deploy=1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        try {
            pd = ProgressDialog.show(NewRecordActivity.this, "Loading.Please wait...", "Wait....", true);
            getCustomer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Button bNew = (Button) findViewById(R.id.bCreate);
        etTitle = (EditText) findViewById(R.id.etTitle);
        spCustomer = (Spinner) findViewById(R.id.spCustomer);

        bNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pd = ProgressDialog.show(NewRecordActivity.this, "Loading.Please wait...", "Wait....", true);
                    String title = etTitle.getText().toString();
                    Customer customer = (Customer) spCustomer.getSelectedItem();
                    createRecord( title,customer.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                    NewRecordActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            adapter = new CustomerAdapter(NewRecordActivity.this,
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
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void createRecord(String title, String customerId) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"title\":\""+title+"\",\"customer\":"+customerId+"}");
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("authorization", "NLAuth nlauth_account=1763428, nlauth_email=hminhduc@icloud.com, nlauth_signature=Netsuite12345")
                .addHeader("content-type","application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {call.cancel();}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                //Log.d("myResponse",myResponse);
                NewRecordActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast toast = Toast.makeText(NewRecordActivity.this, "Create Complete", Toast.LENGTH_LONG);
                        toast.show();
                        Intent intent = new Intent(NewRecordActivity.this,MainActivity.class);
                        intent.putExtra("myResponse", myResponse);
                        NewRecordActivity.this.startActivity(intent);
                    }
                });
            }
        });
    }
}
