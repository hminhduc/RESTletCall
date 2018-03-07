package restletcall.netsuite.com.restletcall;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
        //final EditText etDate = (EditText) findViewById(R.id.etDate);
        //final Spinner spCustomer = (Spinner) findViewById(R.id.spCustomer);
        final Button bCancle = (Button) findViewById(R.id.bCancle);
        final TextView tvCustomer = (TextView) findViewById(R.id.tvCustomer);
        final TextView tvDate = (TextView) findViewById(R.id.tvDate);
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
                TableLayout tl = (TableLayout) findViewById(R.id.tlList);
                TableRow row = (TableRow) LayoutInflater.from(EditActivity.this).inflate(R.layout.edit_row, null);
                TextView tvItemNo = (TextView) row.findViewById(R.id.tvItemNo);
                TextView tvItemName = (TextView) row.findViewById(R.id.tvItemName);
                TextView tvName = (TextView) row.findViewById(R.id.tvName);
                TextView tvType = (TextView) row.findViewById(R.id.tvType);
                TextView tvUnitPrice = (TextView) row.findViewById(R.id.tvUnitPrice);
                TextView tvCounterOld = (TextView) row.findViewById(R.id.tvCounterOld);
                EditText etCounter = (EditText) row.findViewById(R.id.etCounter);
                EditText etDifference = (EditText) row.findViewById(R.id.etDifference);
                EditText etAmount = (EditText) row.findViewById(R.id.etAmount);
                EditText etMemo = (EditText) row.findViewById(R.id.etMemo);
                tvItemNo.setText(itemvalue.getString("custrecord_nid_rental_setting_no"));
                JSONArray items = itemvalue.getJSONArray("custrecord_nid_rental_item_name");
                JSONObject item = items.getJSONObject(0);
                tvItemName.setText(item.getString("text"));
                tvName.setText(itemvalue.getString("name"));
                tvType.setText(itemvalue.getString("custrecord_nid_rental_model"));
                tvUnitPrice.setText(itemvalue.getString("custrecord_nid_rental_unit_price"));
                JSONArray rentalSales = responseObject.getJSONArray("rental_sales");
                if (rentalSales.length() != 0) {
                    JSONObject rentalSalesJSONObject = rentalSales.getJSONObject(0);
                    JSONObject value = rentalSalesJSONObject.getJSONObject("values");
                    tvCounterOld.setText(value.getString("custrecord_nid_rental_sales_counter"));
                    etCounter.setText(value.getString("custrecord_nid_rental_sales_counter"));
                    etDifference.setText("");
                    etAmount.setText(value.getString("custrecord_nid_rental_sales_amount_d"));
                    etMemo.setText(value.getString("custrecord_nid_rental_sales_memo"));
                }
                tl.addView(row);
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
}
