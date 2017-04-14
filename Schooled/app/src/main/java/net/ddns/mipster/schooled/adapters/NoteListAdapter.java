package net.ddns.mipster.schooled.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.SchooledApplication;
import net.ddns.mipster.schooled.classes.NoteData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 15/03/2017.
 */

public class NoteListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NoteData> data;
    private String[] classes;

    public NoteListAdapter(Context context, ArrayList<NoteData> data, String[] classes){
        this.context = context;
        this.data = data;
        this.classes = classes;
    }

    public NoteListAdapter(Context context, Cursor data, String[] classes){
        this.context = context;
        this.classes = classes;
        this.data = new ArrayList<>();

        while (data.moveToNext())
            this.data.add(new NoteData(data.getInt(0),data.getInt(1),data.getInt(2),data.getInt(3),data.getString(4)));
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.note_list_item, null);

        TextView text = (TextView) convertView.findViewById(R.id.text),
                 info = (TextView) convertView.findViewById(R.id.info);

        NoteData data = this.data.get(position);

        text.setText(data.getText());

        String classSelect = "ההודעה מופיעה מתחת לכיתות: ";

        for(int i = data.getX1(); i < data.getX2(); i++)
            classSelect += classes[i - 1] + ", ";
        classSelect += classes[data.getX2() - 1];


        if(data.getY1() != data.getY2()) {
            classSelect += "\nובין השעות ";
            classSelect += (data.getY1() - SchooledApplication.FIRST_LINE - 1)
                    + " ל " +
                    (data.getY2() - SchooledApplication.FIRST_LINE - 1);
        } else
            classSelect += "\nבשעה " + (data.getY1() - SchooledApplication.FIRST_LINE - 1);

        info.setText(classSelect);

        return convertView;
    }
}