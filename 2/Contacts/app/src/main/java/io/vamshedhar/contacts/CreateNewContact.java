package io.vamshedhar.contacts;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateNewContact extends AppCompatActivity {

    public static final int REQUEST_IMAGE_CAPTURE = 102;

    EditText firstName, lastName, company, email, phone,
            url, address, birthday, nickname, fbURL,
            twitterURL, skype, youtubeChannel;
    ImageView profilePic;

    String imageAbsolutePath = "";

    Calendar myCalendar = Calendar.getInstance();

    Contact oldContact;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5002){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                takePhoto();
            } else {
                Toast.makeText(CreateNewContact.this, "Please Grant required permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        imageAbsolutePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_contact);

        firstName = (EditText) findViewById(R.id.firstNameET);
        lastName = (EditText) findViewById(R.id.lastNameET);
        company = (EditText) findViewById(R.id.companyET);
        email = (EditText) findViewById(R.id.emailET);
        phone = (EditText) findViewById(R.id.phoneET);
        url = (EditText) findViewById(R.id.urlET);
        address = (EditText) findViewById(R.id.addressET);
        birthday = (EditText) findViewById(R.id.birthdayET);
        nickname = (EditText) findViewById(R.id.nickNameET);
        fbURL = (EditText) findViewById(R.id.fbURLET);
        twitterURL = (EditText) findViewById(R.id.twitterURLET);
        skype = (EditText) findViewById(R.id.skypeET);
        youtubeChannel = (EditText) findViewById(R.id.youTubeET);
        profilePic = (ImageView) findViewById(R.id.dvProfilePic);


        birthday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    DatePickerDialog dialog = new DatePickerDialog(CreateNewContact.this, datepicker, myCalendar
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

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(ContactListActivity.EDIT_CONTACT_KEY)){
            oldContact = (Contact) getIntent().getExtras().getSerializable(ContactListActivity.EDIT_CONTACT_KEY);

            firstName.setText(oldContact.firstName);
            lastName.setText(oldContact.lastName);
            company.setText(oldContact.company);
            email.setText(oldContact.email);
            phone.setText(oldContact.phone);
            url.setText(oldContact.url);
            address.setText(oldContact.address);
            birthday.setText(oldContact.birthday);
            nickname.setText(oldContact.nickname);
            fbURL.setText(oldContact.fbURL);
            twitterURL.setText(oldContact.twitterURL);
            skype.setText(oldContact.skype);
            youtubeChannel.setText(oldContact.youtubeChannel);

            if (!oldContact.profileImagePath.equals("")){
                Bitmap bmImg = BitmapFactory.decodeFile(oldContact.profileImagePath);
                profilePic.setImageBitmap(bmImg);
            } else {
                profilePic.setImageResource(R.drawable.default_image);
            }
        }
    }

    DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "MM/dd/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            birthday.setText(sdf.format(myCalendar.getTime()));
        }

    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){

                try {

                    Bitmap bmImg = BitmapFactory.decodeFile(imageAbsolutePath);
                    profilePic.setImageBitmap(bmImg);

                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                    Log.e("Camera", e.toString());
                }

            }
        }
    }

    public void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if(photoFile != null) {
                Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),
                        "io.vamshedhar.contacts.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void onCameraClick(View view){

        if (ContextCompat.checkSelfPermission(CreateNewContact.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(CreateNewContact.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CreateNewContact.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5002);
        } else {
            takePhoto();
        }


    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isValidPhoneNumber(String number){
        return number.length() >= 10 && number.length() <= 13 && PhoneNumberUtils.isGlobalPhoneNumber(number);
    }

    public boolean validate(){
        if(firstName.getText().toString().equals("")){
            Toast.makeText(CreateNewContact.this, "Please enter valid First Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if(lastName.getText().toString().equals("")) {
            Toast.makeText(CreateNewContact.this, "Please enter valid Last Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!isValidPhoneNumber(phone.getText().toString())){
            Toast.makeText(CreateNewContact.this, "Please enter valid Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (email.getText().toString().length() > 0 && !isValidEmail(email.getText().toString())){
            Toast.makeText(CreateNewContact.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void onSaveClick(View view){

        if(!validate()){
            return;
        }

        Contact newContact = new Contact(firstName.getText().toString(),
                                        lastName.getText().toString(),
                                        phone.getText().toString());

        newContact.profileImagePath = imageAbsolutePath;
        newContact.company = company.getText().toString();
        newContact.email = email.getText().toString();
        newContact.url = url.getText().toString();
        newContact.address = address.getText().toString();
        newContact.birthday = birthday.getText().toString();
        newContact.nickname = nickname.getText().toString();
        newContact.fbURL = fbURL.getText().toString();
        newContact.twitterURL = twitterURL.getText().toString();
        newContact.skype = skype.getText().toString();
        newContact.youtubeChannel = youtubeChannel.getText().toString();


        Intent intent = new Intent();

        if(oldContact != null){
            intent.putExtra(ContactListActivity.UPDATE_CONTACT_KEY, newContact);
            intent.putExtra(ContactListActivity.EDIT_CONTACT_KEY, oldContact);
        } else{
            intent.putExtra(MainActivity.CREATE_CONTACT_KEY, newContact);
        }

        setResult(RESULT_OK, intent);
        finish();
    };
}
