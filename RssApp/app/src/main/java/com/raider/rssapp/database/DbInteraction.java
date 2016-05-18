package com.raider.rssapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.raider.rssapp.classes.Preferences;
import com.raider.rssapp.classes.Rss;
import com.raider.rssapp.classes.RssItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Raider on 14/05/16.
 */
public class DbInteraction {

    private DbSQLiteHelper dbSQLiteHelper;

    public DbInteraction(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this.dbSQLiteHelper = new DbSQLiteHelper(context,name,factory,version);
    }

    private SQLiteDatabase getReadableDb() {
        return dbSQLiteHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWritableDb() {
        return dbSQLiteHelper.getWritableDatabase();
    }

    public void saveItems(List<RssItem> items) {

        SQLiteDatabase db = getWritableDb();

        for (RssItem r: items) {

            if (!existsItem(r)) {

                try {

                    insertInto(r);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        deleteLastEntries();

        if (db != null) db.close();
    }

    public void saveRss(List<Rss> items) {

        SQLiteDatabase db = getWritableDb();

        for (Rss r: items) {

            if (!existsRss(r)) {

                try {

                    insertIntoRss(r);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (db != null) db.close();
    }

    private boolean existsRss(Rss r) {

        SQLiteDatabase db = getReadableDb();

        String[] args = {r.getCompany_name(),r.getUrl()};
        Cursor c = db.rawQuery("SELECT * FROM rss WHERE cname = ? AND url = ?", args);

        if(c.moveToFirst()){
            if (db != null) db.close();
            return true;
        }

        if (db != null) db.close();
        return false;
    }

    private void insertIntoRss(Rss r) throws ParseException {

        SQLiteDatabase db = getWritableDb();

        ContentValues reg = new ContentValues();
        reg.put("cname", r.getCompany_name());
        reg.put("url", r.getUrl());
        reg.put("topic", r.getTopic());

        db.insert("rss", null, reg);

        if (db != null) db.close();
    }

    public List<Rss> getRssList() {

        List<Rss> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDb();

        Cursor c = db.rawQuery("SELECT cname, topic, url FROM rss",null);
        if (c.moveToFirst()) {
            do {
                Rss r = new Rss();

                r.setCompany_name(c.getString(0));
                r.setTopic(c.getString(1));
                r.setUrl(c.getString(2));

                items.add(r);

            } while (c.moveToNext());
        }

        if (db != null) db.close();

        return items;
    }

    private boolean existsItem(RssItem r) {

        SQLiteDatabase db = getReadableDb();

        String[] args = {r.getTitle(),r.getUrl()};
        Cursor c = db.rawQuery("SELECT * FROM rssitems WHERE title = ? AND url = ?", args);

        if(c.moveToFirst()){
            if (db != null) db.close();
            return true;
        }

        if (db != null) db.close();
        return false;
    }

    private void insertInto(RssItem r) throws ParseException {

        SQLiteDatabase db = getWritableDb();

        ContentValues reg = new ContentValues();
        reg.put("rootrss", r.getRootRss());
        reg.put("title", r.getTitle());
        reg.put("category", r.getFirstCategory());
        reg.put("description", r.getDescription());
        reg.put("url", r.getUrl());
        reg.put("guid", r.getGuid());
        reg.put("pubDate", formatDate(r.getPubDate()));
        reg.put("author", r.getAuthor());

        db.insert("rssitems", null, reg);

        if (db != null) db.close();
    }

    private String formatDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date));
    }

    public void deleteEntrie(String s) {

        SQLiteDatabase db = getWritableDb();
        String[] args = new String[] {s};
        db.execSQL("DELETE FROM rssitems WHERE rootrss = ?",args);
        db.execSQL("DELETE FROM rss WHERE cname = ?", args);

        if (db != null) db.close();
    }

    private void deleteLastEntries() {

        SQLiteDatabase dbase = getReadableDb();
        Cursor cursor = dbase.rawQuery("SELECT DISTINCT(rootrss) FROM rssitems",null);
        List<String> rootrss = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0) != null) rootrss.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        SQLiteDatabase db = null;
        Cursor c;

        if (rootrss != null) {

            for (String s: rootrss) {
                db = getReadableDb();

                String[] arg = new String[] {s};
                c = db.rawQuery("SELECT count(*) FROM rssitems WHERE rootrss = ?", arg);

                int rowsNum = 0;

                if (c.moveToFirst()) {
                    rowsNum = c.getInt(0);

                    if (rowsNum > 100) {

                        db = getWritableDb();
                        int deletedRows = rowsNum - 100;
                        String[] args = new String[] {s, String.valueOf(deletedRows)};
                        db.execSQL("DELETE FROM rssitems WHERE rootrss = ? ORDER BY pubDate ASC limit ?",args);
                    }
                }
            }
        }

        if (db != null) db.close();
    }

    public List<RssItem> getList(String rootrss) {

        List<RssItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDb();
        String[] args = new String[]{rootrss};
        Cursor c = db.rawQuery("SELECT title, category, description, url, guid, pubDate, author" +
                " FROM rssitems WHERE rootrss = ? ORDER BY pubDate DESC limit 100", args);
        if (c.moveToFirst()) {
            do {
                RssItem r = new RssItem();

                r.setTitle(c.getString(0));
                r.setFirstCategory(c.getString(1));
                r.setDescription(c.getString(2));
                r.setUrl(c.getString(3));
                r.setGuid(c.getString(4));
                r.setPubDate(c.getString(5));
                r.setAuthor(c.getString(6));

                items.add(r);

            } while (c.moveToNext());
        }

        if (db != null) db.close();

        return items;
    }

    public List<RssItem> checkNewEntries(List<RssItem> items) {

        List<RssItem> newEntries = new ArrayList<>();

        for (RssItem r: items) {

            if (!existsItem(r)) {
                newEntries.add(r);
            }
        }

        return newEntries;
    }

    public void updatePreference(Preferences p, String arg) {

        SQLiteDatabase db = getWritableDb();

        ContentValues reg = new ContentValues();
        reg.put("language", p.getLanguage());
        reg.put("rotation", p.getRotation());

        String[] args = new String[]{arg};

        db.update("preferences", reg, "language = ?", args);
    }

    public Preferences getPreference() {

        SQLiteDatabase db = getReadableDb();

        Cursor c = db.rawQuery("SELECT language, rotation FROM preferences", null);

        Preferences p = new Preferences();

        if (c.moveToFirst()) {
            p.setLanguage(c.getString(0));
            p.setRotation(c.getInt(1));
        }

        return p;
    }

    public void createDatabase() {

        SQLiteDatabase db = getWritableDb();
        db.execSQL("DROP TABLE IF EXISTS rss");
        db.execSQL("DROP TABLE IF EXISTS rssitems");
        db.execSQL("DROP TABLE IF EXISTS preferences");
        db.execSQL("CREATE TABLE IF NOT EXISTS rss (cname TEXT, url TEXT, topic TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS rssitems (rootrss TEXT ,title TEXT, category TEXT, " +
                "description TEXT, url TEXT, guid TEXT, pubDate TIMESTAMP, author TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS preferences (language TEXT, rotation INTEGER)");
        String[] args = new String[]{"spanish", "0"};
        db.execSQL("INSERT INTO preferences (language, rotation) values (?, ?)", args);
    }
}
