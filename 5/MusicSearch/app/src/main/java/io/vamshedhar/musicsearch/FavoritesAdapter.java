package io.vamshedhar.musicsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/13/17 6:16 PM.
 * vchinta1@uncc.edu
 */

public class FavoritesAdapter extends ArrayAdapter<Track> {
    Context context;
    ArrayList<Track> tracks;

    public FavoritesAdapter(Context context, ArrayList<Track> objects) {
        super(context, R.layout.favorite_item, objects);
        this.context = context;
        this.tracks = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.favorite_item, parent, false);
            holder = new ViewHolder();
            holder.trackTitle = (TextView) convertView.findViewById(R.id.favTrackTitle);
            holder.trackArtist = (TextView) convertView.findViewById(R.id.favTrackArtist);
            holder.trackThumbnil = (ImageView) convertView.findViewById(R.id.favTrackThumbnil);
            holder.favImage = (ImageView) convertView.findViewById(R.id.favTrackFavImage);
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

        holder.favImage.setTag(position);

        holder.favImage.setImageResource(android.R.drawable.btn_star_big_on);

        return convertView;
    }

    static class ViewHolder{
        TextView trackTitle, trackArtist;
        ImageView trackThumbnil, favImage;
    }
}
