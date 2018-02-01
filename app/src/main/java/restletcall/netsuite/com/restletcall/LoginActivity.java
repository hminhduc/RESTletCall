package restletcall.netsuite.com.restletcall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final ImageView ivLogo = (ImageView) findViewById(R.id.ivLogo);
        SharedPreferences sharedPref = getSharedPreferences("my_data", MODE_PRIVATE);
        Toast.makeText(LoginActivity.this, sharedPref.getString("account",""), Toast.LENGTH_SHORT).show();
        final EditText etAccount = (EditText) findViewById(R.id.etAccount);
        etAccount.setText(sharedPref.getString("account",""));
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                LoginActivity.this.startActivity(intent);
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
}
