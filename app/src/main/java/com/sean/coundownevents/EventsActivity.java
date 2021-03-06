package com.sean.coundownevents;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sean.coundownevents.database.DatabaseTools;
import com.sean.coundownevents.models.ColorWheel;
import com.sean.coundownevents.models.CountdownEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

public class EventsActivity extends AppCompatActivity {

    private final int MAKE_NEW_EVENT = 1;
    private DatabaseTools mDb;
    private ArrayList<CountdownEvent> mEvents;
    private LruCache<String, Bitmap> mMemoryCache;

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
    private View mVeryFarBackgroundView;

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

        mVeryFarBackgroundView = findViewById(R.id.main_content);
        mVeryFarBackgroundView.setBackgroundColor(ColorWheel.getColor());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mEventsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchEventBuilder(view);
                }
            });
        }
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void launchEventBuilder(View view) {
        Intent intent = new Intent(this, EventBuilderActivity.class);
        startActivityForResult(intent, MAKE_NEW_EVENT);
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

    public static int daysBetween(Calendar day1, Calendar day2){
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        updateEvents();
    }

    public void loadBitmap(byte[] bitmapArray, ImageView imageView, String imageKey) {
        final Bitmap bitmap = mMemoryCache.get(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView,bitmapArray, imageKey);
            task.execute(0);
        }
    }

    public void updateEvents() {
        mEvents = mDb.getCoutdownEvents();
        mEventsPagerAdapter.notifyDataSetChanged();
    }

    /**
     * A fragment containing the event view.
     */
    public static class EventFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_EVENT_TITLE = "event_title";
        private static final String ARG_EVENT_DATETIME = "event_datetime";
        private static final String ARG_EVENT_BACKGROUND = "event_background";
        private static final String ARG_EVENT_POSITION = "event_position";

        public EventFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static EventFragment newInstance(CountdownEvent event, int position) {
            EventFragment fragment = new EventFragment();
            Bundle args = new Bundle();
            args.putString(ARG_EVENT_TITLE, event.getTitle());
            args.putLong(ARG_EVENT_DATETIME, event.getDatetime());
            args.putByteArray(ARG_EVENT_BACKGROUND, event.getBackground());
            args.putInt(ARG_EVENT_POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_events, container, false);
            TextView titleView = (TextView) rootView.findViewById(R.id.event_title);
            TextView datetimeView = (TextView) rootView.findViewById(R.id.event_datetime);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setBackgroundColor(ColorWheel.getColor());
            titleView.setText(getArguments().getString(ARG_EVENT_TITLE));
            datetimeView.setText(getCountdownDays(getArguments().getLong(ARG_EVENT_DATETIME)) + "");
            byte[] background = getArguments().getByteArray(ARG_EVENT_BACKGROUND);
            int position = getArguments().getInt(ARG_EVENT_POSITION);
            if(background != null){
                ((EventsActivity) getActivity()).loadBitmap(background, imageView, position + "");
            }
            return rootView;
        }

        private String getCountdownDays(long datetime) {
            Calendar today = Calendar.getInstance();
            Calendar event = Calendar.getInstance();
            event.setTimeInMillis(datetime);
            int days = daysBetween(today, event);
            return days+"";
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
            return EventFragment.newInstance(mEvents.get(position), position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView(((Fragment)object).getView());
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


    protected class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private byte[] bimapByteArray = null;
        private String iKey = "";
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView, byte[] bitmapArray,String imageKey) {
            bimapByteArray = bitmapArray;
            iKey = imageKey;
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromByteArray();
        }

        private Bitmap decodeSampledBitmapFromByteArray() {
            Bitmap bmp;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inScaled = false;
            options.inSampleSize = 4;
            bmp = BitmapFactory.decodeByteArray(bimapByteArray, 0, bimapByteArray.length, options);
            addBitmapToMemoryCache(iKey,bmp);
            return bmp;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
