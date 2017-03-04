package net.ddns.mipster.schooled.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.ScheduleListAdapter;
import net.ddns.mipster.schooled.SchooledApplication;
import net.ddns.mipster.schooled.Tuple;

import java.util.Arrays;

/**
 * Created by Chen on 24/02/2017.
 */

public class ScheduleFragment extends Fragment {

    private Spinner spinner;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String[][] excelData;
    private SharedPreferences sharedPref;

    public  ScheduleFragment(){}

    public static ScheduleFragment newInstance(String[][] excelData) {
        Bundle args = new Bundle();

        args.putSerializable(SchooledApplication.SCHEDULE_DATA, excelData);

        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduale, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        spinner = (Spinner) getView().findViewById(R.id.spinner);
        listView = (ListView) getView().findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        excelData = (String[][]) getArguments().getSerializable(SchooledApplication.SCHEDULE_DATA);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        String[] classes = new String[excelData.length - 1];
        for(int i = 0; i < classes.length; i++)
            classes[i] = excelData[i + 1][0];

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item_rtl);
        adapter.addAll(classes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Tuple<String[], Boolean> deleted = deleteExtra(excelData[i + 1]);

                ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(getContext(), deleted.getItem1(), deleted.getItem2());
                listView.setAdapter(scheduleListAdapter);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(SchooledApplication.SCHEDULE_DATA, i + 1);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner.setSelection(sharedPref.getInt(SchooledApplication.SCHEDULE_DATA, 1) - 1);
        Tuple<String[], Boolean> deleted = deleteExtra(excelData[sharedPref.getInt(SchooledApplication.SCHEDULE_DATA, 1)]);

        ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(getContext(), deleted.getItem1(), deleted.getItem2());
        listView.setAdapter(scheduleListAdapter);

        swipeRefreshLayout.setColorSchemeColors(
                Color.RED,
                Color.GREEN,
                Color.BLUE
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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
    }

    private Tuple<String[], Boolean> deleteExtra(String[] schedule){
        boolean isPre = false;
        int len;

        Log.d("deleteExtra", "Length: " + schedule.length);
        for (String str : schedule)
            Log.d("deleteExtra", str.isEmpty() ? "null" : str);
        Log.d("deleteExtra", "Done");

        if(!schedule[1].isEmpty())
            isPre = true;

        for (len = 0; len < schedule.length; len++) {
            Log.d("deleteExtra", schedule[schedule.length - len - 1].isEmpty() ? "null" : schedule[schedule.length - len - 1]);
            if (!schedule[schedule.length - len - 1].isEmpty())
                break;
        }
        Log.d("deleteExtra", "len before: " + len);
        len += isPre ? 1 : 2;
        Log.d("deleteExtra", "len after: " + len);

        return new Tuple<>(Arrays.copyOfRange(schedule,isPre ? 1 : 2, (isPre ? 1 : 2) + schedule.length - len), isPre);
    }
}