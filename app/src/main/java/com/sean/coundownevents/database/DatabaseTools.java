package com.sean.coundownevents.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sean.coundownevents.models.CountdownEvent;

import java.util.ArrayList;

/**
 * Created by Sean on 3/13/16. :)
 */
public class DatabaseTools {
    private CountdownEventReaderDbHelper mDbHelper;

    public DatabaseTools(Context context) {
        mDbHelper = new CountdownEventReaderDbHelper(context);
    }

    public long insert(CountdownEvent event) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_EVENT_TITLE, event.getTitle());
        values.put(CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_EVENT_DATETIME, event.getDatetime());
        values.put(CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_BACKGROUND_COLOR, event.getBackground());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                CountdownEventReaderContract.CountdownEventEntry.TABLE_NAME,
                null,
                values);
        return newRowId;
    }

    public int deleteEvent(long id){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = CountdownEventReaderContract.CountdownEventEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        int rowsAffected = db.delete(CountdownEventReaderContract.CountdownEventEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return rowsAffected;
    }

    public Cursor getAllEvents(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                CountdownEventReaderContract.CountdownEventEntry._ID,
                CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_EVENT_TITLE,
                CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_EVENT_DATETIME,
                CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_BACKGROUND_COLOR
        };

        Cursor c = db.query(
                CountdownEventReaderContract.CountdownEventEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        db.close();
        return c;
    }

    public ArrayList<CountdownEvent> getCoutdownEvents(){
        Cursor c = getAllEvents();
        return getEventsFromCursor(c);
    }

    private ArrayList<CountdownEvent> getEventsFromCursor(Cursor c){
        ArrayList<CountdownEvent> events = new ArrayList<CountdownEvent>();
        if(c!=null){
            if(c.moveToFirst()){
                do{
                    long id = c.getLong(c.getColumnIndex(CountdownEventReaderContract.CountdownEventEntry._ID));
                    String title = c.getString(c.getColumnIndex(CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_EVENT_TITLE));
                    String datetime = c.getString(c.getColumnIndex(CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_EVENT_DATETIME));
                    String background = c.getString(c.getColumnIndex(CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_BACKGROUND_COLOR));
                    events.add(new CountdownEvent(id,title,datetime,background));
                }while(c.moveToNext());
            }
        }
        return events;
    }

    public int updateRecord(long id, String title, String datetime, String background){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_EVENT_TITLE, title);
        values.put(CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_EVENT_DATETIME, datetime);
        values.put(CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_BACKGROUND_COLOR, background);

        // Which row to update, based on the ID
        String selection = CountdownEventReaderContract.CountdownEventEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(
                CountdownEventReaderContract.CountdownEventEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        db.close();
        return count;
    }

}
