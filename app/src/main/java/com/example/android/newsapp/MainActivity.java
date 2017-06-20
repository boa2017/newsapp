package com.example.android.newsapp;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener {
    private static final String NEWS_REQUEST_URL =
            "https://content.guardianapis.com/search?";
    private static int LOADER_ID = 0;
    SwipeRefreshLayout swipe;
    private NewsAdapter adapter;
    private TextView emptyTextView;
    private NetworkInfo networkInfo;
    private ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        ListView listView = (ListView) findViewById(R.id.list_view);
        adapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                News news = adapter.getItem(i);
                String url = news.url;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo == null) {
            emptyTextView.setText(R.string.no_internet);
        } else {
            getLoaderManager().restartLoader(0, null, MainActivity.this);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        return new NewsLoader(this, QueryUtils.createStringUrl());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        swipe.setRefreshing(false);
        if (data != null) {
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.setNotifyOnChange(true);
            adapter.addAll(data);

        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
    }

    @Override
    public void onRefresh() {
        if (cm.getActiveNetworkInfo() == null) {
            emptyTextView.setText(R.string.no_internet);
        } else {
            emptyTextView.setText("");
        }
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}
