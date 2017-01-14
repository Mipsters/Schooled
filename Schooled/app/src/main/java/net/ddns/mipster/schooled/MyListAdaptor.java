package net.ddns.mipster.schooled;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.nodes.Element;

/**
 * Created by Tom on 13/01/2017.
 */

public class MyListAdaptor extends ArrayAdapter {
    private Context context;
    private int resource;
    private LayoutInflater inflater;
    private Element[] data;

    public MyListAdaptor(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.inflater = LayoutInflater.from(context);
        data = (Element[]) objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* create a new view of my layout and inflate it in the row */
        convertView = inflater.inflate( resource, null );

        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView text = (TextView) convertView.findViewById(R.id.text);

        date.setText(data[position].select("sup").first().text());

        title.setText(data[position].select("b").first().text());

        String txt = data[position].text().replace(data[position].select("b").first().text(), "").replace(data[position].select("sup").first().text(), "").replace("לחצ\\י לפתיחת הקובץ","").replace("למידע נוסף","");
        text.setText(txt);
        return convertView;
    }

    @Nullable
    @Override
    public Element getItem(int position) {
        return data[position];
    }
}
