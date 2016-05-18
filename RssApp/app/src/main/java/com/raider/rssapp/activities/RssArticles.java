package com.raider.rssapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.raider.rssapp.R;
import com.raider.rssapp.adapters.RssItemAdapter;
import com.raider.rssapp.classes.Rss;
import com.raider.rssapp.classes.RssItem;
import com.raider.rssapp.database.DbInteraction;
import com.raider.rssapp.utils.Constants;
import com.raider.rssapp.utils.RssParser;
import com.raider.rssapp.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class RssArticles extends AppCompatActivity {

    private RssParser saxparser;
    private SwipeRefreshLayout swipeLayout;
    private String rss;
    private String rssName;
    private ListView listView;
    private RssItemAdapter adapter;
    private List<RssItem> rssItems;
    private DbInteraction db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_articles);

        db = new DbInteraction(this, Constants.dbName, null, 1);

        listView = (ListView) findViewById(R.id.list_articles);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                RssItem selectedRss = ((RssItem) adapterView.getItemAtPosition(i));
                Intent intent = new Intent(RssArticles.this, WebViewer.class);
                Bundle b = new Bundle();
                b.putString("url", selectedRss.getUrl());
                intent.putExtras(b);

                startActivity(intent);
            }
        });

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_articles);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshRss refreshRss = new RefreshRss(rss);
                refreshRss.execute();
            }
        });

        rss = this.getIntent().getExtras().getString("rss");
        rssName = this.getIntent().getExtras().getString("name");
        rssItems = new ArrayList<>();

        RefreshRss refreshRss = new RefreshRss(rss);
        refreshRss.execute();

        adapter = new RssItemAdapter(RssArticles.this, rssItems);
        listView.setAdapter(adapter);
    }

    private class RefreshRss extends AsyncTask<String,Void,Void> {

        private String url;

        private RefreshRss(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            saxparser = new RssParser(url);
            swipeLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(String... strings) {

            db.saveItems(saxparser.parse(rssName));
            return null;
        }

        @Override
        protected void onPostExecute(Void aBoolean) {

            super.onPostExecute(aBoolean);
            adapter.clear();
            adapter.addAll(db.getList(rssName));
            adapter.notifyDataSetChanged();
            swipeLayout.setRefreshing(false);
            Util.toast(getApplicationContext(), getResources().getString(R.string.toastUpdated));
        }
    }
}
