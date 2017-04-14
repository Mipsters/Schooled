package net.ddns.mipster.schooled.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import net.ddns.mipster.schooled.SQLiteHelper;
import net.ddns.mipster.schooled.adapters.NoteListAdapter;
import net.ddns.mipster.schooled.classes.NoteData;
import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.SchooledApplication;

import java.util.ArrayList;

/**
 * Created by Chen on 24/02/2017.
 */

public class NoteFragment extends Fragment {

    ListView listView;
    TextView gone;
    String[] classes;

    public NoteFragment(){}

    public static NoteFragment newInstance(String[] classes) {
        
        Bundle args = new Bundle();

        args.putStringArray(SchooledApplication.CLASSES_DATA, classes);
        
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView);
        gone = (TextView) rootView.findViewById(R.id.gone);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Cursor noteData = SchooledApplication.SQL_DATA.getAllData(SQLiteHelper.Tables.NOTE);
        classes = getArguments().getStringArray(SchooledApplication.CLASSES_DATA);

        if(noteData.getCount() == 0) {
            listView.setVisibility(View.GONE);
            gone.setVisibility(View.VISIBLE);
        } else {
            NoteListAdapter adapter = new NoteListAdapter(getContext(), noteData, classes);
            listView.setAdapter(adapter);
        }
    }
}