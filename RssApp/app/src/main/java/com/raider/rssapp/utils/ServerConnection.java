package com.raider.rssapp.utils;

import com.raider.rssapp.classes.Rss;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raider on 10/05/16.
 */
public class ServerConnection {

    private Rss rss;

    public JSONObject getJson(String url) {

        try {
            System.out.println(url);
            InputStream is;
            JSONObject jsonObject;
            String resultado;
            HttpClient clienteHttp = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse respuesta = null;
            respuesta = clienteHttp.execute(httpPost);
            HttpEntity entity = respuesta.getEntity();
            is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String linea = null;

            while ((linea = br.readLine()) != null) {
                sb.append(linea + "\n");
                System.out.println(linea);
            }


            is.close();
            resultado = sb.toString();

            return new JSONObject(resultado);
        } catch (ClientProtocolException cpe) {
            cpe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Rss> getRssList() throws JSONException {
        JSONArray array = getJson(Constants.urlServer + "getrss").getJSONArray("rss");
        List<Rss> rssList = new ArrayList<Rss>();
        for (int i = 0; i < array.length(); i++) {
            rss = new Rss();
            rss.setId(array.getJSONObject(i).getInt("id"));
            rss.setCompany_name(array.getJSONObject(i).getString("company_name"));
            rss.setUrl(array.getJSONObject(i).getString("url"));
            rss.setTopic(array.getJSONObject(i).getString("topic"));
            rssList.add(rss);
        }

        return rssList;
    }

    public List<Rss> getRssList(String topic) throws JSONException {
        JSONArray array = getJson(Constants.urlServer + "getrss/topic=" + topic).getJSONArray("rss");
        List<Rss> rssList = new ArrayList<Rss>();
        for (int i = 0; i < array.length(); i++) {
            rss = new Rss();
            rss.setId(array.getJSONObject(i).getInt("id"));
            rss.setCompany_name(array.getJSONObject(i).getString("company_name"));
            rss.setUrl(array.getJSONObject(i).getString("url"));
            rss.setTopic(topic);
            rssList.add(rss);
        }

        return rssList;
    }

    public List<Rss> getUserRss(int id) throws JSONException {
        JSONArray array = getJson(Constants.urlServer + "getrss/user/id=" + id).getJSONArray("rss");
        List<Rss> rssList = new ArrayList<Rss>();
        for (int i = 0; i < array.length(); i++) {
            rss = new Rss();
            rss.setId(array.getJSONObject(i).getInt("id"));
            rss.setCompany_name(array.getJSONObject(i).getString("company_name"));
            rss.setUrl(array.getJSONObject(i).getString("url"));
            rss.setTopic(array.getJSONObject(i).getString("topic"));
            rssList.add(rss);
        }

        return rssList;
    }

    public List<Rss> getNoUserRss(int id) throws JSONException {
        JSONArray array = getJson(Constants.urlServer + "getrss/nouser/id=" + id).getJSONArray("rss");
        List<Rss> rssList = new ArrayList<Rss>();
        for (int i = 0; i < array.length(); i++) {
            rss = new Rss();
            rss.setId(array.getJSONObject(i).getInt("id"));
            rss.setCompany_name(array.getJSONObject(i).getString("company_name"));
            rss.setUrl(array.getJSONObject(i).getString("url"));
            rss.setTopic(array.getJSONObject(i).getString("topic"));
            rssList.add(rss);
        }

        return rssList;
    }

    public List<Rss> getUserRss(String topic, int id) throws JSONException {
        JSONArray array = getJson(Constants.urlServer + "getrss/id=" + id + "&topic=" + topic).getJSONArray("rss");
        List<Rss> rssList = new ArrayList<Rss>();
        for (int i = 0; i < array.length(); i++) {
            rss = new Rss();
            rss.setId(array.getJSONObject(i).getInt("id"));
            rss.setCompany_name(array.getJSONObject(i).getString("company_name"));
            rss.setUrl(array.getJSONObject(i).getString("url"));
            rss.setTopic(topic);
            rssList.add(rss);
        }

        return rssList;
    }

    public Boolean removeUserRss(int idrss) throws JSONException {
        return getJson(Constants.urlServer + "delete/user_rss/id_rss=" + idrss + "&id_user=" + Var.userId).getBoolean("removed");
    }

    public Boolean checkUser(String nick, String password) throws JSONException {
        return getJson(Constants.urlServer + "checkuser/nick=" + nick + "&password=" + password).getBoolean("check");
    }

    public Boolean createUser(String nick, String password, String name, String surname, String mail) throws JSONException {
        return getJson(Constants.urlServer + "setuser/name=" + name + "&surname=" + surname + "&nick=" + nick + "&password=" + password + "&mail=" + mail).getBoolean("insert");
    }

    public Boolean setUser_Rss(int rssId) throws JSONException {
        return getJson(Constants.urlServer + "setuser_rss/id_user=" + Var.userId + "&id_rss=" + rssId).getBoolean("insert");
    }

    public void getUserId(String nick) throws JSONException {
        Var.userId = getJson(Constants.urlServer + "user/getid/nickname=" + nick).getInt("id");
    }

    public Boolean setRss(String cname, String url, String topic) throws JSONException {
        return getJson(Constants.urlServer + "setrss/cname=" + cname + "&url=" + url + "&topic=" + topic).getBoolean("insert");
    }
}
