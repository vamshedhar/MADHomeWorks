package io.vamshedhar.searchwords;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class WordsFound extends AppCompatActivity {

    ArrayList<SearchResult> searchResults;
    HashMap<String, Integer> keyWordColors;

    LinearLayout searchResultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_found);

        searchResultsList = (LinearLayout) findViewById(R.id.searchResultsList);

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(MainActivity.SEARCH_RESULTS_KEY)){
            searchResults = (ArrayList<SearchResult>) getIntent().getSerializableExtra(MainActivity.SEARCH_RESULTS_KEY);
            keyWordColors = (HashMap<String, Integer>) getIntent().getSerializableExtra(MainActivity.SEARCH_RESULTS_COLORS);


            for (int i = 0; i < searchResults.size(); i++) {
                SearchResult result = searchResults.get(i);

                SearchResultItemUI SearchResultsView = new SearchResultItemUI(this);

                View SearchResultsItemView = SearchResultsView;

                SearchResultsView.startText.setText(getResources().getString(R.string.dots) + result.startString);
                SearchResultsView.keyWord.setText(result.keyWord);

                if(keyWordColors.containsKey(result.keyWord.trim().toLowerCase())){
                    SearchResultsView.keyWord.setTextColor(keyWordColors.get(result.keyWord.trim().toLowerCase()));
                } else{
                    Log.d(MainActivity.TAG, result.startString + " : " + result.keyWord + " : " + result.endString);
                }

                SearchResultsView.endText.setText(result.endString + getResources().getString(R.string.dots));

                searchResultsList.addView(SearchResultsItemView);
            }

        }
    }

    public void finishClick(View view){
        finish();
    }
}
