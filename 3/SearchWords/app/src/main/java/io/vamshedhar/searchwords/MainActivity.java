package io.vamshedhar.searchwords;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "WordSearch";

    public static final String READ_FILE = "READ_FILE";
    public static final String SEARCH_RESULTS = "SEARCH_RESULTS";
    public static final String SEARCH_RESULTS_KEY = "SEARCH_RESULTS_KEY";
    public static final String SEARCH_RESULTS_COLORS = "SEARCH_RESULTS_COLORS";

    ArrayList<String> fileLines;

    ArrayList<SearchResult> allSearchResults;
    ArrayList<String> keyWords;

    HashMap<String, Integer> keyWordColors;

    EditText newKeyWord;
    LinearLayout KeyWordList;
    Button searchBtn;
    ProgressBar pb;
    CheckBox matchCases;

    Handler handler;

    public void showSearchResults(){
        keyWordColors = new HashMap<>();

        int[] colors = {Color.RED, Color.GREEN, Color.BLUE};

        for (int i = 0; i < keyWords.size(); i++) {
            keyWordColors.put(keyWords.get(i).trim().toLowerCase(), colors[i % 3]);
        }

        Collections.sort(allSearchResults);

        pb.setVisibility(View.GONE);
        enableButtons(true);

        Intent intent = new Intent(MainActivity.this, WordsFound.class);
        intent.putExtra(SEARCH_RESULTS_KEY, allSearchResults);
        intent.putExtra(SEARCH_RESULTS_COLORS, keyWordColors);
        startActivity(intent);
    }

    private class LoadDataFromFile implements Runnable{

        public void sendMsg() {
            Bundle bundle = new Bundle();
            bundle.putBoolean("READ_FILE", true);

            Message message = new Message();
            message.setData(bundle);
            handler.sendMessage(message);
        }

        @Override
        public void run() {
            try{
                InputStream is =  getAssets().open("textfile.txt");
                fileLines = Util.getDataFromFile(is);
                sendMsg();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private class SearchWordInFile implements Runnable{

        String keyWord;
        boolean matchCases;
        ArrayList<String> fileLines;

        public SearchWordInFile(ArrayList fileLines, String keyWord, boolean matchCases) {
            this.keyWord = keyWord;
            this.matchCases = matchCases;
            this.fileLines = fileLines;
        }

        public void sendMsg() {
            Bundle bundle = new Bundle();
            bundle.putString("SEARCH_RESULTS", keyWord);

            Message message = new Message();
            message.setData(bundle);
            handler.sendMessage(message);
        }

        @Override
        public void run() {
            ArrayList<SearchResult> searchResult = Util.wordSearch(fileLines, keyWord, this.matchCases);
            allSearchResults.addAll(searchResult);
            sendMsg();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allSearchResults = new ArrayList<>();
        keyWords = new ArrayList<>();

        newKeyWord = (EditText) findViewById(R.id.newKeyWord);
        
        KeyWordList = (LinearLayout) findViewById(R.id.keyWordList);

        searchBtn = (Button) findViewById(R.id.searchBtn);

        matchCases = (CheckBox) findViewById(R.id.matchCases);
        
        pb = (ProgressBar) findViewById(R.id.searchProgress);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {

                if(message.getData().containsKey(READ_FILE)){
                    ExecutorService taskPool = Executors.newFixedThreadPool(1);
                    for (String keyWord : keyWords) {
                        taskPool.execute(new Thread(new SearchWordInFile(fileLines, keyWord, matchCases.isChecked())));
                    }
                }

                if(message.getData().containsKey(SEARCH_RESULTS)){
                    Log.d(TAG, message.getData().getString(SEARCH_RESULTS) + ": " + allSearchResults.size());
                    pb.setProgress(pb.getProgress() + 1);

                    if(pb.getProgress() == keyWords.size()){
                        showSearchResults();
                    }
                }

                return true;
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allSearchResults = new ArrayList<>();
                keyWords = new ArrayList<>();
                for (int i = 0; i < KeyWordList.getChildCount() - 1; i++) {
                    LinearLayout KWItem = (LinearLayout) KeyWordList.getChildAt(i);
                    LinearLayout KWItemContainer = (LinearLayout) KWItem.getChildAt(0);
                    EditText KWEditText = (EditText) KWItemContainer.getChildAt(0);

                    String keyWord =  KWEditText.getText().toString().trim();

                    if(!keyWord.equals("")){
                        keyWords.add(keyWord);
                    }

                }

                if(keyWords.size() > 0){
                    pb.setVisibility(View.VISIBLE);
                    pb.setMax(keyWords.size());
                    pb.setProgress(0);

                    enableButtons(false);

                    fileLines = new ArrayList<>();

                    Thread readData = new Thread(new LoadDataFromFile());
                    readData.start();
                } else{
                    Toast.makeText(MainActivity.this, "Add atleast one key word", Toast.LENGTH_SHORT).show();
                }



            }
        });


    }

    public void enableButtons(boolean enable){
        searchBtn.setEnabled(enable);
        matchCases.setEnabled(enable);

        if(enable){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            View focusView = MainActivity.this.getCurrentFocus();
            if (focusView != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }


    }

    public void addKeyWord(View view){
        String keyWord = newKeyWord.getText().toString();

        if(!keyWord.trim().equals("")){
            KeyWordItemUI KewWordView = new KeyWordItemUI(this);

            View KewWordItemView = KewWordView;

            KewWordView.keyWord.setText(keyWord);

            KewWordView.removeKeywordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyWordList.removeView((View) view.getParent().getParent());
                }
            });

            KeyWordList.addView(KewWordItemView, KeyWordList.getChildCount() - 1);

            newKeyWord.setText("");
        }

    }

}
