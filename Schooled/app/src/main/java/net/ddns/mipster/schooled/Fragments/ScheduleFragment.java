package net.ddns.mipster.schooled.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.Adapters.ScheduleListAdapter;
import net.ddns.mipster.schooled.SchooledApplication;
import net.ddns.mipster.schooled.MyClasses.Tuple;

import java.util.Arrays;


public class ScheduleFragment extends Fragment {

    private TextView day;
    private Spinner spinner;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Switch aSwitch;
    private String[][] excelData;
    private String[] classes;
    private SharedPreferences sharedPref;
    private Tuple<String[], Boolean> deleted;

    public  ScheduleFragment(){}

    public static ScheduleFragment newInstance(String[][] excelData, String[] classes) {
        Bundle args = new Bundle();

        args.putSerializable(SchooledApplication.SCHEDULE_DATA, excelData);
        args.putStringArray(SchooledApplication.CLASSES_DATA, classes);

        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduale, container, false);

        day = (TextView) rootView.findViewById(R.id.day);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        listView = (ListView) rootView.findViewById(R.id.listView);
        aSwitch = (Switch) rootView.findViewById(R.id.switch1);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        excelData = (String[][]) getArguments().getSerializable(SchooledApplication.SCHEDULE_DATA);
        classes = getArguments().getStringArray(SchooledApplication.CLASSES_DATA);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        aSwitch.setChecked(sharedPref.getBoolean(SchooledApplication.SWITCH_DATA, false));

        day.setText("יום " + excelData[0][0]);


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item_rtl, (CharSequence[]) classes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deleted = deleteExtra(excelData[i + 1]);

                ScheduleListAdapter scheduleListAdapter =
                        new ScheduleListAdapter(getContext(), deleted.getItem1(), deleted.getItem2(), aSwitch.isChecked());
                listView.setAdapter(scheduleListAdapter);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(SchooledApplication.SCHEDULE_DATA, i + 1);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spinner.setSelection(sharedPref.getInt(SchooledApplication.SCHEDULE_DATA, 1) - 1);
        deleted = deleteExtra(excelData[sharedPref.getInt(SchooledApplication.SCHEDULE_DATA, 1)]);


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
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(getContext(), deleted.getItem1(), deleted.getItem2(), aSwitch.isChecked());
                listView.setAdapter(scheduleListAdapter);

                sharedPref.edit().putBoolean(SchooledApplication.SWITCH_DATA,aSwitch.isChecked()).apply();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sharedPref.edit().putBoolean(SchooledApplication.SWITCH_DATA,aSwitch.isChecked()).apply();
    }

    private Tuple<String[], Boolean> deleteExtra(String[] schedule){
        boolean isPre = false;
        int len;

        if(!schedule[1].isEmpty())
            isPre = true;

        for (len = 0; len < schedule.length; len++) {
            if(schedule[schedule.length - len - 1] != null)
                if (!schedule[schedule.length - len - 1].isEmpty())
                    break;
        }

        len += isPre ? 1 : 2;

        return new Tuple<>(Arrays.copyOfRange(schedule, SchooledApplication.FIRST_LINE + (isPre ? 1 : 2), /*FIRST_LINE +*/ (isPre ? 1 : 2) + schedule.length - len), isPre);
    }
}