package com.example.android.newsapp;

public class Articles {

    private String mSection;
    private String mTitle;
    private String mDate;
    private String mBrief;
    private String mUrl;
    private String mName;

    public Articles(String section, String title, String brief, String date, String url, String name) {
        mSection = section;
        mTitle = title;
        mBrief = brief;
        mDate = date;
        mUrl = url;
        mName = name;
    }

    public String getSection() {
        return mSection;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDate() {
        return mDate;
    }

    public String getBrief() {
        return mBrief;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getName() {
        return mName;
    }
}
