package io.vamshedhar.coursemanager;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class LoginFragment extends MenuActionBar {

    private OnFragmentInteractionListener mListener;

    EditText usernameET, passwordET;
    CheckBox showPassword;
    Button loginBtn;
    TextView signUp;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.home).setVisible(false);
        menu.findItem(R.id.instructors).setVisible(false);
        menu.findItem(R.id.addInstructor).setVisible(false);
        menu.findItem(R.id.logout).setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public boolean isValidData(){
        if (usernameET.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity(), "Please enter valid username!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordET.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity(), "Please enter valid password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Course Manager");

        usernameET = getView().findViewById(R.id.registerUsernameET);
        passwordET = getView().findViewById(R.id.websiteET);
        showPassword = getView().findViewById(R.id.showPasswordCB);
        loginBtn = getView().findViewById(R.id.loginBtn);
        signUp = getView().findViewById(R.id.signUpTV);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (!showPassword.isChecked()){
                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else{
                    passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                passwordET.setSelection(passwordET.getText().length());
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (isValidData()){
                String username = usernameET.getText().toString().trim();
                Realm realm = Realm.getDefaultInstance();
                RealmResults<User> users = realm.where(User.class)
                        .findAll();

                User user = realm.where(User.class)
                        .equalTo("username", username)
                        .findFirst();

                Log.d(MainActivity.TAG, users.toString());

                if (user != null && user.getPassword().equals(passwordET.getText().toString().trim())){

                    SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.TAG, getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(MainActivity.SP_USERNAME, username);
                    editor.commit();

                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new CourseListFragment(), "courses_fragment")
                            .commit();
                    getFragmentManager().executePendingTransactions();
                } else {
                    passwordET.setText("");
                    Toast.makeText(getActivity(), "In-Valid login credentials!", Toast.LENGTH_LONG).show();
                }

                realm.close();
            }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new RegisterFragment(), "register_fragment")
                        .addToBackStack(null)
                        .commit();
                getFragmentManager().executePendingTransactions();
            }
        });
    }

    public void onLoginClicked() {
        if (mListener != null) {
            mListener.onLoginClicked();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLoginClicked();
    }
}
