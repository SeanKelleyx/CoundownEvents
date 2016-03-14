package com.sean.coundownevents.models;

/**
 * Created by Sean on 3/13/16. :)
 */
public class CountdownEvent {
    private long mId;
    private String mTitle;
    private String mDatetime;
    private String mBackground;

    public CountdownEvent(long id, String title, String datetime, String background){
        mId = id;
        mTitle = title;
        mDatetime = datetime;
        mBackground = background;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDatetime() {
        return mDatetime;
    }

    public void setDatetime(String datetime) {
        mDatetime = datetime;
    }

    public String getBackground() {
        return mBackground;
    }

    public void setBackground(String background) {
        mBackground = background;
    }
}
