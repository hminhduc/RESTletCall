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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class CreateActivity extends AppCompatActivity {
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        configureToolbar();

//        final Button bAdd = (Button) findViewById(R.id.bAdd);
//        final Button bDelete = (Button) findViewById(R.id.bDelete);
        final Button bSave = (Button) findViewById(R.id.bSave);
        final Button bCancel = (Button) findViewById(R.id.bCancel);

        final TextView tvCustomer = (TextView) findViewById(R.id.tvCustomer);
        final TextView tvDate = (TextView) findViewById(R.id.tvDate);
        Bundle extras = getIntent().getExtras();
        tvCustomer.setText(extras.getString("customer"));
        tvDate.setText(extras.getString("date"));
        String myResponse = extras.getString("myResponse");
        final TableLayout tl = (TableLayout) findViewById(R.id.tlList);

        try {
            JSONArray responseArray = new JSONArray(myResponse);
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject item = responseArray.getJSONObject(i);
                TableRow row = (TableRow) LayoutInflater.from(CreateActivity.this).inflate(R.layout.edit_row, null);
                //No.
                TextView tvItemNo = (TextView) row.findViewById(R.id.tvItemNo);
                tvItemNo.setText(item.getString("setting_no"));
                //物件名
                TextView tvItemName = (TextView) row.findViewById(R.id.tvItemName);
                tvItemName.setText(item.getString("item_name"));
                //種別
                TextView tvType = (TextView) row.findViewById(R.id.tvType);
                tvType.setText(item.getString("type"));
                //使用料金
                final TextView tvUnitPrice = (TextView) row.findViewById(R.id.tvUnitPrice);
                tvUnitPrice.setText(item.getString("unit_price"));
                //今回カウンター
                final EditText etCounter = (EditText) row.findViewById(R.id.etCounter);
                //カウンター差分
                final TextView tvDifference = (TextView) row.findViewById(R.id.tvDifference);
                //前回カウンター
                final TextView tvCounterOld = (TextView) row.findViewById(R.id.tvCounterOld);
                tvCounterOld.setText(item.getString("sales_counter_old"));
                //金額
                final TextView etAmount = (TextView) row.findViewById(R.id.etAmount);
                //メンテカウント

                etCounter.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        //使用料金
                        int use_amount = Integer.parseInt(tvUnitPrice.getText().toString());
                        int counter_old;
                        if (tvCounterOld.getText().toString().isEmpty()) {
                            counter_old = 0;
                        } else {
                            counter_old = Integer.parseInt(tvCounterOld.getText().toString());
                        }

                        int counter;
                        //NumberFormatException: For input string: ""
                        if (etCounter.getText().toString().isEmpty()) {
                            counter = 0;
                        } else {
                            counter = Integer.parseInt(etCounter.getText().toString());
                        }
                        //カウンター差分
                        int difference = counter_old - counter;
                        if (difference < 0) {
                            difference = difference * -1;
                        }
                        tvDifference.setText(new Integer(difference).toString());
                        //金額＝使用料金＊カウンタ差分
                        int amount = difference * use_amount;
                        etAmount.setText(new Integer(amount).toString());
                    }
                });
                tl.addView(row);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.setClass(CreateActivity.this, SelectActivity.class);
                CreateActivity.this.startActivity(intent);
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                String date_sales = extras.getString("date");
                String myResponse = extras.getString("myResponse");
                Log.d("CreateActivity Save", myResponse);
                pd = ProgressDialog.show(CreateActivity.this, "データ読み込み中......", "しばらくお待ちください。", true);
                try {
                    JSONArray responseArray = new JSONArray(myResponse);
                    for (int i = 1; i < tl.getChildCount(); i++) {
                        View tlChild = tl.getChildAt(i);
                        if (tlChild instanceof TableRow) {
                            JSONObject responseObject = responseArray.getJSONObject(i - 1);
                            TableRow row = (TableRow) tlChild;
                            EditText etCounter = (EditText) row.findViewById(R.id.etCounter);
                            TextView tvDifference = (TextView) row.findViewById(R.id.tvDifference);
                            EditText etAmount = (EditText) row.findViewById(R.id.etAmount);
                            EditText etMemo = (EditText) row.findViewById(R.id.etMemo);
                            //set for rental_sales
//                            JSONArray rentalSales = responseObject.getJSONArray("rental_sales");
//                            JSONObject value = rentalSales.getJSONObject("values");
//                            value.put("custrecord_nid_rental_sales_counter", etCounter.getText().toString());
                            //今回カウンター
                            responseObject.put("sales_counter", etCounter.getText().toString());
                            //カウンター差分
                            responseObject.put("sales_diff", tvDifference.getText().toString());
                            //金額
                            responseObject.put("sales_counter_d", etAmount.getText().toString());
                            //メンテカウンター
                            responseObject.put("sales_memo", etMemo.getText().toString());
                            responseObject.put("date_sales", date_sales);
                            //loginId
                            SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
                            responseObject.put("loginId", sharedPref.getString("entityid",""));
                            //value.put("custrecord_nid_rental_sales_memo", etDifference.getText().toString());
//                            value.put("custrecord_nid_rental_sales_amount_d", etAmount.getText().toString());
//                            value.put("custrecord_nid_rental_sales_memo", etMemo.getText().toString());

                        }
                    }
                    //Connect to server
                    SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
                    String url = sharedPref.getString("url", "https://rest.netsuite.com");
                    url = url + "/app/site/hosting/restlet.nl?script=";
//                    String url =  "https://4882653.restlets.api.netsuite.com/app/site/hosting/restlet.nl";
                    String account = sharedPref.getString("account", "4882653_SB1");
                    String email = sharedPref.getString("email", "rest.user@nidlaundry.jp");
                    String sign = sharedPref.getString("sign", "Netsuite1234567");
                    if (!isOnline()) {
                        Toast toast = Toast.makeText(CreateActivity.this, "ネットワークに接続されていません。", Toast.LENGTH_LONG);
                        toast.show();
                    } else if ((url.equals("https://")) || account.isEmpty() || email.isEmpty() || sign.isEmpty()) {
                        Toast toast = Toast.makeText(CreateActivity.this, "Please input setting", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        url = url + sharedPref.getString("retletscriptid", "99");
                        url = url + "&deploy=1";
//                        url = url + "?script=119&deploy=1";
                        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//                        OkHttpClient client = new OkHttpClient();
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(60, TimeUnit.SECONDS)
                                .readTimeout(60, TimeUnit.SECONDS)
                                .writeTimeout(60, TimeUnit.SECONDS)
                                .retryOnConnectionFailure(false) //<-- not necessary but useful!
                                .build();
                        MediaType mediaType = MediaType.parse("application/json");
                        JSONObject bodyJson = new JSONObject();
                        bodyJson.put("data", responseArray);
                        Log.d("body", bodyJson.toString());
                        RequestBody body = RequestBody.create(mediaType, bodyJson.toString());
                        Request request = new Request.Builder()
                                .url(urlBuilder.build().toString())
                                .addHeader("authorization", "NLAuth nlauth_account=" + account + ", nlauth_email=" + email + ", nlauth_signature=" + sign)
                                .addHeader("content-type", "application/json")
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                call.cancel();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String myResponse = response.body().string();
                                //Log.d("myResponse",myResponse);
                                CreateActivity.this.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        Toast toast = Toast.makeText(CreateActivity.this, "保存完了", Toast.LENGTH_LONG);
                                        toast.show();
                                        Intent intent = getIntent();
                                        intent.setClass(CreateActivity.this, ViewActivity.class);
                                        intent.putExtra("myResponse", myResponse);
                                        Log.d("myResponse", myResponse);
                                        CreateActivity.this.startActivity(intent);
                                    }
                                });
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        /*bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = (TableRow) LayoutInflater.from(CreateActivity.this).inflate(R.layout.add_row, null);
                EditText etItemNo = (EditText) row.findViewById(R.id.etItemNo);
                EditText etItemName = (EditText) row.findViewById(R.id.etItemName);
                EditText etName = (EditText) row.findViewById(R.id.etName);
                EditText etType = (EditText) row.findViewById(R.id.etType);
                EditText etUnitPrice = (EditText) row.findViewById(R.id.etUnitPrice);
                final EditText etCounterOld = (EditText) row.findViewById(R.id.etCounterOld);
                final EditText etCounter = (EditText) row.findViewById(R.id.etCounter);
                final EditText etDifference = (EditText) row.findViewById(R.id.etDifference);
                final EditText etAmount = (EditText) row.findViewById(R.id.etAmount);
                final EditText etMemo = (EditText) row.findViewById(R.id.etMemo);
                tl.addView(row);
            }
        });
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = tl.getChildCount();
                if(count != 1){
                    View tlChild = tl.getChildAt(count-1);
                    if (tlChild instanceof TableRow) {
                        TableRow row = (TableRow) tlChild;
                        tl.removeView(row);
                    }
                }
            }
        });*/
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
    }

    @Override
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
