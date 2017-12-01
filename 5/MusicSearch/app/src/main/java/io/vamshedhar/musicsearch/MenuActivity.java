package io.vamshedhar.musicsearch;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/13/17 7:10 PM.
 * vchinta1@uncc.edu
 */

public class MenuActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.homeBtn:
                loadMainActivity();
                break;
            case R.id.quitBtn:
                quitApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void quitApp(){
        finishAffinity();
    }
}
