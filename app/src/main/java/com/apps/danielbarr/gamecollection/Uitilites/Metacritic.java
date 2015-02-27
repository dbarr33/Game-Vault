package com.apps.danielbarr.gamecollection.Uitilites;

import android.os.AsyncTask;
import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by danielbarr on 1/27/15.
 */
public class Metacritic {

    private JSONObject gameJson = new JSONObject();

    public JSONObject getInfo()
    {
        new getMataCritic().execute("test");
        return gameJson;
    }

    private class getMataCritic extends AsyncTask<String, String, JSONObject> {
        private final static String ENDPOINT = "https://byroredux-metacritic.p.mashape.com/find/game";
        private final static String KEY = "LyqQ22KvW7mshYfhVGtywEd0ZsQkp1VMTxKjsnUpApz3AnNC0K";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected JSONObject doInBackground(String... args) {

            HttpResponse<JsonNode> response = null;
            HttpClient httpClient = new DefaultHttpClient();
            org.apache.http.HttpResponse response2;
            try {
                HttpPost httpPost = new HttpPost(ENDPOINT);

                HttpParams params = new BasicHttpParams();
                params.setParameter("platform", String.valueOf(1));
                params.setParameter("retry", String.valueOf(4));
                params.setParameter("title", "The Elder Scrolls V: Skyrim");

                httpPost.setHeader("X-Mashape-Key", "LyqQ22KvW7mshYfhVGtywEd0ZsQkp1VMTxKjsnUpApz3AnNC0K");
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                httpPost.setHeader("Accept", "application/json");
                httpPost.setParams(params);

                try {
                    response2  = httpClient.execute(httpPost);
                    InputStream in =  response2.getEntity().getContent();
                    String result = in.toString();
                    Log.e("MetaCritic",String.valueOf(response2.getStatusLine().getReasonPhrase()));
                    JSONObject jsonObject = new JSONObject(result);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    response = Unirest.post("https://byroredux-metacritic.p.mashape.com/find/game")
                            .header("X-Mashape-Key", "LyqQ22KvW7mshYfhVGtywEd0ZsQkp1VMTxKjsnUpApz3AnNC0K")
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .header("Accept", "application/json")

                            .asJson();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response.getBody().getArray().getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(JSONObject jsonObject) {

            gameJson = jsonObject;

        }
    }

    String getUrl(String urlSpect) throws IOException
    {
        return new String(getUrlBytes(urlSpect));
    }

    byte[] getUrlBytes(String urlSpect) throws IOException
    {
        URL url = new URL(urlSpect);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return  out.toByteArray();
        }
        finally {
            Log.e("MetaCritic", connection.getResponseMessage() + " " + connection.getResponseCode());
            connection.disconnect();
        }
    }
}
