package io.vamshedhar.mysocial.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import io.vamshedhar.mysocial.R;
import io.vamshedhar.mysocial.objects.User;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MySocial";
    public static final int RC_SIGN_IN = 1001;

    EditText usernameET, passwordET;
    TextView signupBtn;
    Button fbLoginBtn, googleLoginBtn, loginBtn;

    CallbackManager fbCallbackManager;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    String dateOfBirth;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            onGoogleLoginSuccess(task);
        }
    }

    public boolean isConnectedOnline(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnected()){
            return true;
        }

        Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean isValidData(){
        if (!isValidEmail(usernameET.getText().toString().trim())){
            Toast.makeText(this, "Please enter valid email!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordET.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter valid password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserData(FirebaseUser authUser){
        final DatabaseReference userRef = mDatabase.child("users").child(authUser.getUid());

        String profilePicUrl = "";

        if (authUser.getPhotoUrl() != null){
            profilePicUrl = authUser.getPhotoUrl().toString();
        }

        final User user = new User(authUser.getUid(), authUser.getEmail(), authUser.getDisplayName(), profilePicUrl, "");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null){
                    userRef.setValue(user);
                }

                goToHome();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void firebaseLoginWithCredentials(AuthCredential credential){
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    saveUserData(user);
                } else {
                    if (task.getException().getMessage().contains("An account already exists with the same email address")){
                        Toast.makeText(MainActivity.this, "An account already exists with the same email address. Use correct login method.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.d(MainActivity.TAG, "Authentication Failed!");
                        Toast.makeText(MainActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
                }
            });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseLoginWithCredentials(credential);
    }


    private void firebaseAuthWithFacebook(AccessToken token) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseLoginWithCredentials(credential);
//        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject object, GraphResponse response) {
//                try {
//                    dateOfBirth = object.getString("birthday");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                firebaseLoginWithCredentials(credential);
//            }
//        });
//
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name,email,gender,birthday");
//        request.setParameters(parameters);
//        request.executeAsync();

    }

    private void setupFacebookLogin(){
        fbCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(fbCallbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    firebaseAuthWithFacebook(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(MainActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                }
            });

        fbLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile", "user_birthday"));
            }
        });
    }

    private void onGoogleLoginSuccess(Task<GoogleSignInAccount> task){
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Toast.makeText(MainActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupGoogleLogin(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void firebaseAuthWithEmail(){
        String username = usernameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        AuthCredential credential = EmailAuthProvider.getCredential(username, password);

        firebaseLoginWithCredentials(credential);
    }

    private void setupEmailLogin(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidData() && isConnectedOnline()){
                    firebaseAuthWithEmail();
                }
            }
        });
    }

    private void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setIcon(R.drawable.app_icon_icon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fbLoginBtn = findViewById(R.id.fb_login_btn);
        googleLoginBtn = findViewById(R.id.google_login_btn);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);

        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        dateOfBirth = "";

        if (currentUser != null){
            goToHome();
        } else{
            setupFacebookLogin();
            setupGoogleLogin();
            setupEmailLogin();
        }

    }

    public void onSignupClick(View view){
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
