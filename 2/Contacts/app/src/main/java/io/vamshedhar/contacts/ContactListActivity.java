package io.vamshedhar.contacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    public static final int EDIT_CONTACT_CODE = 2001;
    public static final String EDIT_CONTACT_KEY = "EDIT_CONTACT";
    public static final String UPDATE_CONTACT_KEY = "UPDATED_CONTACT";
    public static final String CONTACT_DETAIL_KEY = "CONTACT_DETAILS";

    ScrollView sv;
    String LIST_ACTION = "DISPLAY";

    ArrayList<Contact> contactsList;

    public void displayContact(Contact contact){
        Intent intent = new Intent(ContactListActivity.this, DisplayContactActivity.class);
        intent.putExtra(CONTACT_DETAIL_KEY, contact);
        startActivity(intent);
    }

    public void editContact(Contact contact){
        Intent intent = new Intent(ContactListActivity.this, CreateNewContact.class);
        intent.putExtra(EDIT_CONTACT_KEY, contact);
        startActivityForResult(intent, EDIT_CONTACT_CODE);
    }

    public void loadContactList(){
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        for (final Contact contact : contactsList) {
            ContactItemUI contactItem = new ContactItemUI(this);

            View contactItemView = contactItem;

            contactItem.fullName.setText(contact.getFullName());
            contactItem.phoneNumber.setText(contact.phone);

            contactItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("log ", contact.toString());

                    switch (LIST_ACTION){
                        case "DELETE":
                            deleteContact(view, contact);
                            break;
                        case "EDIT":
                            editContact(contact);
                            break;
                        case "DISPLAY":
                            displayContact(contact);
                            break;
                        default:
                            break;
                    }
                }
            });

            if (!contact.profileImagePath.equals("")){
                Bitmap bmImg = BitmapFactory.decodeFile(contact.profileImagePath);
                contactItem.profilePic.setImageBitmap(bmImg);
            }

            container.addView(contactItemView);
        }

        sv.removeAllViews();
        sv.addView(container);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_CONTACT_CODE){
            if (resultCode == RESULT_OK){
                Contact newContact = (Contact) data.getExtras().getSerializable(UPDATE_CONTACT_KEY);
                Contact oldContact = (Contact) data.getExtras().getSerializable(EDIT_CONTACT_KEY);
                int index = contactsList.indexOf(oldContact);
                contactsList.set(index, newContact);
                loadContactList();
                Log.d(MainActivity.TAG, contactsList.toString());
            }
        }
    }

    public void deleteContact(final View view, final Contact contact){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete Contact: " + contact.getFullName())
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LinearLayout layout = (LinearLayout) sv.getChildAt(0);
                        layout.removeView(view);
                        contactsList.remove(contact);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(MainActivity.TAG, "onClick: No");
                    }
                });

        AlertDialog alert = builder.create();

        alert.show();

    }

    @Override
    public void onBackPressed() {
        switch (LIST_ACTION){
            case "DELETE":
            case "EDIT":
                Log.d(MainActivity.TAG, LIST_ACTION + " Back Clicked");
                Intent intent = new Intent();
                intent.putExtra(MainActivity.UPDATED_CONTACT_LIST_KEY, contactsList);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                super.onBackPressed();
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        sv = (ScrollView) findViewById(R.id.contactListScroll);



        if(getIntent().getExtras() != null){

            if(getIntent().getExtras().containsKey(MainActivity.CONTACT_LIST_ACTION_KEY)){
                LIST_ACTION = getIntent().getExtras().getString(MainActivity.CONTACT_LIST_ACTION_KEY);
                Log.d(MainActivity.TAG, "onCreate: " + LIST_ACTION);
            }

            if(getIntent().getExtras().containsKey(MainActivity.CONTACT_LIST_KEY)){
                contactsList = (ArrayList<Contact>) getIntent().getSerializableExtra(MainActivity.CONTACT_LIST_KEY);
                loadContactList();
            }

        }


    }
}
