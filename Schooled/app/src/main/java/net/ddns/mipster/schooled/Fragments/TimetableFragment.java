package net.ddns.mipster.schooled.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.ddns.mipster.schooled.R;

/**
 * Created by Chen on 24/02/2017.
 */

public class TimetableFragment extends Fragment {

    public TimetableFragment(){}

    public static TimetableFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TimetableFragment fragment = new TimetableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("Timetable Fragment");

        return rootView;
    }
}
