package net.ddns.mipster.schooled;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chen on 24/02/2017.
 */

public class ScheduleFragment extends Fragment {

    public  ScheduleFragment(){}

    public static ScheduleFragment newInstance() {
        
        Bundle args = new Bundle();

        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduale, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("Schedule Fragment");

        return rootView;
    }
}