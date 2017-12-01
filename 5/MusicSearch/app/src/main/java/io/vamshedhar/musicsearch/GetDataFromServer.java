package io.vamshedhar.musicsearch;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/27/17 10:51 PM.
 * vchinta1@uncc.edu
 */

public class GetDataFromServer extends AsyncTask<Void, Void, ArrayList<Track>> {
    String BASE_URL;
    IData activity;

    HashMap<String, String> urlParams;

    public GetDataFromServer(IData activity, String BASE_URL, HashMap<String, String> urlParams) {
        this.BASE_URL = BASE_URL;
        this.activity = activity;
        this.urlParams = urlParams;
    }

    @Override
    protected void onPostExecute(ArrayList<Track> ObjectsList) {
        super.onPostExecute(ObjectsList);
        activity.setupData(ObjectsList);
    }

    @Override
    protected ArrayList<Track> doInBackground(Void... voids) {
        RequestParams request = new RequestParams("GET", BASE_URL);

        for(String key : this.urlParams.keySet()){
            request.addParam(key, this.urlParams.get(key));
        }

        ArrayList<Track> ObjectsList = new ArrayList<>();

        try {
            HttpURLConnection con = request.setupConnection();
            con.connect();
            int statusCode = con.getResponseCode();
            if(statusCode == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder builder = new StringBuilder();

                String line = reader.readLine();

                while (line != null){
                    builder.append(line);
                    line = reader.readLine();
                }

                ObjectsList =  ParseTrackUtil.ParseTracks(builder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ObjectsList;
    }

    public interface IData{
        void setupData(ArrayList<Track> ObjectsList);
    }
}
