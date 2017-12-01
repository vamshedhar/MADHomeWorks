package io.vamshedhar.coursemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class InstructorsListFragment extends MenuActionBar {

    private OnFragmentInteractionListener mListener;

    FloatingActionButton addInstructorBtn;

    GridView instructorListGrid;
    InstructorListAdapter adaptor;

    String username;

    public InstructorsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.instructors).setVisible(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_instructors_list, container, false);
    }

    public void loadData(){
        Realm realm = Realm.getDefaultInstance();

        SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.TAG, getActivity().MODE_PRIVATE);;
        username = pref.getString(MainActivity.SP_USERNAME, null);

        User currentUser = realm.where(User.class)
                .equalTo("username", username)
                .findFirst();

        RealmResults<Instructor> instructors = realm.where(Instructor.class)
                .equalTo("user.username", currentUser.getUsername())
                .findAll();

        ArrayList<Instructor> instructorsList = new ArrayList(instructors);

        realm.close();

        adaptor = new InstructorListAdapter(getActivity(), instructorsList);

        instructorListGrid.setAdapter(adaptor);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Instructors");

        addInstructorBtn = getView().findViewById(R.id.addInstructorBtn);

        instructorListGrid =  getView().findViewById(R.id.instructorList);

        loadData();

        instructorListGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Instructor instructor = (Instructor) instructorListGrid.getItemAtPosition(position);

                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new InstructorDetailsFragment(), "instructor_details_fragment")
                        .addToBackStack(null)
                        .commit();
                getFragmentManager().executePendingTransactions();

                InstructorDetailsFragment fragment = (InstructorDetailsFragment) getFragmentManager().findFragmentByTag("instructor_details_fragment");
                fragment.loadData(instructor);
            }
        });

        instructorListGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Instructor instructor = (Instructor) instructorListGrid.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Are you sure you want to delete the instructor and his courses?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteInstructor(instructor.getId());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                final AlertDialog deleteAlert = builder.create();

                deleteAlert.show();
                return true;
            }
        });

        addInstructorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new AddInstructorFragment(), "add_courses_fragment")
                .addToBackStack(null)
                .commit();
            getFragmentManager().executePendingTransactions();
            }
        });
    }

    public void deleteInstructor(final int id){
        final Realm realm = Realm.getDefaultInstance();


        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {

                RealmResults<Course> courses = bgRealm.where(Course.class)
                        .equalTo("instructor.id", id)
                        .findAll();

                courses.deleteAllFromRealm();

                RealmResults<Instructor> results = bgRealm.where(Instructor.class)
                        .equalTo("id", id)
                        .findAll();

                results.deleteFirstFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
                loadData();
                Toast.makeText(getActivity(), "Instructor deleted successfully!", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                realm.close();
                loadData();
                Toast.makeText(getActivity(), "Error in Instructor deletion!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
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
//            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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
