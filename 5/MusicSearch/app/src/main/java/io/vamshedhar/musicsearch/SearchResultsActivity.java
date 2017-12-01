package io.vamshedhar.musicsearch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SearchResultsActivity extends MenuActivity implements GetDataFromServer.IData {

    public static final String TRACK_DETAILS_KEY = "TRACK_DETAILS";

    TextView resultsLabel;
    ListView searchResultsList;
    ProgressBar searchProgress;
    SearchResultsAdapter adapter;

    ArrayList<Track> tracks;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sortAsc:
                Collections.sort(tracks);
                adapter.notifyDataSetChanged();
                break;
            case R.id.sortDsc:
                Collections.reverse(tracks);
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        resultsLabel = (TextView) findViewById(R.id.resultsLabel);
        searchResultsList = (ListView) findViewById(R.id.searchResultsList);
        searchProgress = (ProgressBar) findViewById(R.id.searchResultsProgress);

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(MainActivity.SEARCH_STRING_KEY)){
            String searchString = getIntent().getExtras().getString(MainActivity.SEARCH_STRING_KEY);

            HashMap<String, String> urlParams = new HashMap<>();
            urlParams.put("method", MainActivity.SEARCH_METHOD);
            urlParams.put("track", searchString);
            urlParams.put("limit", "20");
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
        searchProgress.setVisibility(View.INVISIBLE);
        resultsLabel.setVisibility(View.VISIBLE);
        searchResultsList.setVisibility(View.VISIBLE);

        this.tracks = tracks;

        adapter = new SearchResultsAdapter(this, tracks);
        searchResultsList.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        searchResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Track track = tracks.get(i);
            Intent intent = new Intent(SearchResultsActivity.this, TrackDetailsActivity.class);
            intent.putExtra(TRACK_DETAILS_KEY, track);
            startActivity(intent);
            }
        });
    }
}
