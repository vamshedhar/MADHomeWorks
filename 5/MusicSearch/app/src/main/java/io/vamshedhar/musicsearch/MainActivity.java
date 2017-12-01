package io.vamshedhar.musicsearch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends MenuActivity {

    public static final String TAG = "MusicSearch";
    public static final String SEARCH_STRING_KEY = "SEARCH_STRING";
    public static final String BASE_URL = "http://ws.audioscrobbler.com/2.0/";
    public static final String API_KEY = "80f4b3fdf6fe5d8e60e860177b8fe2e0";

    public static final String SEARCH_METHOD = "track.search";
    public static final String SIMILAR_TRACKS_METHOD = "track.getsimilar";

    public static final String FAVORITE_LIST = "FAVORITES";

    Set<String> ids;
    ArrayList<Track> favoriteTracks;

    EditText searchString;
    ListView favoritesList;

    FavoritesAdapter adapter;



    public boolean isConnectedOnline(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnected()){
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5001){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                searchResults();
            } else {
                Toast.makeText(MainActivity.this, "Please Grant required permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onFavRemove(View view){
        int position = (int) view.getTag();
        favoriteTracks.remove(position);
        adapter.notifyDataSetChanged();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MusicSearch", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        Gson storeGson = new Gson();
        String storeJson = storeGson.toJson(favoriteTracks);

        editor.putString(MainActivity.FAVORITE_LIST, storeJson);

        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MusicSearch", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = pref.getString(MainActivity.FAVORITE_LIST, null);
        Type type = new TypeToken<ArrayList<Track>>() {}.getType();
        favoriteTracks = gson.fromJson(json, type);

        adapter = new FavoritesAdapter(this, favoriteTracks);
        favoritesList.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        favoritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Track track = favoriteTracks.get(i);
            Intent intent = new Intent(MainActivity.this, TrackDetailsActivity.class);
            intent.putExtra(SearchResultsActivity.TRACK_DETAILS_KEY, track);
            startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchString = (EditText) findViewById(R.id.searchString);
        favoritesList = (ListView) findViewById(R.id.favoritesList);

        ids = new HashSet<>();
        favoriteTracks = new ArrayList<>();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MusicSearch", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = pref.getString(MainActivity.FAVORITE_LIST, null);
        Type type = new TypeToken<ArrayList<Track>>() {}.getType();
        favoriteTracks = gson.fromJson(json, type);

        if(favoriteTracks == null){
            Gson updategson = new Gson();
            String updatejson = updategson.toJson(new ArrayList<Track>());

            editor.putString(FAVORITE_LIST, updatejson);

            editor.commit();
        }

    }

    public void searchResults(){
        if(!isConnectedOnline()){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        String searchText = searchString.getText().toString().trim();

        if(searchText.equals("")){
            Toast.makeText(this, "Please type a name to search!", Toast.LENGTH_SHORT).show();
        } else{
            Intent intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra(SEARCH_STRING_KEY, searchText);
            startActivity(intent);
        }
    }

    public void onSearchClick(View view){

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, 5001);
        } else {
            searchResults();
        }
    }
}
