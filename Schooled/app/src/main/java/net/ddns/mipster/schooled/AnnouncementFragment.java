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

public class AnnouncementFragment extends Fragment {

    public AnnouncementFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_announcement, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("Announcement Fragment");

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }
}
