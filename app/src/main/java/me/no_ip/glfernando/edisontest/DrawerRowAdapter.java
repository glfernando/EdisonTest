package me.no_ip.glfernando.edisontest;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by fernando on 2/18/15.
 */
public class DrawerRowAdapter extends BaseAdapter{
    String [] options;
    private Context context;

    public DrawerRowAdapter(Context context) {
        this.context = context;
        options = context.getResources().getStringArray(R.array.drawer_options);
    }

    @Override
    public int getCount() {
        return options.length;
    }

    @Override
    public Object getItem(int position) {
        return options[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_row, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.row_textView);
        text.setText(options[position]);

        return convertView;
    }
}
