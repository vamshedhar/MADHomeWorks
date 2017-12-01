package io.vamshedhar.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/19/17 11:47 AM.
 * vchinta1@uncc.edu
 */

public class ContactItemUI extends LinearLayout {

    public TextView fullName, phoneNumber;
    public ImageView profilePic;

    public ContactItemUI(Context context) {
        super(context);
        inflateXML(context);
    }

    private void inflateXML(Context context) {
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ContactItemView = inflater.inflate(R.layout.contact_item, this);
        this.fullName = (TextView) findViewById(R.id.contactFullName);
        this.phoneNumber = (TextView) findViewById(R.id.contactNumber);
        this.profilePic = (ImageView) findViewById(R.id.contactProfilePic);
    }
}
