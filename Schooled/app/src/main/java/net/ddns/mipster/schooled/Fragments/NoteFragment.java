package net.ddns.mipster.schooled.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.ddns.mipster.schooled.classes.NoteData;
import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.SchooledApplication;

import java.util.ArrayList;

/**
 * Created by Chen on 24/02/2017.
 */

public class NoteFragment extends Fragment {

    public NoteFragment(){}

    public static NoteFragment newInstance(ArrayList<NoteData> noteData, String[] classes) {
        
        Bundle args = new Bundle();

        args.putSerializable(SchooledApplication.NOTE_DATA, noteData);
        args.putStringArray(SchooledApplication.CLASSES_DATA, classes);
        
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note, container, false);



        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
