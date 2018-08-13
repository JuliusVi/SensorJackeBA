package net.vinnen.sensorjackeba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Julius on 18.07.2018.
 */

/**
 * This class is an adapter for the list to display files in file select activity
 */
public class ListAdapter extends BaseAdapter {
    Context context;
    String[] data;
    private static LayoutInflater inflater = null;

    public ListAdapter(Context context, String[] data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);
        TextView filename = (TextView) vi.findViewById(R.id.filename);
        filename.setText(data[position].split("_")[0]);
        TextView timestamp = (TextView) vi.findViewById(R.id.timestamp);
        timestamp.setText(data[position].split("_")[1] + "    ");
        TextView filelenght = (TextView) vi.findViewById(R.id.filelength);
        filelenght.setText(data[position].split("_")[2]);
        return vi;
    }
}
