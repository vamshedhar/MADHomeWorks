package io.vamshedhar.mysocial.activities;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import io.vamshedhar.mysocial.R;
import io.vamshedhar.mysocial.adapters.PostListAdapter;
import io.vamshedhar.mysocial.objects.Post;
import io.vamshedhar.mysocial.objects.User;
import io.vamshedhar.mysocial.objects.UserWithFriends;
import io.vamshedhar.mysocial.util.BottomNavigationViewBehavior;
import io.vamshedhar.mysocial.util.BottomNavigationViewHelper;

public class HomeActivity extends MenuActionBar implements PostListAdapter.PostListInterface {

    TextView userFullName, friendName;
    EditText newPostET;
    ImageView postBtn;
    BottomNavigationView bottomNavigation;
    RelativeLayout newPostWrapper;
    RelativeLayout userWallWrapper;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mPostsRef;
    private FirebaseUser currentFBUser;
    private UserWithFriends currentUser;

    private RecyclerView usersWall;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    HashMap<String, User> usersMap;

    User friend;

    ArrayList<Post> posts;

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

    public void loadBottomNavigation(){
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.homeBtn);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());
    }

    public void loadView(){
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        usersWall.setLayoutManager(mLayoutManager);

        DatabaseReference userRef = mDatabase.child("users");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersMap = new HashMap<>();

                for (DataSnapshot userSnap : dataSnapshot.getChildren()){
                    if (userSnap.getKey().equals(currentFBUser.getUid())){
                        currentUser = userSnap.getValue(UserWithFriends.class);
                        userFullName.setText(currentUser.getFullName() + ",");
                        if (currentUser.getFriends() == null){
                            currentUser.setFriends(new HashMap<String, User>());
                        }
                        usersMap.put(currentUser.getId(), currentUser);
                    } else {
                        User u = userSnap.getValue(User.class);
                        usersMap.put(u.getId(), u);
                    }
                }

                loadPosts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void reset(){
        newPostET.setText("");
    }

    public void onPostBtnClick(View view){
        String content = newPostET.getText().toString().trim();

        if (!content.isEmpty()){
            String id = mPostsRef.push().getKey();
            Post post = new Post(id, content, currentUser);

            mPostsRef.child(id).setValue(post);

            reset();
        }
    }

    public void renderList(){
        if (friend == null){
            adapter = new PostListAdapter(posts, this, this, usersMap);
            usersWall.setAdapter(adapter);
        } else {
            ArrayList<Post> friendsPosts = new ArrayList<>();

            for (Post post : posts){
                if (friend.getId().equals(post.getUserId())){
                    friendsPosts.add(post);
                }

                adapter = new PostListAdapter(friendsPosts, this, this, usersMap);
                usersWall.setAdapter(adapter);
            }
        }

    }

    public void loadPosts(){
        mPostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts = new ArrayList<>();
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_YEAR, -2);
                Date date = c.getTime();



                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    // Skip Posts older than 2 days
                    Post post = postSnapshot.getValue(Post.class);
                    Date postDate = new Date(post.getCreateTimestamp());

                    if (postDate.compareTo(date) < 0){
                        continue;
                    }


                    if (post.getUserId().equals(currentUser.getId()) || currentUser.getFriends().containsKey(post.getUserId())){
                        posts.add(post);
                    }
                }

                Collections.reverse(posts);

                renderList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void showNewPost(){
        newPostWrapper.setVisibility(View.VISIBLE);
        userWallWrapper.setVisibility(View.INVISIBLE);
    }

    public void showWallTitle(String friendNameValue){
        newPostWrapper.setVisibility(View.INVISIBLE);
        userWallWrapper.setVisibility(View.VISIBLE);

        friendName.setText(friendNameValue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setIcon(R.drawable.app_icon_icon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        friend = null;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPostsRef = mDatabase.child("posts");

        userFullName = findViewById(R.id.userFullName);
        newPostET = findViewById(R.id.newPostET);
        postBtn = findViewById(R.id.postBtn);
        friendName = findViewById(R.id.friendName);

        newPostWrapper = findViewById(R.id.newPostWrapper);
        userWallWrapper = findViewById(R.id.userWallWrapper);

        usersWall = findViewById(R.id.usersWall);

        showNewPost();

        loadBottomNavigation();

        checkIfValidUserSession();

        loadView();

    }

    public void showUserProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onCurrentUserClick(View view){
        showUserProfile();
    }

    @Override
    public void showWall(User user) {
        friend = user;
        if (friend.getId().equals(currentUser.getId())){
            showUserProfile();
        } else {
            showWallTitle(friend.getFullName());
            renderList();
        }
    }

    @Override
    public void onBackPressed() {
        if (friend != null){
            friend = null;
            showNewPost();
            renderList();
        } else {
            super.onBackPressed();
        }

    }
}
