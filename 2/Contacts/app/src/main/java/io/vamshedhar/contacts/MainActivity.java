package io.vamshedhar.contacts;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Contacts";

    public static final int CREATE_CONTACT_CODE = 1001;
    public static final int DISPLAY_CONTACT_CODE = 1002;
    public static final int DELETE_CONTACT_CODE = 1003;
    public static final int EDIT_CONTACT_CODE = 1004;


    public static final String CREATE_CONTACT_KEY = "CREATED_CONTACT";
    public static final String UPDATED_CONTACT_LIST_KEY = "UPDATED_CONTACT_LIST";
    public static final String CONTACT_LIST_KEY = "CONTACT_LIST";
    public static final String CONTACT_LIST_ACTION_KEY = "CONTACT_LIST_ACTION";


    Button createNew, editContact, deleteContact, displayContact, finishBtn;


    ArrayList<Contact> contactsList = new ArrayList<>();

    public void close(View view){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREATE_CONTACT_CODE){
            if(resultCode == RESULT_OK){

                Contact newContact = (Contact) data.getExtras().getSerializable(CREATE_CONTACT_KEY);
                contactsList.add(newContact);

                Log.d(TAG, contactsList.toString());
            }
        }

        if (requestCode == EDIT_CONTACT_CODE || requestCode == DELETE_CONTACT_CODE){
            if(resultCode == RESULT_OK){

                contactsList = (ArrayList<Contact>) data.getSerializableExtra(UPDATED_CONTACT_LIST_KEY);

                Log.d(TAG, contactsList.toString());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNew = (Button) findViewById(R.id.createNewBtn);
        editContact = (Button) findViewById(R.id.editContactBtn);
        deleteContact = (Button) findViewById(R.id.deleteContactBtn);
        displayContact = (Button) findViewById(R.id.displayContactBtn);
        finishBtn = (Button) findViewById(R.id.finishBtn);

        Contact myContact = new Contact("Vamshedhar", "Reddy", "+19809389877", "");

        myContact.url = "https://github.com/vamshedhar/";
        myContact.fbURL = "https://www.facebook.com/vamshedhar";
        myContact.twitterURL = "https://twitter.com/vamshedhar";
        myContact.skype = "https://www.skype.com/vamshedhar";
        myContact.youtubeChannel = "https://www.youtube.com/channel/UChDoCcmSxqdSzJSFQXXUExg?view_as=subscriber";

        contactsList.add(myContact);
        contactsList.add(new Contact("Aditya", "Varma", "+19809389870", ""));
        contactsList.add(new Contact("Ajitesh", "J", "+19809389871", ""));
        contactsList.add(new Contact("Sandeep", "Krishna", "+19809389883", ""));
    }

    public void onCreateContactClick(View view){
        Intent intent = new Intent(MainActivity.this, CreateNewContact.class);
        startActivityForResult(intent, CREATE_CONTACT_CODE);
    }

    public void showContactList(String action, int CODE){
        Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
        intent.putExtra(CONTACT_LIST_KEY, contactsList);
        intent.putExtra(CONTACT_LIST_ACTION_KEY, action);
        startActivityForResult(intent, CODE);
    }

    public void onDisplayContactClick(View view){
        this.showContactList("DISPLAY", DISPLAY_CONTACT_CODE);
    }

    public void onEditContactClick(View view){
        this.showContactList("EDIT", EDIT_CONTACT_CODE);
    }

    public void onDeleteContactClick(View view){
        this.showContactList("DELETE", DELETE_CONTACT_CODE);
    }

}
