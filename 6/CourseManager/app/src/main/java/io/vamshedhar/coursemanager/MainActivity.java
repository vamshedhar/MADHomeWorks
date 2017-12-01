package io.vamshedhar.coursemanager;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener {

    public static final String TAG = "CourseManager";
    public static final String SP_USERNAME = "USERNAME";
    public static final int REQUEST_IMAGE_CAPTURE = 102;
    Realm realm;
    LinearLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        SharedPreferences pref = getApplicationContext().getSharedPreferences(MainActivity.TAG, MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString(MainActivity.SP_USERNAME, null);
//        editor.commit();
        String username = pref.getString(SP_USERNAME, null);

        if (username == null || username.equals("")){
            fragmentContainer = (LinearLayout) findViewById(R.id.fragmentContainer);

            getFragmentManager().beginTransaction().add(R.id.fragmentContainer, new LoginFragment(), "main_fragment").commit();
        } else {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new CourseListFragment(), "courses_fragment")
                    .commit();
            getFragmentManager().executePendingTransactions();
        }


    }

    @Override
    public void onLoginClicked() {

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
