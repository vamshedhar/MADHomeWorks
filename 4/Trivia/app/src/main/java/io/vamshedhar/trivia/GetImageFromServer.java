package io.vamshedhar.trivia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/28/17 11:18 AM.
 * vchinta1@uncc.edu
 */

public class GetImageFromServer extends AsyncTask<String, Void, Bitmap> {
    IImage activity;

    public GetImageFromServer(IImage activity) {
        this.activity = activity;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        RequestParams request = new RequestParams("GET", params[0]);

        try {
            HttpURLConnection con = request.setupConnection();
            Bitmap image = BitmapFactory.decodeStream(con.getInputStream());
            if(isCancelled()) return null;
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (!this.isCancelled()){
            activity.setImage(bitmap);
        }
    }

    interface IImage{
        void setImage(Bitmap image);
    }
}
