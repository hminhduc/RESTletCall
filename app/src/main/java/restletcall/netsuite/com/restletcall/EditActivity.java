package restletcall.netsuite.com.restletcall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

public class EditActivity extends AppCompatActivity {

    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //final EditText etDate = (EditText) findViewById(R.id.etDate);
        //final Spinner spCustomer = (Spinner) findViewById(R.id.spCustomer);
        final Button bCancle = (Button) findViewById(R.id.bCancle);
        final Button bSave = (Button) findViewById(R.id.bSave);
        final TextView tvCustomer = (TextView) findViewById(R.id.tvCustomer);
        final TextView tvDate = (TextView) findViewById(R.id.tvDate);
        final TableLayout tl = (TableLayout) findViewById(R.id.tlList);
        configureToolbar();

        //Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        String myResponse = extras.getString("myResponse");
        Log.d("myResponse",extras.getString("myResponse"));
        try {
            JSONArray responseArray = new JSONArray(myResponse);
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject responseObject = responseArray.getJSONObject(i);
                JSONObject rentalItems = responseObject.getJSONObject("rental_items");
                JSONObject itemvalue = rentalItems.getJSONObject("values");
                TableRow row = (TableRow) LayoutInflater.from(EditActivity.this).inflate(R.layout.edit_row, null);
                TextView tvItemNo = (TextView) row.findViewById(R.id.tvItemNo);
                TextView tvItemName = (TextView) row.findViewById(R.id.tvItemName);
                TextView tvName = (TextView) row.findViewById(R.id.tvName);
                TextView tvType = (TextView) row.findViewById(R.id.tvType);
                TextView tvUnitPrice = (TextView) row.findViewById(R.id.tvUnitPrice);
                final TextView tvCounterOld = (TextView) row.findViewById(R.id.tvCounterOld);
                final EditText etCounter = (EditText) row.findViewById(R.id.etCounter);
                final EditText etDifference = (EditText) row.findViewById(R.id.etDifference);
                final EditText etAmount = (EditText) row.findViewById(R.id.etAmount);
                final EditText etMemo = (EditText) row.findViewById(R.id.etMemo);
                tvItemNo.setText(itemvalue.getString("custrecord_nid_rental_setting_no"));
                JSONArray items = itemvalue.getJSONArray("custrecord_nid_rental_item_name");
                if(items.length() != 0){
                    JSONObject item = items.getJSONObject(0);
                    tvItemName.setText(item.getString("text"));
                }
                tvName.setText(itemvalue.getString("name"));
                JSONArray types = itemvalue.getJSONArray("custrecord_nid_rental_type");
                if(types.length() != 0){
                    JSONObject type = types.getJSONObject(0);
                    tvType.setText(type.getString("text"));
                }
                tvUnitPrice.setText(itemvalue.getString("custrecord_nid_rental_unit_price"));
                JSONObject rentalSales = responseObject.getJSONObject("rental_sales");
                JSONObject value = rentalSales.getJSONObject("values");
                tvCounterOld.setText(value.getString("custrecord_nid_rental_sales_counter_old"));
                etCounter.setText(value.getString("custrecord_nid_rental_sales_counter"));
                if(!tvCounterOld.getText().toString().isEmpty()){
                    int counted_old = Integer.parseInt(value.getString("custrecord_nid_rental_sales_counter_old"));
                    if(!tvCounterOld.getText().toString().isEmpty()){
                        int counter = Integer.parseInt(value.getString("custrecord_nid_rental_sales_counter"));
                        int difference = counted_old - counter;
                        new Integer(difference).toString();
                        etDifference.setText(new Integer(difference).toString());
                    }
                }
                etAmount.setText(value.getString("custrecord_nid_rental_sales_amount_d"));
                etMemo.setText(value.getString("custrecord_nid_rental_sales_memo"));
                tl.addView(row);

                etCounter.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!tvCounterOld.getText().toString().isEmpty()){
                            int counted_old = Integer.parseInt(tvCounterOld.getText().toString());
                            if(!tvCounterOld.getText().toString().isEmpty()){
                                int counter = Integer.parseInt(etCounter.getText().toString());
                                int difference = counted_old - counter;
                                new Integer(difference).toString();
                                etDifference.setText(new Integer(difference).toString());
                            }
                        }
                    }
                });
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        /*String customerRespone = extras.getString("customerRespone");
        Integer position = extras.getInt("selectitem");
        if(!customerRespone.isEmpty()){
            try {
                JSONArray jsonArray = new JSONArray(customerRespone);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject explrObject = jsonArray.getJSONObject(i);
                    String id = explrObject.getString("id");
                    JSONObject valuesObject = explrObject.getJSONObject("values");
                    Customer customer = new Customer(id, valuesObject.getString("entityid"),valuesObject.getString("companyname"), valuesObject.getString("firstName"), valuesObject.getString("lastName"), valuesObject.getString("middleName"));
                    customerList.add(customer);
                }
                adapter = new CustomerAdapter(EditActivity.this,
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
                spCustomer.setSelection(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
        tvCustomer.setText(extras.getString("customer"));
        tvDate.setText(extras.getString("date"));

        bCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.setClass(EditActivity.this, ViewActivity.class);
                EditActivity.this.startActivity(intent);
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = getIntent().getExtras();
                String myResponse = extras.getString("myResponse");
                pd = ProgressDialog.show(EditActivity.this, "データ読み込み中......", "しばらくお待ちください。", true);
                try {
                    JSONArray responseArray = new JSONArray(myResponse);
                    for(int i = 1; i < tl.getChildCount() ; i++){
                        View tlChild = tl.getChildAt(i);
                        if (tlChild instanceof TableRow) {
                            JSONObject responseObject = responseArray.getJSONObject( i -1 );
                            TableRow row = (TableRow) tlChild;
                            EditText etCounter = (EditText) row.findViewById(R.id.etCounter);
                            EditText etDifference = (EditText) row.findViewById(R.id.etDifference);
                            EditText etAmount = (EditText) row.findViewById(R.id.etAmount);
                            EditText etMemo = (EditText) row.findViewById(R.id.etMemo);
                            JSONObject rentalSales = responseObject.getJSONObject("rental_sales");
                            JSONObject value = rentalSales.getJSONObject("values");
                            value.put("custrecord_nid_rental_sales_counter", etCounter.getText().toString());
                            //value.put("custrecord_nid_rental_sales_memo", etDifference.getText().toString());
                            value.put("custrecord_nid_rental_sales_amount_d", etAmount.getText().toString());
                            value.put("custrecord_nid_rental_sales_memo", etMemo.getText().toString());

                        }
                    }
                    SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
                    String url = sharedPref.getString("url","https://rest.netsuite.com/app/site/hosting/restlet.nl");
                    String account = sharedPref.getString("account","4882653_SB1");
                    String email = sharedPref.getString("email","hminhduc@icloud.com");
                    String sign = sharedPref.getString("sign","Netsuite12345");
                    if(!isOnline()) {
                        Toast toast = Toast.makeText(EditActivity.this, "ネットワークに接続されていません。", Toast.LENGTH_LONG);
                        toast .show();
                    }else if((url.equals("https://"))|| account.isEmpty() || email.isEmpty() || sign.isEmpty()){
                        Toast toast = Toast.makeText(EditActivity.this, "Please input setting", Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        url = url+"?script=99&deploy=1";
                        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
                        OkHttpClient client = new OkHttpClient();
                        MediaType mediaType = MediaType.parse("application/json");
                        JSONObject bodyJson = new JSONObject();
                        bodyJson.put("data", responseArray);
                        Log.d("body", bodyJson.toString());
                        RequestBody body = RequestBody.create(mediaType, bodyJson.toString());
                        Request request = new Request.Builder()
                                .url(urlBuilder.build().toString())
                                .addHeader("authorization", "NLAuth nlauth_account="+account+", nlauth_email="+email+", nlauth_signature="+sign)
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
                                EditActivity.this.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        Toast toast = Toast.makeText(EditActivity.this, "保存完了", Toast.LENGTH_LONG);
                                        toast.show();
                                        Intent intent = getIntent();
                                        intent.setClass(EditActivity.this, ViewActivity.class);
                                        intent.putExtra("myResponse", myResponse);
                                        Log.d("myResponse", myResponse);
                                        EditActivity.this.startActivity(intent);
                                    }
                                });
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*Intent intent = getIntent();
                intent.setClass(EditActivity.this, ViewActivity.class);
                EditActivity.this.startActivity(intent);*/
            }
        });
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, SelectActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;
            case R.id.action_logout:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
