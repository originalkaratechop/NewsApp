package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<Articles>> {

    private static final String LOG_TAG = NewsLoader.class.getName();
    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.e("LOG_TAG", "test: onStartLoading() called");
        forceLoad();
    }

    @Override
    public List<Articles> loadInBackground() {
        Log.e("LOG_TAG", "test: loadInBackground() called");
        if (mUrl == null) {
            return null;
        }

        List<Articles> articles = Utils.fetchNewsData(mUrl);
        return articles;
    }
}
