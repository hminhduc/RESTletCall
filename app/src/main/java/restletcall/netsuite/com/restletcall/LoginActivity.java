package restletcall.netsuite.com.restletcall;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity  {
    int i = 0;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final ImageView ivLogo = (ImageView) findViewById(R.id.ivLogo);
        SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
        final EditText etAccount = (EditText) findViewById(R.id.etAccount);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        etAccount.setText(sharedPref.getString("entityid",""));
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String entityid = etAccount.getText().toString();
                String password = etPassword.getText().toString();
                SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("entityid", entityid);
                editor.apply();
                String url = sharedPref.getString("url","https://rest.netsuite.com/app/site/hosting/restlet.nl");
                String account = sharedPref.getString("account","4882653_SB1");
                String email = sharedPref.getString("email","hminhduc@icloud.com");
                String sign = sharedPref.getString("sign","Netsuite12345");
                if(!isOnline()) {
                    Toast toast = Toast.makeText(LoginActivity.this, "ネットワークに接続されていません。", Toast.LENGTH_LONG);
                    toast .show();
                }else if((url.equals("https://"))|| account.isEmpty() || email.isEmpty() || sign.isEmpty()){
                    Toast toast = Toast.makeText(LoginActivity.this, "Please input setting", Toast.LENGTH_LONG);
                    toast.show();
                }else if(entityid.isEmpty()){
                    Toast toast = Toast.makeText(LoginActivity.this, "従業員IDを入力してください。", Toast.LENGTH_LONG);
                    toast.show();
                }else if(password.isEmpty()){
                    Toast toast = Toast.makeText(LoginActivity.this, "パスワードを入力してください。", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    url = url+"?script=89&deploy=1&user_id="+entityid+"&mobile_pass="+password;
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(urlBuilder.build().toString())
                            .addHeader("authorization", "NLAuth nlauth_account="+account+", nlauth_email="+email+", nlauth_signature="+sign)
                            .addHeader("content-type","application/json")
                            .get()
                            .build();
                    pd = ProgressDialog.show(LoginActivity.this, "データ読み込み中......", "しばらくお待ちください。", true);
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            pd.dismiss();
                            call.cancel();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String myResponse = response.body().string();
                            Log.d("myResponse",myResponse);
                            LoginActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    pd.dismiss();
                                    try {
                                        Object json = new JSONTokener(myResponse).nextValue();
                                        if (json instanceof JSONObject){
                                            JSONObject responseObject = new JSONObject(myResponse);
                                            JSONObject errorObject = responseObject.getJSONObject("error");
                                            Toast toast = Toast.makeText(LoginActivity.this, errorObject.getString("message")+". Please change setting", Toast.LENGTH_LONG);
                                            toast.show();
                                        }else if (json instanceof JSONArray){
                                            JSONArray responseArray = new JSONArray(myResponse);
                                            if(responseArray.length() == 0){
                                                Toast toast = Toast.makeText(LoginActivity.this, "従業員IDまたはパスワードが正しくありません。", Toast.LENGTH_LONG);
                                                toast.show();
                                            }else{
                                                Intent intent = new Intent(LoginActivity.this,SelectActivity.class);
                                                LoginActivity.this.startActivity(intent);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast toast = Toast.makeText(LoginActivity.this, "Please change url setting", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        i = 0;
                    }
                };
                if(i == 1){
                    handler.postDelayed(runnable, 1000);
                }
                if(i == 5){
                    Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                    LoginActivity.this.startActivity(intent);
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {

    }
}
