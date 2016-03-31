package com.sean.coundownevents.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sean on 3/13/16. :)
 */
public class CountdownEventReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Countdown.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CountdownEventReaderContract.CountdownEventEntry.TABLE_NAME + " (" +
                    CountdownEventReaderContract.CountdownEventEntry._ID + " INTEGER PRIMARY KEY," +
                    CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_EVENT_TITLE + TEXT_TYPE + COMMA_SEP +
                    CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_EVENT_DATETIME + TEXT_TYPE + COMMA_SEP +
                    CountdownEventReaderContract.CountdownEventEntry.COLUMN_NAME_BACKGROUND_COLOR + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + CountdownEventReaderContract.CountdownEventEntry.TABLE_NAME;

    public CountdownEventReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO make a better upgrade strategy
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
