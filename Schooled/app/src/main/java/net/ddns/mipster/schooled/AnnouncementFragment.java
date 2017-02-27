package net.ddns.mipster.schooled;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Chen on 24/02/2017.
 */

public class AnnouncementFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    public AnnouncementFragment(){}

    public static AnnouncementFragment newInstance(ArrayList<AnnouncementItemData> scheduleData) {
        Bundle args = new Bundle();

        args.putSerializable(SchooledApplication.ANNOUNCEMENT_DATA, scheduleData);

        AnnouncementFragment fragment = new AnnouncementFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_announcement, container, false);


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        listView = (ListView) rootView.findViewById(R.id.listView);

        final ArrayList<AnnouncementItemData> data = (ArrayList<AnnouncementItemData>)
                getArguments().getSerializable(SchooledApplication.ANNOUNCEMENT_DATA);

        AnnouncementListAdapter announcementListAdapter = new AnnouncementListAdapter(getContext(), data);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = data.get(i).getUrl();
                if(!url.isEmpty())
                    if(isUrlValid(url))
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    else if(isUrlValid("http://handasaim.co.il/" + url))
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://handasaim.co.il/" + url)));
                    else
                        Snackbar.make(view, "The web page \"" + url + "\" does not exist", Snackbar.LENGTH_LONG).show();
            }
        });

        listView.setAdapter(announcementListAdapter);

        return rootView;
    }

    private boolean isUrlValid(String url){
        try {
            new URL(url);
        } catch(java.net.MalformedURLException e) {
            return false;
        }
        return true;
    }
}
