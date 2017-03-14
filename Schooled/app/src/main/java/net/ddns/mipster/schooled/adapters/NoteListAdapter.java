package net.ddns.mipster.schooled.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.classes.NoteData;

import java.util.List;

/**
 * Created by Chen on 15/03/2017.
 */

public class NoteListAdapter extends BaseAdapter {

    private Context context;
    private List<NoteData> data;

    public NoteListAdapter(Context context, List<NoteData> data){
        this.context = context;
        this.data = data;
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

        text.setText(data.get(position).getText());

        return convertView;
    }
}