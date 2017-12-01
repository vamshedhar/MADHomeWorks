package io.vamshedhar.mysocial.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.vamshedhar.mysocial.R;
import io.vamshedhar.mysocial.adapters.FriendsListAdapter;
import io.vamshedhar.mysocial.objects.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    ArrayList<User> usersList;

    private RecyclerView friendsList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public FriendsListFragment() {
        // Required empty public constructor
    }

    public void loadUsers(ArrayList<User> users, boolean reloadList){
        usersList = users;

        if (reloadList){
            loadList();
        }
    }

    public void loadList(){
        if (friendsList != null){
            adapter = new FriendsListAdapter(usersList, getActivity(), (FriendsListAdapter.FriendsListInterface) getActivity());
            friendsList.setAdapter(adapter);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (usersList == null){
            usersList = new ArrayList<>();
        }

        friendsList = getView().findViewById(R.id.friendsList);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        friendsList.setLayoutManager(mLayoutManager);

        loadList();
    }
}
