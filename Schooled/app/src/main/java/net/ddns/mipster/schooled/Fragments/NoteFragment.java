package net.ddns.mipster.schooled.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.ddns.mipster.schooled.SQLiteHelper;
import net.ddns.mipster.schooled.adapters.NoteListAdapter;
import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.SchooledApplication;
import net.ddns.mipster.schooled.classes.NoteData;

/**
 * Created by Chen on 24/02/2017.
 */

public class NoteFragment extends Fragment {

    ListView listView;
    Cursor noteData;
    NoteListAdapter adapter;
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

        noteData = SchooledApplication.SQL_DATA.getAllData(SQLiteHelper.Tables.NOTE);
        classes = getArguments().getStringArray(SchooledApplication.CLASSES_DATA);

        registerForContextMenu(listView);

        if(noteData.getCount() == 0) {
            listView.setVisibility(View.GONE);
            gone.setVisibility(View.VISIBLE);
        } else {
            adapter = new NoteListAdapter(getContext(), noteData, classes);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.shareWith:
                share(true, info.id);
                return true;
            case R.id.shareWithout:
                share(false, info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void share(boolean with, long loc) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, noteText(with,(int) loc));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private String noteText(boolean with, int loc) {
        NoteData data = adapter.getItem(loc);

        if(!with)
            return data.getText();

        String classSelect = "ההודעה מופיעה מתחת לכיתות: ";

        for(int i = data.getX1(); i < data.getX2(); i++)
            classSelect += classes[i - 1] + ", ";
        classSelect += classes[data.getX2() - 1];


        if(data.getY1() != data.getY2()) {
            classSelect += "\nובין השעות ";
            classSelect += (data.getY1() - SchooledApplication.FIRST_LINE - 1)
                    + " ל " +
                    (data.getY2() - SchooledApplication.FIRST_LINE - 1);
        } else
            classSelect += "\nבשעה " + (data.getY1() - SchooledApplication.FIRST_LINE - 1);

        return data.getText() + "\n\n\n" + classSelect;
    }
}