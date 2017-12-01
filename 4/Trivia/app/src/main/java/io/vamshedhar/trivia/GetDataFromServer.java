package io.vamshedhar.trivia;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/27/17 10:51 PM.
 * vchinta1@uncc.edu
 */

public class GetDataFromServer extends AsyncTask<Void, Void, ArrayList<TriviaQuestion>> {
    String BASE_URL;
    IData activity;

    public GetDataFromServer(String BASE_URL, IData activity) {
        this.BASE_URL = BASE_URL;
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(ArrayList<TriviaQuestion> questions) {
        super.onPostExecute(questions);
        activity.setupData(questions);
    }

    @Override
    protected ArrayList<TriviaQuestion> doInBackground(Void... voids) {
        RequestParams request = new RequestParams("GET", BASE_URL);
        ArrayList<TriviaQuestion> question = new ArrayList<>();

        try {
            HttpURLConnection con = request.setupConnection();
            con.connect();
            int statusCode = con.getResponseCode();
            if(statusCode == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder builder = new StringBuilder();

                String line = reader.readLine();

                while (line != null){
                    builder.append(line);
                    line = reader.readLine();
                }
                Log.d(MainActivity.TAG, builder.toString());

                question =  ParseTriviaQuestionUtil.ParseTriveQuestions(builder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return question;
    }

    public interface IData{
        void setupData(ArrayList<TriviaQuestion> questions);
    }
}
