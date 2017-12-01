package io.vamshedhar.coursemanager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/7/17 6:49 PM.
 * vchinta1@uncc.edu
 */

public class MenuActionBar extends Fragment {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_action_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new CourseListFragment(), "courses_fragment")
                        .commit();
                getFragmentManager().executePendingTransactions();
                break;
            case R.id.instructors:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new InstructorsListFragment(), "instructors_fragment")
                        .addToBackStack(null)
                        .commit();
                getFragmentManager().executePendingTransactions();
                break;
            case R.id.addInstructor:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new AddInstructorFragment(), "add_instructor_fragment")
                        .addToBackStack(null)
                        .commit();
                getFragmentManager().executePendingTransactions();
                break;
            case R.id.logout:
                SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.TAG, MainActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(MainActivity.SP_USERNAME, null);
                editor.commit();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new LoginFragment(), "login_fragment")
                        .commit();
                getFragmentManager().executePendingTransactions();
                break;
            case R.id.exit:
                quitApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void quitApp(){
        getActivity().finishAffinity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().invalidateOptionsMenu();
    }
}
