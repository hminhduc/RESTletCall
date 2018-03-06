package restletcall.netsuite.com.restletcall;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

//import com.reginald.editspinner.EditSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SelectActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private int selectItem;
    //private EditSpinner esCustomer;
    private ArrayAdapter  adapter;
    Customer customer;
    private String customerRespone = "";
    List<Customer> customerList = new ArrayList<Customer>();
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        final EditText etDate = (EditText) findViewById(R.id.etDate);
        final Button bSelect = (Button) findViewById(R.id.bSelect);
        final EditText etCustomer = (EditText) findViewById(R.id.etCustomer);
        Calendar newDate = Calendar.getInstance();
        etDate.setText(sdf.format(newDate.getTime()));
        etDate.setKeyListener(null);
        /*try {
            getCustomer();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        bSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String customerString = etCustomer.getText().toString();
                String dateString = etDate.getText().toString();
                if(customerString.isEmpty()) {
                    Toast toast = Toast.makeText(SelectActivity.this, "顧客IDを入力してください。", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
                    String url = sharedPref.getString("url","https://rest.netsuite.com/app/site/hosting/restlet.nl");
                    String account = sharedPref.getString("account","4882653_SB1");
                    String email = sharedPref.getString("email","hminhduc@icloud.com");
                    String sign = sharedPref.getString("sign","Netsuite12345");
                    url = url+"?script=99&deploy=1&customer_name="+customerString+"&collection_date="+dateString;
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(urlBuilder.build().toString())
                            .addHeader("authorization", "NLAuth nlauth_account="+account+", nlauth_email="+email+", nlauth_signature="+sign)
                            .addHeader("content-type","application/json")
                            .get()
                            .build();
                    pd = ProgressDialog.show(SelectActivity.this, "データ読み込み中......", "しばらくお待ちください。", true);
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            pd.dismiss();
                            Log.d("e", e.toString());
                            call.cancel();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String myResponse = response.body().string();
                            Log.d("myResponse",myResponse);
                            SelectActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    pd.dismiss();
                                    try {
                                        Object json = new JSONTokener(myResponse).nextValue();
                                        if (json instanceof JSONObject){
                                            JSONObject responseObject = new JSONObject(myResponse);
                                            JSONObject errorObject = responseObject.getJSONObject("error");
                                            Toast toast = Toast.makeText(SelectActivity.this, errorObject.getString("message"), Toast.LENGTH_LONG);
                                            toast.show();
                                        }else if (json instanceof JSONArray){
                                            JSONArray responseArray = new JSONArray(myResponse);
                                            if(responseArray.length() == 0){
                                                Toast toast = Toast.makeText(SelectActivity.this, "入力された顧客IDに契約データが存在しません。", Toast.LENGTH_LONG);
                                                toast.show();
                                            }else{
                                                String customerString = etCustomer.getText().toString();
                                                String dateString = etDate.getText().toString();
                                                Intent intent = new Intent(SelectActivity.this, ViewActivity.class);
                                                intent.putExtra("customer", customerString);
                                                intent.putExtra("date", dateString);
                                                intent.putExtra("myResponse", myResponse);
                                                SelectActivity.this.startActivity(intent);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast toast = Toast.makeText(SelectActivity.this, "Please change url setting", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Calendar newDate = Calendar.getInstance();
                    String dateString = etDate.getText().toString();
                    if(dateString.isEmpty()){
                        dateString = sdf.format(newDate.getTime());
                    }
                    String strArrtmp[]=dateString.split("/");
                    int intDay = Integer.parseInt(strArrtmp[2]);
                    int intMonth=Integer.parseInt(strArrtmp[1]) - 1;
                    int intYear =Integer.parseInt(strArrtmp[0]);
                    Log.d("date", dateString);
                    DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            Calendar c = Calendar.getInstance();
                            c.set(year, month, day, 0, 0);
                            etDate.setText(sdf.format(c.getTime()));
                        }
                    };

                    DatePickerDialog pic=new DatePickerDialog(
                            SelectActivity.this, callback, intYear, intMonth, intDay);
                    pic.show();
                }
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newDate = Calendar.getInstance();
                String dateString = etDate.getText().toString();
                if(dateString.isEmpty()){
                    dateString = sdf.format(newDate.getTime());
                }
                String strArrtmp[]=dateString.split("/");
                int intDay = Integer.parseInt(strArrtmp[2]);
                int intMonth=Integer.parseInt(strArrtmp[1]) - 1;
                int intYear =Integer.parseInt(strArrtmp[0]);
                Log.d("date", dateString);
                DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day, 0, 0);
                        etDate.setText(sdf.format(c.getTime()));
                    }
                };

                DatePickerDialog pic=new DatePickerDialog(
                        SelectActivity.this, callback, intYear, intMonth, intDay);
                pic.show();
            }
        });
        //configureNavigationDrawer();
        configureToolbar();
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
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_action_menu_white);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.menu_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Fragment f = null;
                int itemId = menuItem.getItemId();

                if (itemId == R.id.refresh) {
                    Log.d("click","stop");
                } else if (itemId == R.id.stop) {
                    Log.d("click","stop");
                }

                if (f != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, f);
                    transaction.commit();
                    drawerLayout.closeDrawers();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch(itemId) {
            // Android home
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            // manage other entries if you have it ...
        }

        return true;
    }*/

    /*void getCustomer() throws IOException {
        SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
        String url = sharedPref.getString("url","https://rest.netsuite.com/app/site/hosting/restlet.nl");
        String account = sharedPref.getString("account","4882653_SB1");
        String email = sharedPref.getString("email","hminhduc@icloud.com");
        String sign = sharedPref.getString("sign","Netsuite12345");
        if(!isOnline()) {
            Toast toast = Toast.makeText(SelectActivity.this, "ネットワークに接続されていません。", Toast.LENGTH_LONG);
            toast .show();
        }else {
            url = url+"?script=90&deploy=1";
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urlBuilder.build().toString())
                    .addHeader("authorization", "NLAuth nlauth_account="+account+", nlauth_email="+email+", nlauth_signature="+sign)
                    .addHeader("content-type","application/json")
                    .get()
                    .build();

            pd = ProgressDialog.show(SelectActivity.this, "データ読み込み中......", "しばらくお待ちください。", true);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {call.cancel();}

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    pd.dismiss();
                    final String myResponse = response.body().string();
                    //Log.d("myResponse",myResponse);
                    try {
                        customerRespone = myResponse;
                        JSONArray jsonArray = new JSONArray(myResponse);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject explrObject = jsonArray.getJSONObject(i);
                            String id = explrObject.getString("id");
                            JSONObject valuesObject = explrObject.getJSONObject("values");
                            Customer customer = new Customer(id, valuesObject.getString("entityid"),valuesObject.getString("companyname"), valuesObject.getString("firstName"), valuesObject.getString("lastName"), valuesObject.getString("middleName"));
                            customerList.add(customer);
                        }
                        SelectActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                adapter = new CustomerAdapter(SelectActivity.this,
                                        android.R.layout.simple_spinner_item,
                                        customerList);
                                *//*spCustomer.setAdapter(adapter);
                                spCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        Customer customer = (Customer)spCustomer.getSelectedItem();
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });*//*
                                *//*esCustomer.setAdapter(adapter);
                                esCustomer.setDropDownDrawableSpacing(50);
                                esCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Customer customer = customerList.get(position);
                                        selectItem = position;
                                    }
                                });*//*

                                iacCustomer.setAdapter(adapter);

                                // Clear autocomplete
                                iacCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        customer = customerList.get(position);
                                        //selectItem = position;
                                    }
                                });

                                iacCustomer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        iacCustomer.setText("");
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
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }*/
    @Override
    public void onBackPressed() {

    }
}
