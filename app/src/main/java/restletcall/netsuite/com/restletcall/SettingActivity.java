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
        final Button bSave = (Button) findViewById(R.id.bSave);
        final Button bCancle = (Button) findViewById(R.id.bCancle);

        SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
        sharedPref.getString("account","");
        etAccount.setText(sharedPref.getString("account",""));
        etEmail.setText(sharedPref.getString("email",""));
        etSign.setText(sharedPref.getString("sign",""));
        etUrl.setText(sharedPref.getString("url","https://rest.netsuite.com/app/site/hosting/restlet.nl"));

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = etAccount.getText().toString();
                String email = etEmail.getText().toString();
                String sign = etSign.getText().toString();
                String url = etUrl.getText().toString();

                SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("account", account);
                editor.putString("email", email);
                editor.putString("sign", sign);
                editor.putString("url", url);
                editor.apply();
                Log.d("accountVal", sharedPref.getString("account",""));
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
