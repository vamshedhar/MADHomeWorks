package io.vamshedhar.musicsearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class TrackDetailsActivity extends MenuActivity implements GetDataFromServer.IData {

    TextView trackDetailsName, trackDetailsArtist, trackDetailsUrl;
    ImageView trackDetailsImage;

    ListView similarTracks;

    ProgressBar similarTracksPb;

    ArrayList<Track> tracks;

    SearchResultsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);

        trackDetailsName = (TextView) findViewById(R.id.trackDetailsName);
        trackDetailsArtist = (TextView) findViewById(R.id.trackDetailsArtist);
        trackDetailsUrl = (TextView) findViewById(R.id.trackDetailsUrl);

        trackDetailsImage = (ImageView) findViewById(R.id.trackDetailsImage);

        similarTracksPb = (ProgressBar) findViewById(R.id.similarTracksPb);

        similarTracks = (ListView) findViewById(R.id.similarTracksList);

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(SearchResultsActivity.TRACK_DETAILS_KEY)){
            Track track = (Track) getIntent().getSerializableExtra(SearchResultsActivity.TRACK_DETAILS_KEY);

            trackDetailsName.setText(track.getName());
            trackDetailsArtist.setText(track.getArtist());
            trackDetailsUrl.setText(track.getUrl());

            if (!track.getImageUrl().equals("")){
                Picasso.with(this)
                        .load(track.getImageUrl())
                        .placeholder(R.drawable.progress_animation)
                        .error(R.drawable.no_image)
                        .into(trackDetailsImage);
            }

            HashMap<String, String> urlParams = new HashMap<>();
            urlParams.put("method", MainActivity.SIMILAR_TRACKS_METHOD);
            urlParams.put("track", track.getName());
            urlParams.put("artist", track.getArtist());
            urlParams.put("limit", "10");
            urlParams.put("api_key", MainActivity.API_KEY);
            urlParams.put("format", "json");

            new GetDataFromServer(this, MainActivity.BASE_URL, urlParams).execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setupData(final ArrayList<Track> tracks) {
        similarTracksPb.setVisibility(View.INVISIBLE);
        similarTracks.setVisibility(View.VISIBLE);

        this.tracks = tracks;

        adapter = new SearchResultsAdapter(this, tracks);
        similarTracks.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        similarTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Track track = tracks.get(i);
            Intent intent = new Intent(TrackDetailsActivity.this, TrackDetailsActivity.class);
            intent.putExtra(SearchResultsActivity.TRACK_DETAILS_KEY, track);
            startActivity(intent);
            }
        });
    }

    public void onURLClick(View view){
        TextView urlView = (TextView) view;

        String url = urlView.getText().toString().trim();


        if(!url.equals("")){

            if (!url.startsWith("http://") && !url.startsWith("https://")){
                url = "http://" + url;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }
}
