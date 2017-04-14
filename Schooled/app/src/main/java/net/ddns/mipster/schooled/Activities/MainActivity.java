package net.ddns.mipster.schooled.activities;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.ddns.mipster.schooled.classes.AnnouncementItemData;
import net.ddns.mipster.schooled.fragments.AnnouncementFragment;
import net.ddns.mipster.schooled.fragments.ScheduleFragment;
import net.ddns.mipster.schooled.fragments.NoteFragment;
import net.ddns.mipster.schooled.classes.NoteData;
import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.SchooledApplication;

import java.util.ArrayList;

import klogi.com.RtlViewPager;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private String[] names;
    private ArrayList<NoteData> noteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        names = getResources().getStringArray(R.array.fragment_names);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (RtlViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(1);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        noteData = (ArrayList<NoteData>)
                getIntent().getSerializableExtra(SchooledApplication.NOTE_DATA);

        ConstraintLayout tabLinearLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.title);
        TextView tabNum = (TextView) tabLinearLayout.findViewById(R.id.num);

        tabContent.setText(tabLayout.getTabAt(2).getText());
        if(noteData.size() == 0)
            tabNum.setVisibility(View.GONE);
        else
            tabNum.setText(Integer.toString(noteData.size()));

        tabLayout.getTabAt(2).setCustomView(tabLinearLayout);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return AnnouncementFragment.newInstance((ArrayList<AnnouncementItemData>)
                            getIntent().getSerializableExtra(SchooledApplication.ANNOUNCEMENT_DATA));
                case 1:
                    return ScheduleFragment.newInstance((String[][])
                            getIntent().getSerializableExtra(SchooledApplication.SCHEDULE_DATA),
                            getIntent().getStringArrayExtra(SchooledApplication.CLASSES_DATA));
                case 2:
                    return NoteFragment.newInstance(noteData,
                            getIntent().getStringArrayExtra(SchooledApplication.CLASSES_DATA));
            }
            return null;
        }

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return names[position];
        }
    }
}