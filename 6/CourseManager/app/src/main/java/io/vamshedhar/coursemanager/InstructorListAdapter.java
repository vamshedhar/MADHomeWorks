package io.vamshedhar.coursemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/6/17 10:08 AM.
 * vchinta1@uncc.edu
 */

public class InstructorListAdapter extends ArrayAdapter<Instructor> {

    Context context;
    ArrayList<Instructor> instructors;

    public InstructorListAdapter(Context context, ArrayList<Instructor> objects) {
        super(context, R.layout.instructor_item, objects);
        this.context = context;
        this.instructors = objects;
    }

    @Override
    public int getCount() {
        return instructors.size();
    }

    @Override
    public Instructor getItem(int i) {
        return instructors.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.instructor_item, parent, false);
            ViewHolder vholder = new ViewHolder();
            vholder.instructorName = convertView.findViewById(R.id.instructorFullName);
            vholder.instructorEmail = convertView.findViewById(R.id.instructorEmail);
            vholder.profilePic = convertView.findViewById(R.id.instructorProfilePic);
            convertView.setTag(vholder);
        }

        holder = (ViewHolder) convertView.getTag();

        class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... urls) {
                Bitmap bmImg = BitmapFactory.decodeFile(urls[0]);
                Bitmap scaled = Bitmap.createScaledBitmap(bmImg, 200, 200, true);
                return scaled;
            }

            protected void onPostExecute(Bitmap result) {
                holder.profilePic.setImageBitmap(result);
            }

        }

        Instructor instructor = instructors.get(i);

        holder.instructorName.setText(instructor.getFullName());
        holder.instructorEmail.setText(instructor.getEmail());

        holder.profilePic.setImageResource(R.drawable.profile_image);

        if (!instructor.getProfilePic().equals("")){
            new DownloadImageTask().execute(instructor.getProfilePic());
        }

        return convertView;
    }

    static class ViewHolder{
        TextView instructorName, instructorEmail;
        ImageView profilePic;
    }
}
