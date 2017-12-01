package io.vamshedhar.contacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayContactActivity extends AppCompatActivity {

    TextView fullname, company, email, phone,
            url, address, birthday, nickname, fbURL,
            twitterURL, skype, youtubeChannel;
    ImageView profilePic;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5001){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                String number = phone.getText().toString().trim();

                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                if (callIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(callIntent);
                }
            } else {
                Toast.makeText(DisplayContactActivity.this, "Please Grant call permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onCallClick(View view){
        TextView phoneLabel = (TextView) view;

        String number = phoneLabel.getText().toString().trim();

        if (!number.equals("")){
            if (ContextCompat.checkSelfPermission(DisplayContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(DisplayContactActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 5001);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                if (callIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(callIntent);
                }
            }


        }


    }

    public void onLinkClick(View view){
        TextView label = (TextView) view;

        String content = label.getText().toString();

        String link = content.substring(content.indexOf(":") + 1).trim();

        if(!link.equals("")){
            Log.d(MainActivity.TAG, link);

            if (!link.startsWith("http://") && !link.startsWith("https://")){
                link = "http://" + link;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);

        fullname = (TextView) findViewById(R.id.dvName);
        company = (TextView) findViewById(R.id.dvcompany);
        email = (TextView) findViewById(R.id.dvemail);
        phone = (TextView) findViewById(R.id.dvPhone);
        url = (TextView) findViewById(R.id.dvurl);
        address = (TextView) findViewById(R.id.dvaddress);
        birthday = (TextView) findViewById(R.id.dvbirthday);
        nickname = (TextView) findViewById(R.id.dvnickname);
        fbURL = (TextView) findViewById(R.id.dvfbURL);
        twitterURL = (TextView) findViewById(R.id.dvtwitterUrl);
        skype = (TextView) findViewById(R.id.dvskype);
        youtubeChannel = (TextView) findViewById(R.id.dvyoutube);
        profilePic = (ImageView) findViewById(R.id.dvProfilePic);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(ContactListActivity.CONTACT_DETAIL_KEY)){

            Contact contact = (Contact) getIntent().getExtras().getSerializable(ContactListActivity.CONTACT_DETAIL_KEY);

            fullname.setText(contact.getFullName());
            company.setText(company.getText() + ": " +  (contact.company != null ? contact.company : ""));
            email.setText(email.getText() + ": " + (contact.email != null ? contact.email : ""));
            phone.setText(contact.phone != null ? contact.phone : "");
            url.setText(url.getText() + ": " + (contact.url != null ? contact.url : ""));
            address.setText(address.getText() + ": " + (contact.address != null ? contact.address : ""));
            birthday.setText(birthday.getText() + ": " + (contact.birthday != null ? contact.birthday : ""));
            nickname.setText(nickname.getText() + ": " + (contact.nickname != null ? contact.nickname : ""));
            fbURL.setText(fbURL.getText() + ": " + (contact.fbURL != null ? contact.fbURL : ""));
            twitterURL.setText(twitterURL.getText() + ": " + (contact.twitterURL != null ? contact.twitterURL : ""));
            skype.setText(skype.getText() + ": " + (contact.skype != null ? contact.skype : ""));
            youtubeChannel.setText(youtubeChannel.getText() + ": " + (contact.youtubeChannel != null ? contact.youtubeChannel : ""));

            if (!contact.profileImagePath.equals("")){
                Bitmap bmImg = BitmapFactory.decodeFile(contact.profileImagePath);
                profilePic.setImageBitmap(bmImg);
            }

        }
    }
}
