package io.vamshedhar.mysocial.objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/18/17 8:16 PM.
 * vchinta1@uncc.edu
 */

public class CreatedTimeObject implements Serializable {
    HashMap<String, Object> createdTime;

    public CreatedTimeObject() {
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.createdTime = timestampNow;
    }

    public HashMap<String, Object> getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(HashMap<String, Object> createdTime) {
        this.createdTime = createdTime;
    }

    @Exclude
    public long getCreateTimestamp(){
        return (long) createdTime.get("timestamp");
    }
}
