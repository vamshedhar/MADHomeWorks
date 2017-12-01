package io.vamshedhar.coursemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/7/17 5:41 PM.
 * vchinta1@uncc.edu
 */

public class CourseListAdapter  extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    ArrayList<Course> courses;
    Context context;

    CourseListInterface IData;

    public CourseListAdapter(ArrayList<Course> courses, Context context, CourseListInterface IData) {
        this.courses = courses;
        this.context = context;
        this.IData = IData;
    }

    public interface CourseListInterface {
        void onCourseSelected(int id);
        void onCourseClicked(Course course);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view, IData);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Course course = courses.get(position);

        holder.course = course;

        holder.courseName.setText(course.getName());
        holder.courseInstructor.setText(course.getInstructor().getFullName());
        holder.courseTime.setText(course.getDay().substring(0, 3) + " " + course.getTime());

        class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... urls) {
                Bitmap bmImg = BitmapFactory.decodeFile(urls[0]);
                Bitmap scaled = Bitmap.createScaledBitmap(bmImg, 200, 200, true);
                return scaled;
            }

            protected void onPostExecute(Bitmap result) {
                holder.courseImage.setImageBitmap(result);
            }

        }

        holder.courseImage.setImageResource(R.drawable.profile_image);

        if (!course.getInstructor().getProfilePic().equals("")){
            new DownloadImageTask().execute(course.getInstructor().getProfilePic());
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, courseInstructor, courseTime;
        ImageView courseImage;
        Course course;
        final CourseListInterface IData;

        public ViewHolder(View itemView, CourseListInterface interfaceIData) {
            super(itemView);
            courseName  = itemView.findViewById(R.id.courseTitle);
            courseInstructor = itemView.findViewById(R.id.courseInstructor);
            courseTime = itemView.findViewById(R.id.courseTime);

            courseImage = itemView.findViewById(R.id.coruserImage);

            course = null;
            IData = interfaceIData;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IData.onCourseClicked(course);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    IData.onCourseSelected(course.getId());
                    return true;
                }
            });
        }
    }
}
