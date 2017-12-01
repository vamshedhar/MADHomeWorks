package io.vamshedhar.mysocial.objects;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/18/17 8:12 PM.
 * vchinta1@uncc.edu
 */

public class Post extends CreatedTimeObject implements Comparable<Post> {
    String id, content, userId;
    User user;

    public Post() {

    }

    public Post(String id, String content, User user) {
        super();
        this.id = id;
        this.content = content;
        this.userId = user.getId();
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public int compareTo(@NonNull Post post) {
        if (this.getCreateTimestamp() < post.getCreateTimestamp()){
            return -1;
        } else if (this.getCreateTimestamp() > post.getCreateTimestamp()){
            return 1;
        }
        return 0;
    }
}
