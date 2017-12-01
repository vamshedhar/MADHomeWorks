package io.vamshedhar.coursemanager;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PagerSnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;


public class RegisterFragment extends MenuActionBar {

    private OnFragmentInteractionListener mListener;

    ImageView profilePic;
    EditText firstNameET, lastNameET, usernameET, passwordET;
    Button registerBtn;
    String imageAbsolutePath;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.home).setVisible(false);
        menu.findItem(R.id.instructors).setVisible(false);
        menu.findItem(R.id.addInstructor).setVisible(false);
        menu.findItem(R.id.logout).setVisible(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MainActivity.REQUEST_IMAGE_CAPTURE){
            if(resultCode == getActivity().RESULT_OK){

                try {

                    Bitmap bmImg = BitmapFactory.decodeFile(imageAbsolutePath);
                    profilePic.setImageBitmap(bmImg);

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
                    Log.e("Camera", e.toString());
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5002){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                takePhoto();
            } else {
                Toast.makeText(getActivity(), "Please Grant required permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        imageAbsolutePath = image.getAbsolutePath();
        return image;
    }

    public void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if(photoFile != null) {
                Uri imageUri = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                        "io.vamshedhar.coursemanager.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, MainActivity.REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    public boolean isValidData(){
        if (firstNameET.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity(), "Please enter valid first name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lastNameET.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity(), "Please enter valid last name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (usernameET.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity(), "Please enter valid username!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordET.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity(), "Please enter valid password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordET.getText().toString().length() < 8){
            Toast.makeText(getActivity(), "Password should contain minimum 8 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Register");
        
        profilePic = getView().findViewById(R.id.registerProfilePic);
        firstNameET = getView().findViewById(R.id.registerFirstNameET);
        lastNameET = getView().findViewById(R.id.registerLastNameET);
        usernameET = getView().findViewById(R.id.registerUsernameET);
        passwordET = getView().findViewById(R.id.registerPasswordET);
        registerBtn = getView().findViewById(R.id.registerBtn);

        imageAbsolutePath = "";

        profilePic.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5002);
            } else {
                takePhoto();
            }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (isValidData()){
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                    User user = new User();
                    user.setFirstName(firstNameET.getText().toString().trim());
                    user.setLastName(lastNameET.getText().toString().trim());
                    user.setPassword(passwordET.getText().toString().trim());
                    user.setUsername(usernameET.getText().toString().trim());
                    user.setProfilePic(imageAbsolutePath);

                    bgRealm.copyToRealm(user);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.TAG, getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(MainActivity.SP_USERNAME, usernameET.getText().toString().trim());
                        editor.commit();

                        Toast.makeText(getActivity(), "User created successfully!", Toast.LENGTH_SHORT).show();

                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new CourseListFragment(), "courses_fragment")
                            .commit();
                        getFragmentManager().executePendingTransactions();
                        realm.close();

                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    if(error.getClass().getCanonicalName().contains("RealmPrimaryKeyConstraintException")){
                        Toast.makeText(getActivity(), "User with same username already exists!", Toast.LENGTH_LONG).show();
                    } else{
                        Toast.makeText(getActivity(), "Unknown Error occurred!", Toast.LENGTH_LONG).show();
                    }

                    realm.close();
                    }
                });
            }
            }
        });
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
