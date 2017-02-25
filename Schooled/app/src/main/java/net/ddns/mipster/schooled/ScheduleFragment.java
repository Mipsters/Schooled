package net.ddns.mipster.schooled;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Chen on 24/02/2017.
 */

public class ScheduleFragment extends Fragment {

    public  ScheduleFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduale, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("Schedule Fragment");

        return rootView;
    }
}
