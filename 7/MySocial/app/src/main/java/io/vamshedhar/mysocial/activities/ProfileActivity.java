package io.vamshedhar.mysocial.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import io.vamshedhar.mysocial.R;
import io.vamshedhar.mysocial.adapters.CurrentUserPostListAdapter;
import io.vamshedhar.mysocial.objects.Post;
import io.vamshedhar.mysocial.objects.User;
import io.vamshedhar.mysocial.objects.UserWithFriends;
import io.vamshedhar.mysocial.util.BottomNavigationViewBehavior;
import io.vamshedhar.mysocial.util.BottomNavigationViewHelper;

public class ProfileActivity extends MenuActionBar implements CurrentUserPostListAdapter.CurrentUserPostListInterface {

    TextView userFullName;
    ImageView editUserBtn;
    BottomNavigationView bottomNavigation;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mPostsRef;
    private FirebaseUser currentFBUser;
    private UserWithFriends currentUser;

    private RecyclerView usersWall;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
        bottomNavigation.setSelectedItemId(R.id.profileBtn);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());
    }

    public void loadView(){
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        usersWall.setLayoutManager(mLayoutManager);

        DatabaseReference userRef = mDatabase.child("users").child(currentFBUser.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(UserWithFriends.class);
                userFullName.setText(currentUser.getFullName() + ",");
                if (currentUser.getFriends() == null){
                    currentUser.setFriends(new HashMap<String, User>());
                }
                loadPosts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void renderList(){
        adapter = new CurrentUserPostListAdapter(posts, this, this, currentUser);
        usersWall.setAdapter(adapter);
        Log.d(MainActivity.TAG, posts.size() + "");
    }

    public void loadPosts(){
        Query currentUserPostsRef = mPostsRef.orderByChild("userId").equalTo(currentUser.getId());
        currentUserPostsRef.addValueEventListener(new ValueEventListener() {
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

                    posts.add(post);
                }

                Collections.reverse(posts);

                renderList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setIcon(R.drawable.app_icon_icon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPostsRef = mDatabase.child("posts");

        userFullName = findViewById(R.id.userFullName);
        editUserBtn = findViewById(R.id.editBtn);

        editUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, SignUpActivity.class);
                intent.putExtra(SignUpActivity.EDIT_USER, currentUser);
                startActivity(intent);
            }
        });

        usersWall = findViewById(R.id.usersWall);

        loadBottomNavigation();

        checkIfValidUserSession();

        loadView();
    }

    public void deletePostObject(Post post){
        mPostsRef.child(post.getId()).removeValue();
    }

    @Override
    public void deletePost(final Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure you want to delete the post?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePostObject(post);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        final AlertDialog deleteAlert = builder.create();

        deleteAlert.show();

    }
}
