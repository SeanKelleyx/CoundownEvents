package com.sean.coundownevents;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.sean.coundownevents.database.DatabaseTools;
import com.sean.coundownevents.models.CountdownEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EventBuilderActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private byte[] mSelectedImage;
    private String mErrorMessage;
    private Long mDateMillis;

    @Bind(R.id.event_title) EditText mEventTitle;
    @Bind(R.id.event_date) DatePicker mDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_builder);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatePicker.setMinDate(System.currentTimeMillis()+ TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(validateNewEvent()){
                        DatabaseTools db = new DatabaseTools(getApplicationContext());
                        CountdownEvent event = new CountdownEvent();
                        event.setTitle(mEventTitle.getText().toString());
                        event.setDatetime(mDateMillis);
                        event.setBackground(mSelectedImage);
                        db.insert(event);
                        setResult(Activity.RESULT_OK);
                        finish();
                    }else{
                        Snackbar.make(view, mErrorMessage, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean validateNewEvent() {
        if(titleExists() && datetimeIsValid()){
            return true;
        }
        return false;
    }

    private boolean datetimeIsValid() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
        cal.set(Calendar.MONTH, mDatePicker.getMonth());
        cal.set(Calendar.YEAR, mDatePicker.getYear());
        mDateMillis = cal.getTimeInMillis();
        return true;
    }

    private boolean titleExists() {
        String title = mEventTitle.getText().toString();
        if(title!=null && title.length()>0){
            return true;
        }
        return false;
    }

    public void getImage(View view){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (null == data) return;
        Uri originalUri = null;
        byte[] bitmapdata = null;
        if (requestCode == SELECT_PICTURE) {
            originalUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), originalUri);
                ByteArrayOutputStream baData = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40 , baData);
                bitmapdata = baData.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mSelectedImage = bitmapdata;
    }
}
