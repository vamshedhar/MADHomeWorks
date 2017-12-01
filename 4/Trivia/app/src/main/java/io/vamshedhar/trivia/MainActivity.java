package io.vamshedhar.trivia;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GetDataFromServer.IData {

    public static final String TAG = "Trivia";
    public static final String TRIVIA_QUESTIONS_KEY = "TRIVIA_QUESTIONS";
    public static final int PERMISSIONS_CODE = 5001;
    public final String BASE_URL = "http://dev.theappsdr.com/apis/trivia_json/index.php";

    ImageView mainImage;
    ProgressBar pb;
    Button startBtn, exitBtn;
    TextView loadingText, readyText;

    ArrayList<TriviaQuestion> questions;

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
        if (requestCode == PERMISSIONS_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                new GetDataFromServer(BASE_URL, this).execute();
            } else {
                Toast.makeText(MainActivity.this, "Please Grant required permissions!", Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.INVISIBLE);
                loadingText.setText("Please grant required permissions!");
                startBtn.setText(getResources().getString(R.string.tryAgain));
                startBtn.setEnabled(true);
            }
        }
    }

    public void loadData(){
        if (isConnectedOnline()){
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, PERMISSIONS_CODE);
            } else {
                new GetDataFromServer(BASE_URL, this).execute();
            }
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.INVISIBLE);
            loadingText.setText(getResources().getString(R.string.noNetwork));
            startBtn.setText(getResources().getString(R.string.tryAgain));
            startBtn.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainImage = (ImageView) findViewById(R.id.mainImage);

        pb = (ProgressBar) findViewById(R.id.loadingProgress);

        startBtn = (Button) findViewById(R.id.startBtn);
        exitBtn = (Button) findViewById(R.id.exitBtn);

        loadingText = (TextView) findViewById(R.id.loadingText);
        readyText = (TextView) findViewById(R.id.readyText);

        startBtn.setEnabled(false);

        questions = new ArrayList<>();

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(TRIVIA_QUESTIONS_KEY)){
            setupData((ArrayList<TriviaQuestion>) getIntent().getSerializableExtra(TRIVIA_QUESTIONS_KEY));
        } else{
            loadData();
        }
    }

    @Override
    public void setupData(ArrayList<TriviaQuestion> questions) {
        pb.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);
        startBtn.setEnabled(true);
        mainImage.setVisibility(View.VISIBLE);
        readyText.setVisibility(View.VISIBLE);

        this.questions = questions;
    }

    public void onExitClick(View view){
        finish();
    }

    public void startTrivia(View view){
        if(startBtn.getText().equals(getResources().getString(R.string.tryAgain))){
            pb.setVisibility(View.VISIBLE);
            loadingText.setText(getResources().getString(R.string.loadingTrivia));
            startBtn.setText(getResources().getString(R.string.startTrivia));
            startBtn.setEnabled(false);
            loadData();
        } else{
            Intent intent = new Intent(this, TriviaActivity.class);
            intent.putExtra(TRIVIA_QUESTIONS_KEY, questions);
            startActivity(intent);
        }
    }
}
