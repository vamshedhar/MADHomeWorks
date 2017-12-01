package io.vamshedhar.coursemanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class CourseDetailsFragment extends MenuActionBar {

    private OnFragmentInteractionListener mListener;

    TextView courseTitle, courseTime, courseSemester, creditHours, instructorName, instructorEmail, instructorWebsite;
    ImageView instructorImage;

    RelativeLayout instructorBlock;

    Course course;
    Instructor instructor;

    public CourseDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_course_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Course Details");

        courseTitle = getView().findViewById(R.id.courseTitle);
        courseTime = getView().findViewById(R.id.courseTime);
        courseSemester = getView().findViewById(R.id.courseSemester);
        creditHours = getView().findViewById(R.id.creditHours);
        instructorName = getView().findViewById(R.id.instructorName);
        instructorEmail = getView().findViewById(R.id.instructorEmail);
        instructorWebsite = getView().findViewById(R.id.instructorWebsite);
        instructorImage = getView().findViewById(R.id.instructorProfilePic);
        instructorBlock = getView().findViewById(R.id.instructorBlock);

        instructorWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView urlView = (TextView) view;

                String url = urlView.getText().toString().trim();

                if(!url.equals("")){
                    Log.d(MainActivity.TAG, url);

                    if (!url.startsWith("http://") && !url.startsWith("https://")){
                        url = "http://" + url;
                    }

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getActivity().startActivity(browserIntent);
                }
            }
        });

        instructorBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new InstructorDetailsFragment(), "instructor_details_fragment")
                        .addToBackStack(null)
                        .commit();
                getFragmentManager().executePendingTransactions();

                InstructorDetailsFragment fragment = (InstructorDetailsFragment) getFragmentManager().findFragmentByTag("instructor_details_fragment");
                fragment.loadData(instructor);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (course != null){
            loadData(course);
        }
    }

    public void loadData(Course course){
        this.course = course;
        this.instructor = course.getInstructor();
        courseTitle.setText(course.getName());
        courseTime.setText(course.getDay() + " " + course.getTime());
        courseSemester.setText(course.getSemester() + " Semester");
        creditHours.setText(course.getCredits() + " Credit hours");

        instructorName.setText(instructor.getFullName());
        instructorEmail.setText(instructor.getEmail());
        instructorWebsite.setText(instructor.getWebsite());

        if (!instructor.getProfilePic().equals("")){
            Bitmap bmImg = BitmapFactory.decodeFile(instructor.getProfilePic());
            instructorImage.setImageBitmap(bmImg);
        }
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
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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
