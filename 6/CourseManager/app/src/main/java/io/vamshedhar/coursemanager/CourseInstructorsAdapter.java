package io.vamshedhar.coursemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/6/17 4:18 PM.
 * vchinta1@uncc.edu
 */

public class CourseInstructorsAdapter extends RecyclerView.Adapter<CourseInstructorsAdapter.ViewHolder> {
    ArrayList<Instructor> instructors;
    Context context;

    CourseInstructorsInterface IData;

    private static ImageView selectedImage;
    private static int selectedId;

    public interface CourseInstructorsInterface {
        void onInstructorSelected(int id);
    }

    public CourseInstructorsAdapter(ArrayList<Instructor> instructors, Context context, int selectedId, CourseInstructorsInterface IData) {
        this.instructors = instructors;
        this.context = context;
        this.IData = IData;
        this.selectedId = selectedId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_instructor_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Instructor instructor = instructors.get(position);

        holder.instructorName.setText(instructor.getFullName());
        holder.checkedImage.setVisibility(View.INVISIBLE);

        if (selectedId == instructor.getId()) {
            holder.checkedImage.setVisibility(View.VISIBLE);
            selectedImage = holder.checkedImage;
        }

        class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... urls) {
                Bitmap bmImg = BitmapFactory.decodeFile(urls[0]);
                Bitmap scaled = Bitmap.createScaledBitmap(bmImg, 200, 200, true);
                return scaled;
            }

            protected void onPostExecute(Bitmap result) {
                holder.instructorImage.setImageBitmap(result);
            }

        }

        holder.instructorImage.setImageResource(R.drawable.profile_image);

        if (!instructor.getProfilePic().equals("")){
            new DownloadImageTask().execute(instructor.getProfilePic());
        }

        holder.instructorImage.setTag(position);

        holder.instructorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView profilePic = (ImageView) view;

                int index = (int) profilePic.getTag();

                selectedImage.setVisibility(View.INVISIBLE);
                selectedImage = holder.checkedImage;
                Instructor selectedInstructor = instructors.get(index);
                selectedId = selectedInstructor.getId();
                holder.checkedImage.setVisibility(View.VISIBLE);

                IData.onInstructorSelected(selectedId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return instructors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView instructorName;
        ImageView instructorImage, checkedImage;

        public ViewHolder(View itemView) {
            super(itemView);
            instructorName  = itemView.findViewById(R.id.instructorFullName);
            instructorImage = itemView.findViewById(R.id.coruserImage);
            checkedImage = itemView.findViewById(R.id.selectInstructor);
        }
    }

}
