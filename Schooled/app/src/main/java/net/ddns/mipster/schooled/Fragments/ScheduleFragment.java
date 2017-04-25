package net.ddns.mipster.schooled.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import net.ddns.mipster.schooled.SQLiteHelper;
import net.ddns.mipster.schooled.activities.LoadingActivity;
import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.adapters.ScheduleListAdapter;
import net.ddns.mipster.schooled.SchooledApplication;
import net.ddns.mipster.schooled.classes.Tuple;

import java.util.Arrays;

public class ScheduleFragment extends Fragment {

    private TextView day, error;
    private Spinner spinner;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Cursor excelData;
    private Switch aSwitch;
    private ImageView share;
    private String[] classes;
    private String[] classesData;
    private String dayVal;
    private SharedPreferences sharedPref;

    public  ScheduleFragment(){}

    public static ScheduleFragment newInstance(String[] classes, String day) {
        Bundle args = new Bundle();

        args.putStringArray(SchooledApplication.CLASSES_DATA, classes);
        args.putString(SchooledApplication.DAY_DATA, day);

        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduale, container, false);

        day = (TextView) rootView.findViewById(R.id.day);
        error = (TextView) rootView.findViewById(R.id.error);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        listView = (ListView) rootView.findViewById(R.id.listView);
        aSwitch = (Switch) rootView.findViewById(R.id.switch1);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        share = (ImageView) rootView.findViewById(R.id.share);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        excelData = SchooledApplication.SQL_DATA.getAllData(SQLiteHelper.Tables.SCHEDULE);
        classes = getArguments().getStringArray(SchooledApplication.CLASSES_DATA);
        dayVal = getArguments().getString(SchooledApplication.DAY_DATA);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        int loc = sharedPref.getInt(SchooledApplication.SCHEDULE_DATA, 0);

        if(excelData != null)
            if(loc < excelData.getColumnCount())
                excelData.moveToPosition(sharedPref.getInt(SchooledApplication.SCHEDULE_DATA, 0));
            else {
                excelData.moveToPosition(0);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(SchooledApplication.SCHEDULE_DATA, 0);
                editor.apply();
            }

        if(excelData == null || excelData.getCount() == 0 || classes == null || classes.length == 0) {
            swipeRefreshLayout.setVisibility(View.GONE);

            error.setVisibility(View.VISIBLE);
            error.setText("אין מערכת");

            return;
        }


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item_rtl, (CharSequence[]) classes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        aSwitch.setChecked(sharedPref.getBoolean(SchooledApplication.SWITCH_DATA, false));

        day.setText("יום " + dayVal);

        spinner.setSelection(sharedPref.getInt(SchooledApplication.SCHEDULE_DATA, 0));

        excelData.moveToFirst();
        classesData = new String[excelData.getCount()];

        for(int j = 0; j < classesData.length; j++, excelData.moveToNext())
            classesData[j] = excelData.getString(spinner.getSelectedItemPosition());


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                excelData.moveToFirst();
                classesData = new String[excelData.getCount()];

                for(int j = 0; j < classesData.length; j++, excelData.moveToNext())
                    classesData[j] = excelData.getString(i);


                ScheduleListAdapter scheduleListAdapter =
                        new ScheduleListAdapter(getContext(), classesData, aSwitch.isChecked());
                listView.setAdapter(scheduleListAdapter);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(SchooledApplication.SCHEDULE_DATA, i);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        swipeRefreshLayout.setColorSchemeColors(
                Color.RED,
                Color.GREEN,
                Color.BLUE
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().finish();
                startActivity(new Intent(getContext(), LoadingActivity.class));
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

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(getContext(), classesData, aSwitch.isChecked());
                listView.setAdapter(scheduleListAdapter);

                sharedPref.edit().putBoolean(SchooledApplication.SWITCH_DATA,aSwitch.isChecked()).apply();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, scheduleText());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    private String scheduleText(){
        String text = "המערכת ל" + day.getText() + ", כיתה " + classes[spinner.getSelectedItemPosition()] + "\n";

        if(classesData[0] != null) {
            int len = classesData.length - 1;
            boolean isPre = !classesData[0].isEmpty();

            for (; classesData[len] == null || classesData[len].isEmpty(); len--) ;

            for (int i = (isPre ? 0 : 1); i < len; i++) {
                String time = Integer.toString(i) + ". ";
                String classText = classesData[i].replaceAll("(?:\\n)+", ", ");
                text += time + (classText.isEmpty() ? "אין שיעור" : classText) + '\n';
            }

            String time = Integer.toString(len) + ". ";
            String classText = classesData[len].replaceAll("(?:\\n)+", ", ");
            text += time + (classText.isEmpty() ? "אין שיעור" : classText);
        } else
            text += "אין מערכת";

        return text;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sharedPref.edit().putBoolean(SchooledApplication.SWITCH_DATA,aSwitch.isChecked()).apply();
    }
}