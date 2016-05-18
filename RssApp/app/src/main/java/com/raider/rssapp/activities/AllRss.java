package com.raider.rssapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.raider.rssapp.R;
import com.raider.rssapp.adapters.MainMenuAdapter;
import com.raider.rssapp.classes.Rss;
import com.raider.rssapp.utils.ServerConnection;
import com.raider.rssapp.utils.Util;
import com.raider.rssapp.utils.Var;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AllRss extends AppCompatActivity {

    private ListView lvAllRss;
    private MainMenuAdapter adapter;
    private List<Rss> rssList;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rss);

        rssList = new ArrayList<>();
        adapter = new MainMenuAdapter(AllRss.this, rssList);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_allrss);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvAllRss = (ListView) findViewById(R.id.lvAllRss);
        lvAllRss.setAdapter(adapter);
        registerForContextMenu(lvAllRss);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshListTask task = new RefreshListTask();
                task.execute();
            }
        });

        RefreshListTask task = new RefreshListTask();
        task.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        if (preferences.getBoolean("lockRotation", false)) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.all_rss_contextualmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.ctadd:
                AddRssTask task = new AddRssTask(((Rss) adapter.getItem(info.position)));
                task.execute();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_rss_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.ajustes:
                Intent ajustes = new Intent(AllRss.this, Settings.class);
                startActivity(ajustes);
                return true;
            case R.id.about:
                Intent about = new Intent(AllRss.this, About.class);
                startActivity(about);
                return true;
            case R.id.mainmenu:
                Intent menu = new Intent(AllRss.this, MainMenu.class);
                startActivity(menu);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class RefreshListTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServerConnection sc = new ServerConnection();
            try {
                rssList.clear();
                rssList = sc.getNoUserRss(Var.userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.clear();
            adapter.addAll(rssList);
            rssList.clear();
            swipeLayout.setRefreshing(false);
            Util.toast(getApplicationContext(), getResources().getString(R.string.toastUpdated));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
        }
    }

    private class AddRssTask extends AsyncTask<Void,Void,Void> {

        private Rss item;

        private AddRssTask(Rss item) {
            this.item = item;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServerConnection sc = new ServerConnection();
            try {
                sc.setUser_Rss(item.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            rssList.remove(item);
            adapter.remove(item);

        }
    }
}
