package restletcall.netsuite.com.restletcall;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kyuubi on 2018/01/23.
 */

public class CustomerAdapter extends ArrayAdapter<Customer> {

    private List<Customer> objects;
    private Context context;

    public CustomerAdapter(@NonNull Context context, int resource, @NonNull List<Customer> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public int getCount(){
        return objects.size();
    }

    @Override
    public Customer getItem(int position){
        return objects.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(objects.get(position).toString());
        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(objects.get(position).toString());
        return label;
    }
}
