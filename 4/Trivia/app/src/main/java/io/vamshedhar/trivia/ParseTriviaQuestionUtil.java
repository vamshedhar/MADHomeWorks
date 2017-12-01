package io.vamshedhar.trivia;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/27/17 11:19 PM.
 * vchinta1@uncc.edu
 */

public class ParseTriviaQuestionUtil {

    public static ArrayList<TriviaQuestion> ParseTriveQuestions(String data) throws JSONException, InterruptedException {
        ArrayList<TriviaQuestion> questions = new ArrayList<>();

        JSONObject root = new JSONObject(data);
        JSONArray results = root.getJSONArray("questions");

        for (int i = 0; i < results.length(); i++) {
            JSONObject question = results.getJSONObject(i);

            String image = "";

            if (question.has("image")){
                image =  question.getString("image");
            }

            TriviaQuestion QuestionObject = new TriviaQuestion(question.getInt("id"), question.getString("text"), image);

            JSONObject choiceObject = question.getJSONObject("choices");

            JSONArray choiceList = choiceObject.getJSONArray("choice");

            for (int j = 0; j < choiceList.length(); j++) {
                QuestionObject.addChoice(choiceList.getString(j));
            }

            Thread.sleep(500);

            QuestionObject.setAnswer(choiceObject.getInt("answer") - 1);

            questions.add(QuestionObject);
        }

        return questions;
    }
}
