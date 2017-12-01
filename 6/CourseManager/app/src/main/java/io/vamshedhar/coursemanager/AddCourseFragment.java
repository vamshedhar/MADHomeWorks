package io.vamshedhar.coursemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;


public class AddCourseFragment extends MenuActionBar implements CourseInstructorsAdapter.CourseInstructorsInterface {

    private OnFragmentInteractionListener mListener;

    EditText courseTitle, timeHours, timeMinutes;
    Spinner daySpinner, timeDaySpinner, semesterSpinner;
    RadioGroup creditsRG;
    TextView noInstructorsMsg;

    Button resetBtn, createBtn, createInstructorBtn;

    String username;

    int selectedInstructor;

    private RecyclerView instructorList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public AddCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_add_course, container, false);
    }

    public boolean isValidData(){
        if (courseTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity(), "Please enter valid title!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (timeHours.getText().toString().trim().isEmpty() || timeMinutes.getText().toString().trim().isEmpty() || timeDaySpinner.getSelectedItem().toString().isEmpty()){
            Toast.makeText(getActivity(), "Please enter valid time!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (daySpinner.getSelectedItem().toString().isEmpty()){
            Toast.makeText(getActivity(), "Please select schedule weekday!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (semesterSpinner.getSelectedItem().toString().isEmpty()){
            Toast.makeText(getActivity(), "Please select schedule semester!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (creditsRG.getCheckedRadioButtonId() == -1){
            Toast.makeText(getActivity(), "Please select course credits!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void reset(){
        courseTitle.setText("");
        timeHours.setText("");
        timeMinutes.setText("");
        daySpinner.setSelection(0);
        timeDaySpinner.setSelection(0);
        semesterSpinner.setSelection(0);
        creditsRG.check(R.id.credits1);

        selectedInstructor = -1;

        loadInstructors();
    }

    public void loadInstructors(){
        Realm realm = Realm.getDefaultInstance();

        SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.TAG, MainActivity.MODE_PRIVATE);;
        username = pref.getString(MainActivity.SP_USERNAME, null);

        User currentUser = realm.where(User.class)
                .equalTo("username", username)
                .findFirst();

        RealmResults<Instructor> instructorsResults = realm.where(Instructor.class)
                .equalTo("user.username", currentUser.getUsername())
                .findAll();

        ArrayList<Instructor> instructors = new ArrayList(instructorsResults);

        realm.close();

        if (instructors.size() == 0){
            instructorList.setVisibility(View.INVISIBLE);
            createInstructorBtn.setVisibility(View.VISIBLE);
            noInstructorsMsg.setVisibility(View.VISIBLE);
            createBtn.setEnabled(false);
            resetBtn.setEnabled(false);
        } else {
            Log.d(MainActivity.TAG, Integer.toString(selectedInstructor));
            if (selectedInstructor == -1){
                selectedInstructor = instructors.get(0).getId();
            }

            adapter = new CourseInstructorsAdapter(instructors, getActivity(), selectedInstructor, this);
            instructorList.setAdapter(adapter);
        }


    }

    public void selectInstructor(int instructorId){
        selectedInstructor = instructorId;

        loadInstructors();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Add Course");

        courseTitle = getView().findViewById(R.id.courseTitleET);
        timeHours = getView().findViewById(R.id.timeHours);
        timeMinutes = getView().findViewById(R.id.timeMinutes);

        noInstructorsMsg = getView().findViewById(R.id.noInstructorsMessage);

        daySpinner = getView().findViewById(R.id.daySpinner);
        timeDaySpinner = getView().findViewById(R.id.timeDay);
        semesterSpinner = getView().findViewById(R.id.semesterSpinner);
        creditsRG = getView().findViewById(R.id.creditsGroup);
        
        resetBtn = getView().findViewById(R.id.resetBtn);
        createBtn = getView().findViewById(R.id.createBtn);
        createInstructorBtn = getView().findViewById(R.id.createInstructorBtn);
        
        instructorList = getView().findViewById(R.id.instructorList);

        timeHours.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "12")});
        timeMinutes.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "59")});

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        instructorList.setLayoutManager(mLayoutManager);

        reset();

        createInstructorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new AddInstructorFragment(), "add_instructor_fragment")
                .addToBackStack(null)
                .commit();
            getFragmentManager().executePendingTransactions();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Do you want to reset all fields?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    reset();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

        final AlertDialog resetAlert = builder.create();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAlert.show();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (isValidData()){
                final Realm realm = Realm.getDefaultInstance();

                final String title = courseTitle.getText().toString().trim();

                SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.TAG, getActivity().MODE_PRIVATE);
                final String username = pref.getString(MainActivity.SP_USERNAME, null);

                User currentUser = realm.where(User.class)
                        .equalTo("username", username)
                        .findFirst();

                RealmResults<Course> courses = realm.where(Course.class)
                        .equalTo("name", title, Case.INSENSITIVE)
                        .equalTo("user.username", currentUser.getUsername())
                        .findAll();

                if (courses.size() > 0){
                    Toast.makeText(getActivity(), "A course already exists with give title!", Toast.LENGTH_LONG).show();
                } else {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            Course course = new Course();

                            User user = bgRealm.where(User.class)
                                    .equalTo("username", username)
                                    .findFirst();

                            Instructor instructor = bgRealm.where(Instructor.class)
                                    .equalTo("id", selectedInstructor)
                                    .findFirst();

                            int nextID = 1;
                            if (bgRealm.where(Course.class).findAll().size() != 0){
                                nextID = bgRealm.where(Course.class).max("id").intValue() + 1;
                            }

                            course.setId(nextID);
                            course.setName(title);
                            course.setInstructor(instructor);
                            course.setDay(daySpinner.getSelectedItem().toString());
                            String time = timeHours.getText().toString() + ":" + timeMinutes.getText().toString() + " " + timeDaySpinner.getSelectedItem().toString();
                            course.setTime(time);
                            course.setUser(user);
                            RadioButton selectBtn = getView().findViewById(creditsRG.getCheckedRadioButtonId());
                            course.setCredits(Integer.parseInt(selectBtn.getText().toString()));
                            course.setSemester(semesterSpinner.getSelectedItem().toString());

                            bgRealm.copyToRealm(course);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            reset();
                            realm.close();
                            Toast.makeText(getActivity(), "Course added successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {

                            error.printStackTrace();
                            Toast.makeText(getActivity(), "Unknown Error occurred!", Toast.LENGTH_LONG).show();

                            realm.close();
                        }
                    });
                }
            }
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

    @Override
    public void onInstructorSelected(int id) {
        selectedInstructor = id;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
