package restletcall.netsuite.com.restletcall;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

public class CreateActivityOld extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        configureToolbar();

        final Button bAdd = (Button) findViewById(R.id.bAdd);
        final Button bDelete = (Button) findViewById(R.id.bDelete);
        final TableLayout tl = (TableLayout) findViewById(R.id.tlList);

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = (TableRow) LayoutInflater.from(CreateActivityOld.this).inflate(R.layout.add_row, null);
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
