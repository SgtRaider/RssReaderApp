package com.raider.rssapp.adapters;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.raider.rssapp.R;
import com.raider.rssapp.classes.Rss;

import java.util.List;

/**
 * Created by Raider on 11/05/16.
 */
public class MainMenuAdapter extends ArrayAdapter {

    private Activity context;
    private List<Rss> data;

    public MainMenuAdapter(Activity context, List<Rss> data) {
        super(context, R.layout.adapter_main_menu, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.adapter_main_menu, null);

        TextView lbTitulo = (TextView) item.findViewById(R.id.lblTitle);
        lbTitulo.setText(data.get(position).getCompany_name());

        TextView lbTopic = (TextView) item.findViewById(R.id.lblCategory);
        lbTopic.setText(data.get(position).getTopic());

        ImageView iv = (ImageView) item.findViewById(R.id.adapteriv);


        switch (data.get(position).getTopic()) {
            case "News":
                iv.setImageResource(R.mipmap.ic_news);
                break;
            case "Gaming":
                iv.setImageResource(R.mipmap.ic_gaming);
                break;
            case "Science":
                iv.setImageResource(R.mipmap.ic_science);
                break;
        }

        return item;
    }
}
