package io.vamshedhar.trivia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TriviaActivity extends AppCompatActivity implements GetImageFromServer.IImage {

    public static final String SCORE_KEY = "SCORE";
    public static final int STATS_RESULT_CODE = 1001;

    TextView questionNoText, timerText, questionText;
    RadioGroup choicesGroup;
    ImageView questionImage;

    ProgressBar imageLoader;

    Button nextButton;

    ArrayList<TriviaQuestion> questions;

    CountDownTimer timer;

    GetImageFromServer LoadingImageTask;

    int currentQuestionIndex, score, checkedId;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == STATS_RESULT_CODE){
            if (resultCode == RESULT_OK){
                score = 0;
                currentQuestionIndex = 0;
                nextButton.setText(getResources().getString(R.string.next));
                loadQuestion(currentQuestionIndex);
                timer.start();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        questionNoText = (TextView) findViewById(R.id.questionNoText);
        timerText = (TextView) findViewById(R.id.timerText);
        questionText = (TextView) findViewById(R.id.questionText);
        nextButton = (Button) findViewById(R.id.nextBtn);

        choicesGroup = (RadioGroup) findViewById(R.id.choicesGroup);

        questionImage = (ImageView) findViewById(R.id.questionImage);

        imageLoader = (ProgressBar) findViewById(R.id.imageLoder);

        questions = new ArrayList<>();

        timer = new CountDownTimer(121000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time Left: " + millisUntilFinished/1000 + " seconds");
            }

            @Override
            public void onFinish() {
                quizComplete();
            }
        };

        choicesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                checkedId = i;
            }
        });

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(MainActivity.TRIVIA_QUESTIONS_KEY)){
            questions = (ArrayList<TriviaQuestion>) getIntent().getSerializableExtra(MainActivity.TRIVIA_QUESTIONS_KEY);
            score = 0;
            currentQuestionIndex = 0;
            loadQuestion(currentQuestionIndex);
            timer.start();
        } else {
            Toast.makeText(this, "No questions to load.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void checkAnswer(){
        TriviaQuestion question = questions.get(currentQuestionIndex);

        if(checkedId != -1){
            RadioButton selectedAnswer = (RadioButton) findViewById(checkedId);

            if(selectedAnswer.getText().equals(question.answer)){
                score = score + 1;
            }
        }

    }

    public void loadQuestion(int questionIndex){
        TriviaQuestion question = questions.get(questionIndex);

        questionText.setText(question.questionText);
        questionNoText.setText("Q" + (questionIndex + 1));

        ArrayList<String> choices = question.choices;

        choicesGroup.removeAllViews();

        checkedId = -1;

        for (int i = 0; i < choices.size(); i++) {
            RadioButton choice = new RadioButton(this);
            choice.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            choice.setText(choices.get(i));
            choice.setId(View.generateViewId());
            choice.setPadding(0, 10, 0, 10);
            choicesGroup.addView(choice);
        }

        if(LoadingImageTask != null){
            LoadingImageTask.cancel(true);
        }

        if(question.image.equals("")){
            questionImage.setImageResource(R.drawable.no_image);
            imageLoader.setVisibility(View.INVISIBLE);
            questionImage.setVisibility(View.VISIBLE);
            LoadingImageTask = null;
        } else {
            questionImage.setVisibility(View.INVISIBLE);
            imageLoader.setVisibility(View.VISIBLE);

            LoadingImageTask = new GetImageFromServer(this);

            LoadingImageTask.execute(question.image);
        }
    }


    public void onTriviaExitClick(View view){
        timer.cancel();
        finish();
    }

    public void quizComplete(){
        Intent intent = new Intent(this, StatsActivity.class);
        int percentageScore = Math.round((float) score * 100/(float) questions.size());
        intent.putExtra(SCORE_KEY, percentageScore);
        intent.putExtra(MainActivity.TRIVIA_QUESTIONS_KEY, questions);
        startActivityForResult(intent, STATS_RESULT_CODE);
    }

    public void onNextClick(View view){
        checkAnswer();

        if(currentQuestionIndex + 1 < questions.size()){
            currentQuestionIndex = currentQuestionIndex + 1;
            loadQuestion(currentQuestionIndex);

            if(currentQuestionIndex == questions.size() - 1){
                nextButton.setText(getResources().getString(R.string.finish));
            }
        } else if(currentQuestionIndex + 1 == questions.size()){
            timer.cancel();
            quizComplete();
        }

    }

    @Override
    public void setImage(Bitmap image) {

        if (image != null){
            questionImage.setImageBitmap(image);
        } else {
            questionImage.setImageResource(R.drawable.no_image);
        }
        imageLoader.setVisibility(View.INVISIBLE);
        questionImage.setVisibility(View.VISIBLE);
    }
}
