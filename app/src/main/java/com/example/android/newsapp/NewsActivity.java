package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Articles>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String LOG_TAG = NewsActivity.class.getName();
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;

    private static final String USGS_REQUEST_URL = "https://content.guardianapis.com/search";
            //"https://content.guardianapis.com/search?q=pixar&format=json&tag=film/film&api-key=test&tags=webTitle&show-fields=trailText&page-size=10&show-tags=contributor&order-by=newest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("er", "test: onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        ListView articlesListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        articlesListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new NewsAdapter(this, new ArrayList<Articles>());

        articlesListView.setAdapter(mAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        articlesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Articles currentArticle = mAdapter.getItem(position);
                Uri articleUri = Uri.parse(currentArticle.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();

            Log.e("er", "test: initLoader() called");
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_query_key)) ||
                key.equals(getString(R.string.settings_order_by_key))) {
            mAdapter.clear();
            mEmptyStateTextView.setVisibility(View.GONE);

            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<Articles>> onCreateLoader(int i, Bundle bundle) {

        Log.e("er", "test: onCreateLoader() called");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String searchQuery = sharedPrefs.getString(
                getString(R.string.settings_query_key),
                getString(R.string.settings_query_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Log.e("er", "orderBy: " + orderBy);

        Set<String> categories = sharedPrefs.getStringSet(getString(R.string.settings_category_key), getDefaultValues());
        String category_to_link = categories.toString()
                .replace(" ", "")
                .replace("[", "")
                .replace("]", "");

        Log.e("er", "category: " + category_to_link);

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // critical ho have
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("api-key", "test");
        // query build up
        uriBuilder.appendQueryParameter("q", searchQuery);
        uriBuilder.appendQueryParameter("tag", category_to_link);
        uriBuilder.appendQueryParameter("tags", "webTitle");
        uriBuilder.appendQueryParameter("page-size", "10");
        uriBuilder.appendQueryParameter("show-fields", "trailText");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", orderBy);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Articles>> loader, List<Articles> articles) {
        Log.e("er", "test: onLoadFinished() called");

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_articles);

        mAdapter.clear();
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Articles>> loader) {
        Log.e("er", "test: onLoaderReset() called");
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options main.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Set<String> getDefaultValues() {
        final Set<String> defaultValues = new HashSet<String>();
        defaultValues.addAll(Arrays.asList(
                getResources().getStringArray(
                        R.array.settings_category_default_values)));
        return defaultValues;
    }
}
