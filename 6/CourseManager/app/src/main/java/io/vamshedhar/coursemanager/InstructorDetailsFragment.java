package io.vamshedhar.coursemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class InstructorDetailsFragment extends MenuActionBar implements CourseListAdapter.CourseListInterface {

    private OnFragmentInteractionListener mListener;

    ImageView instructorImage;
    TextView instructorName, instructorEmail, instructorWebsite, noCoursesMessage, addCourse;

    Instructor instructor;
    String username;

    ArrayList<Course> coursesList;

    private RecyclerView courseList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public InstructorDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_instructor_details, container, false);
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Instructor Details");

        instructorName = getView().findViewById(R.id.instructorName);
        instructorEmail = getView().findViewById(R.id.instructorEmail);
        instructorWebsite = getView().findViewById(R.id.instructorWebsite);
        instructorImage = getView().findViewById(R.id.instructorProfilePic);
        courseList = getView().findViewById(R.id.courseList);
        noCoursesMessage = getView().findViewById(R.id.noCoursesMessage);
        addCourse = getView().findViewById(R.id.addCourse);

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

        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new AddCourseFragment(), "add_course_fragment")
                        .addToBackStack(null)
                        .commit();
                getFragmentManager().executePendingTransactions();

                AddCourseFragment fragment = (AddCourseFragment) getFragmentManager().findFragmentByTag("add_course_fragment");
                fragment.selectInstructor(instructor.getId());
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        courseList.setLayoutManager(mLayoutManager);
    }

    public void loadCourses(){
        Realm realm = Realm.getDefaultInstance();

        SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.TAG, getActivity().MODE_PRIVATE);;
        username = pref.getString(MainActivity.SP_USERNAME, null);

        User currentUser = realm.where(User.class)
                .equalTo("username", username)
                .findFirst();

        RealmResults<Course> courses = realm.where(Course.class)
                .equalTo("user.username", currentUser.getUsername())
                .equalTo("instructor.id", instructor.getId())
                .findAll();

        coursesList = new ArrayList(courses);

        realm.close();
        
        if (coursesList.size() == 0){
            noCoursesMessage.setVisibility(View.VISIBLE);
            addCourse.setVisibility(View.VISIBLE);
            courseList.setVisibility(View.INVISIBLE);
        } else {
            noCoursesMessage.setVisibility(View.INVISIBLE);
            addCourse.setVisibility(View.INVISIBLE);
            courseList.setVisibility(View.VISIBLE);
            adapter = new CourseListAdapter(coursesList, getActivity(), this);
            courseList.setAdapter(adapter);
        }


    }

    public void loadData(Instructor instructor){
        this.instructor = instructor;

        instructorName.setText(instructor.getFullName());
        instructorEmail.setText(instructor.getEmail());
        instructorWebsite.setText(instructor.getWebsite());

        if (!instructor.getProfilePic().equals("")){
            Bitmap bmImg = BitmapFactory.decodeFile(instructor.getProfilePic());
            Bitmap scaled = Bitmap.createScaledBitmap(bmImg, 200, 200, true);
            instructorImage.setImageBitmap(scaled);
        }

        loadCourses();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (instructor != null){
            loadData(instructor);
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

    @Override
    public void onCourseSelected(int id) {

    }

    @Override
    public void onCourseClicked(Course course) {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new CourseDetailsFragment(), "course_details_fragment")
                .addToBackStack(null)
                .commit();
        getFragmentManager().executePendingTransactions();

        CourseDetailsFragment fragment = (CourseDetailsFragment) getFragmentManager().findFragmentByTag("course_details_fragment");
        fragment.loadData(course);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
