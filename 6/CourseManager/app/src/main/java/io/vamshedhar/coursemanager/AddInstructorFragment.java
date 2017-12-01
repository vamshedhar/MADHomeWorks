package io.vamshedhar.coursemanager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class AddInstructorFragment extends MenuActionBar {

    private OnFragmentInteractionListener mListener;

    ImageView profilePic;
    EditText firstNameET, lastNameET, emailET, websiteET;
    Button registerBtn, resetBtn;
    String imageAbsolutePath;

    User currentUser;

    public AddInstructorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.addInstructor).setVisible(false);
    }

    public void reset(){
        firstNameET.setText("");
        lastNameET.setText("");
        emailET.setText("");
        websiteET.setText("");

        profilePic.setImageResource(R.drawable.profile_image);

        imageAbsolutePath = "";
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean isValidURL(String url) {

        String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https", "ftp"
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(url)) {
            return true;
        }

        return false;
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

        if (!isValidEmail(emailET.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please enter valid email address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        String url = websiteET.getText().toString().trim();

        if (!url.isEmpty() && !url.startsWith("http://") && !url.startsWith("https://")){
            url = "http://" + url;
        }

        if (!url.isEmpty() && !isValidURL(url)){
            Toast.makeText(getActivity(), "Please enter valid web link!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_add_instructor, container, false);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Add Instructor");

        profilePic = getView().findViewById(R.id.registerProfilePic);
        firstNameET = getView().findViewById(R.id.firstNameET);
        lastNameET = getView().findViewById(R.id.lastNameET);
        emailET = getView().findViewById(R.id.emailET);
        websiteET = getView().findViewById(R.id.websiteET);
        registerBtn = getView().findViewById(R.id.registerBtn);
        resetBtn = getView().findViewById(R.id.resetBtn);

        reset();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Do you want to reset all fields?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reset();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        final AlertDialog resetAlert = builder.create();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAlert.show();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidData()){
                    final Realm realm = Realm.getDefaultInstance();

                    final String email = emailET.getText().toString().trim();

                    SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.TAG, getActivity().MODE_PRIVATE);
                    final String username = pref.getString(MainActivity.SP_USERNAME, null);

                    currentUser = realm.where(User.class)
                            .equalTo("username", username)
                            .findFirst();

                    RealmResults<Instructor> instructors = realm.where(Instructor.class)
                            .equalTo("email", email, Case.INSENSITIVE)
                            .equalTo("user.username", currentUser.getUsername())
                            .findAll();


                    if (instructors.size() > 0){
                        Toast.makeText(getActivity(), "Instructor with same email id already exists!", Toast.LENGTH_LONG).show();
                    } else{
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                Instructor instructor = new Instructor();

                                User user = bgRealm.where(User.class)
                                        .equalTo("username", username)
                                        .findFirst();

                                int nextID = 1;
                                if (bgRealm.where(Instructor.class).findAll().size() != 0){
                                    nextID = bgRealm.where(Instructor.class).max("id").intValue() + 1;
                                }

                                String url = websiteET.getText().toString().trim();

                                if (!url.startsWith("http://") && !url.startsWith("https://")){
                                    url = "http://" + url;
                                }

                                instructor.setId(nextID);
                                instructor.setFirstName(firstNameET.getText().toString().trim());
                                instructor.setLastName(lastNameET.getText().toString().trim());
                                instructor.setEmail(email);
                                instructor.setWebsite(url);
                                instructor.setUser(user);
                                instructor.setProfilePic(imageAbsolutePath);

                                bgRealm.copyToRealm(instructor);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                reset();
                                realm.close();
                                Toast.makeText(getActivity(), "Instructor added successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {

                                error.printStackTrace();
                                Toast.makeText(getActivity(), "Unknown Error occurred!", Toast.LENGTH_LONG).show();

                                realm.close();
                            }
                        });
                    }


                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5002);
                } else {
                    takePhoto();
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
