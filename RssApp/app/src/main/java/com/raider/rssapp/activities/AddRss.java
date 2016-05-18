package com.raider.rssapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.raider.rssapp.R;
import com.raider.rssapp.utils.ServerConnection;
import com.raider.rssapp.utils.Util;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AddRss extends AppCompatActivity {

    private Spinner spinner;
    private Button btGuardar;
    private TextView txtName;
    private TextView txtUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rss);

        List<String> stringList = new ArrayList<>();
        stringList.add("Science");
        stringList.add("News");
        stringList.add("Gaming");

        ArrayAdapter<String> addRss = new ArrayAdapter<>(AddRss.this,android.R.layout.simple_spinner_item, stringList);


        spinner = (Spinner) findViewById(R.id.spTopics);
        btGuardar = (Button) findViewById(R.id.btSaveRss);
        txtName = (TextView) findViewById(R.id.txtRssName);
        txtUrl = (TextView) findViewById(R.id.txtRssUrl);
        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveRss saveRss = new SaveRss(txtName.getText().toString(), spinner.getSelectedItem().toString(), txtUrl.getText().toString());
                saveRss.execute();
            }
        });

        addRss.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(addRss);
    }

    private class SaveRss extends AsyncTask<Void,Void,Void> {

        private ServerConnection sc = new ServerConnection();

        private String cname;
        private String topic;
        private String url;
        private Boolean isSaved = false;


        public SaveRss(String cname, String topic, String url) {
            this.cname = cname;
            this.topic = topic;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sc = new ServerConnection();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                isSaved = sc.setRss(cname, url, topic);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isSaved) {
                Util.toast(getApplicationContext(), getResources().getString(R.string.toastRssSaved));
            } else {
                Util.toast(getApplicationContext(), getResources().getString(R.string.toastRssSaveProblem));
            }
        }
    }
}
