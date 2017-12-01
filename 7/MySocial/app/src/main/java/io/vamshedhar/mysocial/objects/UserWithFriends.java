package io.vamshedhar.mysocial.objects;

import java.util.HashMap;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/19/17 2:47 PM.
 * vchinta1@uncc.edu
 */

public class UserWithFriends extends User {
    HashMap<String, User> friends;
    HashMap<String, User> friendRequests;
    HashMap<String, User> friendRequestsSent;

    public UserWithFriends() {
        super();
    }

    public HashMap<String, User> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, User> friends) {
        this.friends = friends;
    }

    public HashMap<String, User> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(HashMap<String, User> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public HashMap<String, User> getFriendRequestsSent() {
        return friendRequestsSent;
    }

    public void setFriendRequestsSent(HashMap<String, User> friendRequestsSent) {
        this.friendRequestsSent = friendRequestsSent;
    }

    @Override
    public String toString() {
        return "UserWithFriends{" +
                "friends=" + friends +
                ", friendRequests=" + friendRequests +
                '}';
    }
}
