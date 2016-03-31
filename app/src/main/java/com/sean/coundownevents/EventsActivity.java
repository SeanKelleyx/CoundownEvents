package com.sean.coundownevents;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sean.coundownevents.database.DatabaseTools;
import com.sean.coundownevents.models.CountdownEvent;

import java.util.ArrayList;

public class EventsActivity extends AppCompatActivity {

    private DatabaseTools mDb;
    private ArrayList<CountdownEvent> mEvents;
    private int testInt = 0;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private EventsPagerAdapter mEventsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        mDb = new DatabaseTools(this);
        mEvents = mDb.getCoutdownEvents();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the Events
        mEventsPagerAdapter = new EventsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mEventsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CountdownEvent event = new CountdownEvent();
                    testInt++;
                    event.setTitle("Event Title #"+testInt);
                    event.setDatetime("Event datetime #"+testInt);
                    event.setBackground("Background #"+testInt);
                    mEvents.add(event);
                    updateEvents();
                    Snackbar.make(view, "Will do something sometime!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A fragment containing a simple view.
     */
    public static class EventFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_EVENT_TITLE = "event_title";
        private static final String ARG_EVENT_DATETIME = "event_datetime";
        private static final String ARG_EVENT_BACKGROUND = "event_background";

        public EventFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static EventFragment newInstance(CountdownEvent event) {
            EventFragment fragment = new EventFragment();
            Bundle args = new Bundle();
            args.putString(ARG_EVENT_TITLE, event.getTitle());
            args.putString(ARG_EVENT_DATETIME, event.getDatetime());
            args.putString(ARG_EVENT_BACKGROUND, event.getBackground());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_events, container, false);
            TextView titleView = (TextView) rootView.findViewById(R.id.event_title);
            TextView datetimeView = (TextView) rootView.findViewById(R.id.event_datetime);
            TextView backgroundView = (TextView) rootView.findViewById(R.id.event_background);
            titleView.setText(getArguments().getString(ARG_EVENT_TITLE));
            datetimeView.setText(getArguments().getString(ARG_EVENT_DATETIME));
            backgroundView.setText(getArguments().getString(ARG_EVENT_BACKGROUND));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class EventsPagerAdapter extends FragmentPagerAdapter {

        public EventsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return EventFragment.newInstance(mEvents.get(position));
        }

        @Override
        public int getCount() {
            return mEvents.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mEvents.get(position).getTitle();
        }
    }

    public void updateEvents(){
        //TODO will want to pull from db?
//        mEvents = mDb.getCoutdownEvents();
        mEventsPagerAdapter.notifyDataSetChanged();
    }
}
