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
        //get from Intent
        Bundle extras = getIntent().getExtras();
        tvCustomer.setText(extras.getString("customer"));
        tvDate.setText(extras.getString("date"));
        String myResponse = extras.getString("myResponse");
        TableLayout tl = (TableLayout) findViewById(R.id.tlList);

        Log.d("ViewActivity myResponse", myResponse);
        try {
            JSONArray responseArray = new JSONArray(myResponse);
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject item = responseArray.getJSONObject(i);
                TableRow row = (TableRow) LayoutInflater.from(ViewActivity.this).inflate(R.layout.view_row, null);
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
                TextView tvUnitPrice = (TextView) row.findViewById(R.id.tvUnitPrice);
                tvUnitPrice.setText(item.getString("unit_price"));
                //前回カウンター
                TextView tvCounterOld = (TextView) row.findViewById(R.id.tvCounterOld);
                tvCounterOld.setText(item.getString("sales_counter_old"));
                //今回カウンター
                TextView tvCounter = (TextView) row.findViewById(R.id.tvCounter);
                tvCounter.setText(item.getString("sales_counter"));
                //カウンター差分
                int counterOld = new Integer(item.getString("sales_counter_old"));
                int counter = new Integer(item.getString("sales_counter"));
                int diff = Math.abs(counter - counterOld);
                TextView tvDiff = (TextView) row.findViewById(R.id.tvDifference);
                tvDiff.setText(new Integer(diff).toString());
                //金額
                String amount = item.getString("sales_counter_d");

                if(amount.length()>3){
                    amount = amount.substring(0, amount.length()-3);
                }
                TextView tvAmount = (TextView) row.findViewById(R.id.tvAmount);
                tvAmount.setText(new Integer(amount).toString());
                //メンテカウント
                TextView tvMemo = (TextView) row.findViewById(R.id.tvMemo);
                tvMemo.setText(item.getString("sales_memo"));

                tl.addView(row);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        configureToolbar();

        //編集
        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.setClass(ViewActivity.this, EditActivity.class);
                //Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                ViewActivity.this.startActivity(intent);
            }
        });

        //印刷
        bPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.setClass(ViewActivity.this, PrintPdfActivity.class);
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
}
