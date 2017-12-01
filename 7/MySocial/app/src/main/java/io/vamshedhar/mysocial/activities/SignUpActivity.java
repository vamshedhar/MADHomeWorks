package io.vamshedhar.mysocial.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;

import io.vamshedhar.mysocial.R;
import io.vamshedhar.mysocial.objects.User;

public class SignUpActivity extends AppCompatActivity {

    public static final String EDIT_USER = "EDIT_USER";

    EditText fullNameET, emailET, passwordET, repeatPasswordET, birthday;
    Button signupBtn, cancelBtn;

    TextInputLayout passwordWrap, rPasswordWrap;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRef;

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener datepicker;

    User oldUser;

    public boolean isConnectedOnline(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnected()){
            return true;
        }

        Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private int getAge(String dateString){
        
        int year = Integer.parseInt(dateString.substring(6));
        int month = Integer.parseInt(dateString.substring(0, 2));
        int day = Integer.parseInt(dateString.substring(3, 5));
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month - 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (age != 0 && today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }

    public boolean isValidDate(String date){

        if (date.isEmpty() || getAge(date) < 13){
            return false;
        }

        return true;
    }


    public boolean isValidData(){

        if (fullNameET.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter valid name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!MainActivity.isValidEmail(emailET.getText().toString().trim())){
            Toast.makeText(this, "Please enter valid email!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidDate(birthday.getText().toString())){
            Toast.makeText(this, "Please enter birthday. You must be minimum 13 years of age!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (oldUser == null){
            if (passwordET.getText().toString().trim().length() < 6){
                Toast.makeText(this, "Password should contain minimum 6 characters!", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!passwordET.getText().toString().trim().equals(repeatPasswordET.getText().toString().trim())){
                Toast.makeText(this, "Password and Repeat Password do not match", Toast.LENGTH_SHORT).show();
                return false;
            }
        }


        return true;
    }

    public void loadUserData(){
        fullNameET.setText(oldUser.getFullName());
        birthday.setText(oldUser.getBirthday());
        emailET.setText(oldUser.getEmail());

        emailET.setEnabled(false);

        passwordWrap.setVisibility(View.INVISIBLE);
        rPasswordWrap.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setIcon(R.drawable.app_icon_icon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fullNameET = findViewById(R.id.fullNameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        repeatPasswordET = findViewById(R.id.repeatPasswordET);
        birthday = findViewById(R.id.birthday);
        passwordWrap = findViewById(R.id.passwordWrap);
        rPasswordWrap = findViewById(R.id.rPasswordWrap);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EDIT_USER)){
            oldUser = (User) getIntent().getSerializableExtra(EDIT_USER);
            loadUserData();
        } else{
            oldUser = null;
        }

        datepicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                birthday.setText(sdf.format(myCalendar.getTime()));

                Log.d(MainActivity.TAG, getAge(birthday.getText().toString()) + "");
            }

        };

        birthday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    DatePickerDialog dialog = new DatePickerDialog(SignUpActivity.this, datepicker, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));

                    dialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, 1850);
                    c.set(Calendar.MONTH, 0);
                    c.set(Calendar.DAY_OF_MONTH, 1);
                    dialog.getDatePicker().setMinDate(c.getTimeInMillis() - 1000);
                    dialog.show();
                }
                return true;
            }
        });

        signupBtn = findViewById(R.id.signupBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        mAuth = FirebaseAuth.getInstance();
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    private void saveUserData(){
        FirebaseUser authUser = mAuth.getCurrentUser();
        String fullName = fullNameET.getText().toString().trim();

        DatabaseReference userRef = mUsersRef.child(authUser.getUid());

        String profilePicUrl = "";

        if (oldUser != null){
            profilePicUrl = oldUser.getProfilePicUrl();
        }

        if(oldUser == null){
            User user = new User(authUser.getUid(), authUser.getEmail(), fullName, profilePicUrl, birthday.getText().toString());

            userRef.setValue(user);
            Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show();
        } else {
            userRef.child("fullName").setValue(fullName);
            userRef.child("birthday").setValue(birthday.getText().toString());
            Toast.makeText(this, "User updated successfully!", Toast.LENGTH_SHORT).show();
        }


        finish();
    }

    public void onSignUpClick(View view){
        if (isValidData() && isConnectedOnline()){
            if (oldUser == null){
                String email = emailET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    saveUserData();
                                } else{
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else{
                saveUserData();
            }

        }
    }

    public void onCancelClick(View view){
        finish();
    }
}
