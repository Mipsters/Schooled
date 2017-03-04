package net.ddns.mipster.schooled;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ScheduleListAdapter extends BaseAdapter {
    private String[] data;
    private boolean isPre;
    private LayoutInflater mInflater;

    public ScheduleListAdapter(Context context, String[] data, boolean isPre){
        this.data = data;
        this.isPre = isPre;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.schedule_list_item_centered, null);

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView text = (TextView) convertView.findViewById(R.id.text);

        num.setText(Integer.toString(position + (isPre ? 0 : 1)));
        text.setText(data[position].isEmpty() ? "אין שיעור" : data[position]);

        return convertView;
    }
}