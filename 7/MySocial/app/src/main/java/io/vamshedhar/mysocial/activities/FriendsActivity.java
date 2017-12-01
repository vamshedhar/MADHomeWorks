package io.vamshedhar.mysocial.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import io.vamshedhar.mysocial.R;
import io.vamshedhar.mysocial.adapters.FriendsListAdapter;
import io.vamshedhar.mysocial.adapters.FriendsViewPagerAdapter;
import io.vamshedhar.mysocial.fragments.FriendsListFragment;
import io.vamshedhar.mysocial.objects.User;
import io.vamshedhar.mysocial.objects.UserWithFriends;
import io.vamshedhar.mysocial.util.BottomNavigationViewBehavior;
import io.vamshedhar.mysocial.util.BottomNavigationViewHelper;
import io.vamshedhar.mysocial.util.TabsViewBehaviour;

public class FriendsActivity extends MenuActionBar implements FriendsListAdapter.FriendsListInterface {

    public static final String FRIEND = "FRIEND";
    public static final String FRIEND_REQUESTED = "FRIEND_REQUEST_RECEIVED";
    public static final String FRIEND_REQUESTED_SENT = "FRIEND_REQUEST_SENT";
    public static final String NOT_FRIEND = "NOT_FRIEND";

    private TabLayout friendsTabsLayout;
    private ViewPager friendsContainer;
    private BottomNavigationView bottomNavigation;
    private TextView userFullName;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsersRef;
    private DatabaseReference mCurrentUserRef;
    private FirebaseUser currentFBUser;
    private UserWithFriends currentUser;

    private boolean layoutLoaded;

    FriendsListFragment freindsListFragment;
    FriendsListFragment addNewFriendsFragment;
    FriendsListFragment friendRequestsFragment;

    HashMap<String, User> userMap;

    public void loadBottomNavigation(){
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.friendsBtn);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation);
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigation.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationViewBehavior());
    }

    public void checkIfValidUserSession(){
        mAuth = FirebaseAuth.getInstance();
        currentFBUser = mAuth.getCurrentUser();
        if (currentFBUser == null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void loadUserData(){
        userFullName.setText(currentUser.getFullName() + ",");

        if (currentUser.getFriendRequests() == null){
            currentUser.setFriendRequests(new HashMap<String, User>());
        }

        if (currentUser.getFriends() == null){
            currentUser.setFriends(new HashMap<String, User>());
        }

        if (currentUser.getFriendRequestsSent() == null){
            currentUser.setFriendRequestsSent(new HashMap<String, User>());
        }

        getUsersList();
    }

    public void loadUser(){
        mUsersRef = mDatabase.child("users");
        mCurrentUserRef = mUsersRef.child(currentFBUser.getUid());

        mCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(UserWithFriends.class);
                loadUserData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void renderFriendsLists(ArrayList<User> usersList){
        ArrayList<User> friends = new ArrayList<>();
        ArrayList<User> friendRequests = new ArrayList<>();
        ArrayList<User> notFriends = new ArrayList<>();
        userMap = new HashMap<>();

        for (User user : usersList){
            userMap.put(user.getId(), user);
            if (currentUser.getFriends().containsKey(user.getId())){
                user.setType(FRIEND);
                friends.add(user);
            } else if (currentUser.getFriendRequests().containsKey(user.getId())){
                user.setType(FRIEND_REQUESTED);
                friendRequests.add(user);
            } else if (currentUser.getFriendRequestsSent().containsKey(user.getId())){
                user.setType(FRIEND_REQUESTED_SENT);
                friendRequests.add(user);
            } else{
                user.setType(NOT_FRIEND);
                notFriends.add(user);
            }
        }

        freindsListFragment.loadUsers(friends, layoutLoaded);
        addNewFriendsFragment.loadUsers(notFriends, layoutLoaded);
        friendRequestsFragment.loadUsers(friendRequests, layoutLoaded);

        if (!layoutLoaded){
            loadTabLayout();
            layoutLoaded = true;
        }


    }

    public void getUsersList(){

        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> usersList = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    if (!user.getId().equals(currentUser.getId())){
                        usersList.add(user);
                    }
                }

                renderFriendsLists(usersList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadTabLayout(){
        setupViewPager(friendsContainer);
        friendsTabsLayout.setupWithViewPager(friendsContainer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        getSupportActionBar().setIcon(R.drawable.app_icon_icon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        layoutLoaded = false;

        checkIfValidUserSession();
        loadBottomNavigation();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        friendsTabsLayout = findViewById(R.id.friendsTabLayout);
        friendsContainer = findViewById(R.id.firendsContainer);

        freindsListFragment = new FriendsListFragment();
        addNewFriendsFragment = new FriendsListFragment();
        friendRequestsFragment = new FriendsListFragment();

        userFullName = findViewById(R.id.userFullName);

//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) friendsTabsLayout.getLayoutParams();
//        layoutParams.setBehavior(new TabsViewBehaviour());

        loadUser();

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void setupViewPager(ViewPager viewPager) {
        FriendsViewPagerAdapter adapter = new FriendsViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(freindsListFragment, getResources().getString(R.string.friends));
        adapter.addFragment(addNewFriendsFragment, getResources().getString(R.string.add_friends));
        adapter.addFragment(friendRequestsFragment, getResources().getString(R.string.friend_requests));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void addFriend(String id) {
        User friend = userMap.get(id);
        DatabaseReference friendRef = mUsersRef.child(id);
        friendRef.child("friendRequests").child(currentUser.getId()).setValue(friend);

        mCurrentUserRef.child("friendRequestsSent").child(id).setValue(friend);
    }

    @Override
    public void acceptFriendRequest(String id) {
        User friend = userMap.get(id);
        mCurrentUserRef.child("friendRequests").child(id).removeValue();
        mCurrentUserRef.child("friends").child(id).setValue(currentUser);

        DatabaseReference friendRef = mUsersRef.child(id);
        friendRef.child("friends").child(currentUser.getId()).setValue(friend);
        friendRef.child("friendRequestsSent").child(currentUser.getId()).removeValue();
    }

    @Override
    public void rejectFriendRequest(String id) {
        mCurrentUserRef.child("friendRequests").child(id).removeValue();

        DatabaseReference friendRef = mUsersRef.child(id);
        friendRef.child("friendRequestsSent").child(currentUser.getId()).removeValue();
    }

    @Override
    public void removeFriend(String id) {
        mCurrentUserRef.child("friends").child(id).removeValue();

        DatabaseReference friendRef = mUsersRef.child(id);
        friendRef.child("friends").child(currentUser.getId()).removeValue();
    }

    @Override
    public void cancelFriendRequest(String id) {
        DatabaseReference friendRef = mUsersRef.child(id);
        friendRef.child("friendRequests").child(currentUser.getId()).removeValue();

        mCurrentUserRef.child("friendRequestsSent").child(id).removeValue();
    }
}
