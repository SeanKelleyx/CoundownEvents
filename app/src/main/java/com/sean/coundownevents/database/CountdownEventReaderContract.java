package com.sean.coundownevents.database;

import android.provider.BaseColumns;

/**
 * This class defines the contract and is a container for constants that define names for URIs,
 * tables, and columns.
 */
public class CountdownEventReaderContract {
    //to prevent this class from being instantiated the contructor is empty
    public CountdownEventReaderContract(){}

    //CountdownEventEntry defines the table contents
    public static abstract class CountdownEventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_EVENT_TITLE = "title";
        public static final String COLUMN_NAME_BACKGROUND_COLOR = "color";
        public static final String COLUMN_NAME_EVENT_DATETIME = "datetime";
    }

}
