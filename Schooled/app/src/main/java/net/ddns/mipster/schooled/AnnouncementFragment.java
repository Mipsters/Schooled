package net.ddns.mipster.schooled;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

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

        ArrayList<AnnouncementItemData> data = (ArrayList<AnnouncementItemData>)
                getArguments().getSerializable(SchooledApplication.ANNOUNCEMENT_DATA);

        AnnouncementListAdapter announcementListAdapter = new AnnouncementListAdapter(getContext(), data);

        listView.setAdapter(announcementListAdapter);

        return rootView;
    }
}
