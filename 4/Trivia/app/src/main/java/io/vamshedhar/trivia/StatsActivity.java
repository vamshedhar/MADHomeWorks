package io.vamshedhar.trivia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {

    ProgressBar correctAnswersProgress;
    TextView percentageText;

    ArrayList<TriviaQuestion> questions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        correctAnswersProgress = (ProgressBar) findViewById(R.id.correctAnswersProgress);
        percentageText = (TextView) findViewById(R.id.percentageText);

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(TriviaActivity.SCORE_KEY)){
            int score = getIntent().getExtras().getInt(TriviaActivity.SCORE_KEY);
            questions = (ArrayList<TriviaQuestion>) getIntent().getSerializableExtra(MainActivity.TRIVIA_QUESTIONS_KEY);

            percentageText.setText(score + "%");

            correctAnswersProgress.setProgress(score);
        }
    }

    public void onStatsExitClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.TRIVIA_QUESTIONS_KEY, questions);
        startActivity(intent);
    }

    public void onTryAgainClick(View view){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
