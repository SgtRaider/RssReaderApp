package com.raider.rssapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.raider.rssapp.R;
import com.raider.rssapp.classes.RssItem;

import java.util.List;

/**
 * Created by Raider on 12/05/16.
 */
public class RssItemAdapter extends ArrayAdapter{

    Activity context;
    List<RssItem> data;

    public RssItemAdapter (Activity context, List<RssItem> data) {
        super(context, R.layout.activity_rss_articles, data);
        this.context = context;
        this.data = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.adapter_rss, parent, false);

        TextView lblTitle = (TextView) item.findViewById(R.id.lbTitlerss);
        lblTitle.setText(data.get(position).getTitle());

        return item;
    }
}
