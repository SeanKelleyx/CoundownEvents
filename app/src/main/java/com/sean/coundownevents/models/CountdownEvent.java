package com.sean.coundownevents.models;

/**
 * Created by Sean on 3/13/16. :)
 */
public class CountdownEvent {
    private long mId;
    private String mTitle;
    private long mDatetime;
    private byte[] mBackground;

    public CountdownEvent(){}

    public CountdownEvent(long id, String title, Long datetime, byte[] background){
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

    public long getDatetime() {
        return mDatetime;
    }

    public void setDatetime(Long datetime) {
        mDatetime = datetime;
    }

    public byte[] getBackground() {
        return mBackground;
    }

    public void setBackground(byte[] background) {
        mBackground = background;
    }
}
