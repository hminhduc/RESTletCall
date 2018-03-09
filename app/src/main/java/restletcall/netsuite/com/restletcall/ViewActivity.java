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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity {

    //private DrawerLayout drawerLayout;
    List<Customer> customerList = new ArrayList<Customer>();
    private ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        final Button bEdit = (Button) findViewById(R.id.bEdit);
        final Button bPrint = (Button) findViewById(R.id.bPrint);
        final TextView tvCustomer = (TextView) findViewById(R.id.tvCustomer);
        final TextView tvDate = (TextView) findViewById(R.id.tvDate);
        Bundle extras = getIntent().getExtras();
        tvCustomer.setText(extras.getString("customer"));
        tvDate.setText(extras.getString("date"));
        String myResponse = extras.getString("myResponse");
        Log.d("myResponse",extras.getString("myResponse"));
        try{
            JSONArray responseArray = new JSONArray(myResponse);
            for(int i = 0; i < responseArray.length(); i++){
                JSONObject responseObject = responseArray.getJSONObject(i);
                JSONObject rentalItems = responseObject.getJSONObject("rental_items");
                JSONObject itemvalue = rentalItems.getJSONObject("values");
                TableLayout tl = (TableLayout)findViewById(R.id.tlList);
                TableRow row = (TableRow) LayoutInflater.from(ViewActivity.this).inflate(R.layout.view_row, null);
                TextView tvItemNo = (TextView) row.findViewById(R.id.tvItemNo);
                TextView tvItemName = (TextView) row.findViewById(R.id.tvItemName);
                TextView tvName = (TextView) row.findViewById(R.id.tvName);
                TextView tvType = (TextView) row.findViewById(R.id.tvType);
                TextView tvUnitPrice = (TextView) row.findViewById(R.id.tvUnitPrice);
                TextView tvCounterOld = (TextView) row.findViewById(R.id.tvCounterOld);
                TextView tvCounter = (TextView) row.findViewById(R.id.tvCounter);
                TextView tvDifference = (TextView) row.findViewById(R.id.tvDifference);
                TextView tvAmount = (TextView) row.findViewById(R.id.tvAmount);
                TextView tvMemo = (TextView) row.findViewById(R.id.tvMemo);
                tvItemNo.setText(itemvalue.getString("custrecord_nid_rental_setting_no"));
                JSONArray items = itemvalue.getJSONArray("custrecord_nid_rental_item_name");
                JSONObject item = items.getJSONObject(0);
                tvItemName.setText(item.getString("text"));
                tvName.setText(itemvalue.getString("name"));
                tvType.setText(itemvalue.getString("custrecord_nid_rental_model"));
                tvUnitPrice.setText(itemvalue.getString("custrecord_nid_rental_unit_price"));
                JSONArray rentalSales = responseObject.getJSONArray("rental_sales");
                if(rentalSales.length() != 0 ){
                    JSONObject rentalSalesJSONObject = rentalSales.getJSONObject(0);
                    JSONObject value = rentalSalesJSONObject.getJSONObject("values");
                    tvCounterOld.setText(value.getString("custrecord_nid_rental_sales_counter"));
                    tvCounter.setText(value.getString("custrecord_nid_rental_sales_counter"));
                    tvDifference.setText("");
                    tvAmount.setText(value.getString("custrecord_nid_rental_sales_amount_d"));
                    tvMemo.setText(value.getString("custrecord_nid_rental_sales_memo"));
                }
                tl.addView(row);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        /*TableLayout tl = (TableLayout)findViewById(R.id.tlList);
        TableRow row = (TableRow) LayoutInflater.from(ViewActivity.this).inflate(R.layout.view_row, null);
        TextView tvItemNo = (TextView) row.findViewById(R.id.tvItemNo);
        TextView tvItemName = (TextView) row.findViewById(R.id.tvItemName);
        TextView tvName = (TextView) row.findViewById(R.id.tvName);
        TextView tvType = (TextView) row.findViewById(R.id.tvType);
        TextView tvUnitPrice = (TextView) row.findViewById(R.id.tvUnitPrice);
        TextView tvCounterOld = (TextView) row.findViewById(R.id.tvCounterOld);
        TextView tvCounter = (TextView) row.findViewById(R.id.tvCounter);
        TextView tvDifference = (TextView) row.findViewById(R.id.tvDifference);
        TextView tvMemo = (TextView) row.findViewById(R.id.tvMemo);
//        TableRow row = new TableRow(this);
//        TextView tv = new TextView(this);
//        tv.setText("This is text");

        tl.addView(row);*/
//        row.addView(tv);
        //configureNavigationDrawer();
        configureToolbar();

        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.setClass(ViewActivity.this, EditActivity.class);
                //Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                ViewActivity.this.startActivity(intent);
            }
        });

        bPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.setClass(ViewActivity.this, PrintPdfActivity.class);
                //Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                ViewActivity.this.startActivity(intent);
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
}
