package com.raider.rssapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Raider on 14/05/16.
 */
public class DbSQLiteHelper extends SQLiteOpenHelper {

    String sqlCreateRssItems = "CREATE TABLE rssitems (rootrss TEXT ,title TEXT, category TEXT, " +
            "description TEXT, url TEXT, guid TEXT, pubDate TIMESTAMP, author TEXT)";
    String sqlCreateRss = "CREATE TABLE rss (cname TEXT, url TEXT, topic TEXT)";
    String sqlPreferences = "CREATE TABLE preferences (language TEXT, rotation INTEGER)";

    public DbSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase sq) {
        sq.execSQL(sqlCreateRssItems);
        sq.execSQL(sqlCreateRss);
        sq.execSQL(sqlPreferences);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sq, int i, int i1) {}
}
