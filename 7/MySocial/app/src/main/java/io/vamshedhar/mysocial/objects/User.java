package io.vamshedhar.mysocial.objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/18/17 3:49 PM.
 * vchinta1@uncc.edu
 */

public class User extends CreatedTimeObject {
    String id, email, fullName, profilePicUrl, birthday;

    @Exclude
    String type;


    public User() {
    }

    public User(String id, String email, String fullName, String profilePicUrl, String birthday) {
        super();
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.profilePicUrl = profilePicUrl;
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Exclude
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return fullName + " : " + email;
    }
}
