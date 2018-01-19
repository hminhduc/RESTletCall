package restletcall.netsuite.com.restletcall;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    String id;
    private ProgressDialog pd;
    private TextView tvName;

    private String url= "https://rest.na2.netsuite.com/app/site/hosting/restlet.nl?script=126&deploy=1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText etID = (EditText) findViewById(R.id.etID);
        final Button bGet = (Button) findViewById(R.id.bGet);
        tvName = (TextView) findViewById(R.id.tvName);
        TextView tvSublistLine = (TextView) findViewById(R.id.tvSublistLine);

        bGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    id = etID.getText().toString();
                    pd = ProgressDialog.show(MainActivity.this, "Loading.Please wait...", "Wait....", true);
                    run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void run() throws IOException {
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
                        Log.d("myResponse",myResponse);
                        try {
                            JSONObject jsonResponse = new JSONObject(myResponse);
                            if(jsonResponse.has("error")) {
                                JSONObject errorResponse = jsonResponse.getJSONObject("error");
                                // get the value and do something with it
                                String message = errorResponse.getString("message");
                                JSONObject jsonMessage = new JSONObject(message);
                                Toast toast = Toast.makeText(MainActivity.this,jsonMessage.getString("message"),Toast.LENGTH_LONG);
                                toast.show();
                                tvName.setText("");
                            }else{
                                String type = jsonResponse.getString("type");
                                JSONObject fields = jsonResponse.getJSONObject("fields");
                                tvName.setText(fields.getString("name"));
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

