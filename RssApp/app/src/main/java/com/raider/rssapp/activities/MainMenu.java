package com.raider.rssapp.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
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
import com.raider.rssapp.database.DbInteraction;
import com.raider.rssapp.utils.Constants;
import com.raider.rssapp.utils.ServerConnection;
import com.raider.rssapp.utils.Util;
import com.raider.rssapp.utils.Var;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {

    private ListView lvMainMenu;
    private MainMenuAdapter adapter;
    private List<Rss> rssList;
    private SwipeRefreshLayout swipeLayout;
    private DbInteraction db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        db = new DbInteraction(this, Constants.dbName, null, 1);

        //db.createDatabase();

        rssList = new ArrayList<>();
        adapter = new MainMenuAdapter(MainMenu.this, rssList);


        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_mainmenu);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvMainMenu = (ListView) findViewById(R.id.lvMainMenu);
        lvMainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Rss selectedRss = ((Rss) adapterView.getItemAtPosition(i));
                Intent intent = new Intent(MainMenu.this, RssArticles.class);
                Bundle b = new Bundle();
                b.putString("rss", selectedRss.getUrl());
                b.putString("name", selectedRss.getCompany_name());
                intent.putExtras(b);

                startActivity(intent);
            }
        });

        lvMainMenu.setAdapter(adapter);
        registerForContextMenu(lvMainMenu);

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

        RefreshListTask refreshListTask = new RefreshListTask();
        refreshListTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu_contextualmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.ctremove:
                RemoveRssTask task = new RemoveRssTask(((Rss) adapter.getItem(info.position)));
                task.execute();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.addrss:
                Intent add = new Intent(MainMenu.this, AddRss.class);
                startActivity(add);
                return true;
            case R.id.ajustes:
                Intent ajustes = new Intent(MainMenu.this, Settings.class);
                startActivity(ajustes);
                return true;
            case R.id.about:
                Intent about = new Intent(MainMenu.this, About.class);
                startActivity(about);
                return true;
            case R.id.ntemas:
                Intent ntemas = new Intent(MainMenu.this, AllRss.class);
                startActivity(ntemas);
                return true;
            case R.id.map:
                Intent map = new Intent(MainMenu.this, Map.class);
                startActivity(map);
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
                db.saveRss(sc.getUserRss(Var.userId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.clear();
            adapter.addAll(db.getRssList());
            swipeLayout.setRefreshing(false);
            Util.toast(getApplicationContext(), getResources().getString(R.string.toastUpdated));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
        }
    }

    private class RemoveRssTask extends AsyncTask<Void,Void,Void> {

        private Rss item;
        private Boolean isRemoved = false;

        private RemoveRssTask(Rss item) {
            this.item = item;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServerConnection sc = new ServerConnection();
            try {
                isRemoved = sc.removeUserRss(item.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isRemoved) {
                adapter.remove(item);
                db.deleteEntrie(item.getCompany_name());
                Util.toast(MainMenu.this, getResources().getString(R.string.toastRemovedRss));
            } else {
                Util.toast(MainMenu.this, getResources().getString(R.string.toastNotRemovedRss));
            }

        }
    }
}
