package restletcall.netsuite.com.restletcall;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    List<Customer> customerList = new ArrayList<Customer>();
    private ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        final EditText etDate = (EditText) findViewById(R.id.etDate);
        final Spinner spCustomer = (Spinner) findViewById(R.id.spCustomer);
        final Button bCancle = (Button) findViewById(R.id.bCancle);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        String customerRespone = extras.getString("customerRespone");
        Integer position = extras.getInt("selectitem");
        Log.d("customerRespone", customerRespone);
        Log.d("selectitem", position+"");
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
        }
        etDate.setText(extras.getString("date"));

        bCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.setClass(EditActivity.this, MenuActivity.class);
                EditActivity.this.startActivity(intent);
            }
        });
    }
}
