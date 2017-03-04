package net.ddns.mipster.schooled.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import net.ddns.mipster.schooled.AnnouncementItemData;
import net.ddns.mipster.schooled.AnnouncementListAdapter;
import net.ddns.mipster.schooled.Activities.LoadingActivity;
import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.SchooledApplication;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Chen on 24/02/2017.
 */

public class AnnouncementFragment extends Fragment {

    private ArrayList<AnnouncementItemData> data;
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
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        listView = (ListView) getView().findViewById(R.id.listView);
        data = (ArrayList<AnnouncementItemData>)
                getArguments().getSerializable(SchooledApplication.ANNOUNCEMENT_DATA);

        swipeRefreshLayout.setColorSchemeColors(
                Color.RED,
                Color.GREEN,
                Color.BLUE
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GeneralDataTask().execute();
            }
        });

        AnnouncementListAdapter announcementListAdapter = new AnnouncementListAdapter(getContext(), data);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

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
    }

    private boolean isUrlValid(String url){
        try {
            new URL(url);
        } catch(java.net.MalformedURLException e) {
            return false;
        }
        return true;
    }

    class GeneralDataTask extends AsyncTask<Void,String,Void> {
        private ArrayList<AnnouncementItemData> announcementData;

        @Override
        protected Void doInBackground(Void... voids) {
            announcementData = LoadingActivity.parseAnnouncementData();

            if(announcementData == null)
                cancel(true);

            return null;
        }

        @Override
        protected void onCancelled() {
            Snackbar.make(getView(),
                    "Internet connection error",Snackbar.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listView.setAdapter(new AnnouncementListAdapter(getContext(), announcementData));
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}