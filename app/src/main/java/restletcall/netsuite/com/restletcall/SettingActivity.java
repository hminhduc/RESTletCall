package restletcall.netsuite.com.restletcall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final EditText etAccount = (EditText) findViewById(R.id.etAccount);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etSign = (EditText) findViewById(R.id.etSign);
        final EditText etUrl = (EditText) findViewById(R.id.etUrl);
        final EditText etVersion = (EditText) findViewById(R.id.etVersion);
        final EditText etLoginScriptId = (EditText) findViewById(R.id.etLoginScriptId);
        final EditText etRetletScriptId = (EditText) findViewById(R.id.etRetletScriptId);

        final Button bSave = (Button) findViewById(R.id.bSave);
        final Button bCancle = (Button) findViewById(R.id.bCancle);

        SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
        etAccount.setText(sharedPref.getString("account","4882653_SB1"));
        etEmail.setText(sharedPref.getString("email","rest.user@nidlaundry.jp"));
        etSign.setText(sharedPref.getString("sign","Netsuite1234567"));
        etUrl.setText(sharedPref.getString("url","https://rest.netsuite.com"));
        etLoginScriptId.setText(sharedPref.getString("loginscriptid","89"));
        etRetletScriptId.setText(sharedPref.getString("retletscriptid","99"));
//        etUrl.setText(sharedPref.getString("url","https://rest.netsuite.com/app/site/hosting/restlet.nl?script=89"));
//        etUrl.setText(sharedPref.getString("url","https://4882653-sb1.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=89"));
        etVersion.setText(sharedPref.getString("version","0.0.3"));

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = etAccount.getText().toString();
                String email = etEmail.getText().toString();
                String sign = etSign.getText().toString();
                String url = etUrl.getText().toString();
                String loginScriptId = etLoginScriptId.getText().toString();
                String retletScriptId = etRetletScriptId.getText().toString();
                String version = etVersion.getText().toString();

                SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("account", account);
                editor.putString("email", email);
                editor.putString("sign", sign);
                editor.putString("url", url);
                editor.putString("loginscriptid", loginScriptId);
                editor.putString("retletscriptid", retletScriptId);
                editor.putString("version", version);
                editor.apply();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                SettingActivity.this.startActivity(intent);
            }
        });

        bCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                SettingActivity.this.startActivity(intent);
            }
        });
    }
}
