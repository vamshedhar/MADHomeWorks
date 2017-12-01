package io.vamshedhar.coursemanager;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/5/17 5:20 PM.
 * vchinta1@uncc.edu
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("coursemanager.realm").schemaVersion(2).build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }
}
