package io.vamshedhar.musicsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/13/17 3:25 PM.
 * vchinta1@uncc.edu
 */


public class SearchResultsAdapter extends ArrayAdapter<Track> {
    Context context;
    ArrayList<Track> tracks;

    public SearchResultsAdapter(Context context, ArrayList<Track> objects) {
        super(context, R.layout.search_result_item, objects);
        this.context = context;
        this.tracks = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_result_item, parent, false);
            holder = new ViewHolder();
            holder.trackTitle = (TextView) convertView.findViewById(R.id.trackTitle);
            holder.trackArtist = (TextView) convertView.findViewById(R.id.trackArtist);
            holder.trackThumbnil = (ImageView) convertView.findViewById(R.id.trackThumbnil);
            holder.favImage = (ImageView) convertView.findViewById(R.id.trackFavImage);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        Track track = tracks.get(position);

        holder.trackTitle.setText(track.getName());
        holder.trackArtist.setText(track.getArtist());

        if (!track.getThumbnailUrl().equals("")){
            Picasso.with(context)
                    .load(track.getThumbnailUrl())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.no_image)
                    .into(holder.trackThumbnil);
        }
        SharedPreferences pref = context.getSharedPreferences("MusicSearch", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = pref.getString(MainActivity.FAVORITE_LIST, null);
        Type type = new TypeToken<ArrayList<Track>>() {}.getType();
        ArrayList<Track> favTracks = gson.fromJson(json, type);

        holder.favImage.setTag(position);

        holder.favImage.setImageResource(android.R.drawable.btn_star_big_off);

        if (favTracks.indexOf(track) != -1){
            holder.favImage.setImageResource(android.R.drawable.btn_star_big_on);
        }

        holder.favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = context.getSharedPreferences("MusicSearch", Context.MODE_PRIVATE);

                Gson gson = new Gson();
                String json = pref.getString(MainActivity.FAVORITE_LIST, null);
                Type type = new TypeToken<ArrayList<Track>>() {}.getType();
                ArrayList<Track> favTracks = gson.fromJson(json, type);

                ImageView favImage = (ImageView) view;
                int position = (int) favImage.getTag();
                Track track = tracks.get(position);

                if (favTracks.indexOf(track) != -1){
                    favTracks.remove(track);
                    favImage.setImageResource(android.R.drawable.btn_star_big_off);
                } else {
                    if(favTracks.size() < 20){
                        favTracks.add(track);
                        favImage.setImageResource(android.R.drawable.btn_star_big_on);
                    } else {
                        Toast.makeText(context, "You can add upto 20 Favourites only!", Toast.LENGTH_SHORT).show();
                    }

                }

                SharedPreferences.Editor editor = pref.edit();

                Gson storeGson = new Gson();
                String storeJson = storeGson.toJson(favTracks);

                editor.putString(MainActivity.FAVORITE_LIST, storeJson);

                editor.commit();
            }
        });

        return convertView;
    }

    static class ViewHolder{
        TextView trackTitle, trackArtist;
        ImageView trackThumbnil, favImage;
    }
}
