package net.ddns.mipster.schooled.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.ddns.mipster.schooled.classes.HourTime;
import net.ddns.mipster.schooled.R;

import java.util.ArrayList;
import java.util.Arrays;


public class ScheduleListAdapter extends BaseAdapter {
    private ArrayList<String> data;
    private boolean isPre;
    private boolean useHours;
    private Context context;

    public ScheduleListAdapter(Context context, String[] data, boolean isPre, boolean useHours){
        this.data = new ArrayList<>(Arrays.asList(data));
        this.isPre = isPre;
        this.useHours = useHours;
        this.context = context;

        if(useHours)
            for(int i = 2; i < this.data.size() - 1; i+= 3)
                this.data.add((isPre ? 1 : 0) + i, "הפסקה");
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        convertView = mInflater.inflate(R.layout.schedule_list_item_centered, null);

        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView text = (TextView) convertView.findViewById(R.id.text);

        String numText = useHours ? (context.getResources().getStringArray(R.array.hours_time)[position + (isPre ? 0 : 1)]) :
                Integer.toString(position + (isPre ? 0 : 1));

        num.setText(numText);

        text.setText(data.get(position).isEmpty() ? "אין שיעור" : data.get(position));

        if(useHours)
            try {
                String[] arr = numText.split("\n");
                if (arr.length > 1 && HourTime.isNowInRange(HourTime.parse(arr[0]), HourTime.parse(arr[1])))
                    if (data.get(position).equals("הפסקה"))
                        convertView.setBackgroundColor(Color.parseColor("#33CCFF"));
                    else
                        convertView.setBackgroundColor(Color.parseColor("#FFFC7F"));
            }catch (Exception e){
                e.printStackTrace();
            }

        if(useHours)
            num.setTextSize(15);

        return convertView;
    }
}