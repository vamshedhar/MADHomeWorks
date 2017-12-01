package io.vamshedhar.musicsearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/27/17 11:19 PM.
 * vchinta1@uncc.edu
 */

public class ParseTrackUtil {

    public static ArrayList<Track> ParseTracks(String data) throws JSONException, InterruptedException {
        ArrayList<Track> tracksList = new ArrayList<>();

        JSONObject root = new JSONObject(data);

        JSONArray tracks;
        boolean similartracks = false;

        if (root.has("similartracks")){
            similartracks = true;
            JSONObject similartracksObj = root.getJSONObject("similartracks");
            tracks = similartracksObj.getJSONArray("track");
        } else {
            JSONObject results = root.getJSONObject("results");
            JSONObject trackmatches = results.getJSONObject("trackmatches");
            tracks = trackmatches.getJSONArray("track");
        }

        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);

            String mbid = "";

            if(track.has("mbid")){
                mbid = track.getString("mbid");
            }

            String artist;

            if(similartracks){
                JSONObject artistObj = track.getJSONObject("artist");
                artist = artistObj.getString("name");
            } else {
                artist = track.getString("artist");
            }

            Track TrackObject = new Track(mbid, track.getString("name"), artist,  track.getString("url"));

            JSONArray imagesList = track.getJSONArray("image");

            for (int j = 0; j < imagesList.length(); j++) {
                JSONObject imageObject = imagesList.getJSONObject(j);

                if(imageObject.getString("size").equals("small")){
                    TrackObject.setThumbnailUrl(imageObject.getString("#text"));
                } else if(imageObject.getString("size").equals("large")){
                    TrackObject.setImageUrl(imageObject.getString("#text"));
                }
            }

            tracksList.add(TrackObject);
        }

        return tracksList;
    }
}
