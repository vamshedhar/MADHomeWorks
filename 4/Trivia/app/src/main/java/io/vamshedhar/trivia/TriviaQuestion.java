package io.vamshedhar.trivia;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/27/17 11:13 PM.
 * vchinta1@uncc.edu
 */

public class TriviaQuestion implements Serializable {
    int id;
    String questionText;
    String image;
    String answer;
    ArrayList<String> choices;

    public TriviaQuestion(int id, String questionText, String image) {
        this.id = id;
        this.questionText = questionText;
        this.image = image;
        this.choices = new ArrayList<>();
    }

    public void addChoice(String choice){
        this.choices.add(choice);
    }

    public void setAnswer(int answerIndex){
        this.answer = this.choices.get(answerIndex);
    }
}
