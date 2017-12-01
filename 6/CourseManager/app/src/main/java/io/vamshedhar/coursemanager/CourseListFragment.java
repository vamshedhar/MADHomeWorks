package io.vamshedhar.coursemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class CourseListFragment extends MenuActionBar implements CourseListAdapter.CourseListInterface {

    private OnFragmentInteractionListener mListener;

    FloatingActionButton addCourseBtn;

    String username;

    ArrayList<Course> coursesList;

    private RecyclerView courseList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public CourseListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.home).setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_course_list, container, false);
    }

    public void loadData(){
        Realm realm = Realm.getDefaultInstance();

        SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.TAG, getActivity().MODE_PRIVATE);;
        username = pref.getString(MainActivity.SP_USERNAME, null);

        User currentUser = realm.where(User.class)
                .equalTo("username", username)
                .findFirst();

        RealmResults<Course> courses = realm.where(Course.class)
                .equalTo("user.username", currentUser.getUsername())
                .findAll();

        coursesList = new ArrayList(courses);

        realm.close();

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        courseList.setLayoutManager(mLayoutManager);

        adapter = new CourseListAdapter(coursesList, getActivity(), this);
        courseList.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Course Manager");

        addCourseBtn = getView().findViewById(R.id.addCourseBtn);
        courseList = getView().findViewById(R.id.instructorList);

        loadData();


        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new AddCourseFragment(), "add_courses_fragment")
                        .addToBackStack(null)
                        .commit();
                getFragmentManager().executePendingTransactions();
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
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
//            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void deleteCourse(final int id){
        final Realm realm = Realm.getDefaultInstance();


        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {

                final RealmResults<Course> results = bgRealm.where(Course.class)
                        .equalTo("id", id)
                        .findAll();

                results.deleteFirstFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
                loadData();
                Toast.makeText(getActivity(), "Course deleted successfully!", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                realm.close();
                loadData();
                Toast.makeText(getActivity(), "Error in Course deletion!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onCourseSelected(final int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Are you sure you want to delete the course?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteCourse(id);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        final AlertDialog deleteAlert = builder.create();

        deleteAlert.show();

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
