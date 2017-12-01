package io.vamshedhar.mysocial.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.vamshedhar.mysocial.R;
import io.vamshedhar.mysocial.activities.FriendsActivity;
import io.vamshedhar.mysocial.objects.User;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/19/17 3:19 PM.
 * vchinta1@uncc.edu
 */

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    ArrayList<User> users;
    Context context;
    FriendsListInterface IData;

    public FriendsListAdapter(ArrayList<User> users, Context context, FriendsListInterface IData) {
        this.users = users;
        this.context = context;
        this.IData = IData;
    }

    public interface FriendsListInterface {
        void addFriend(String id);
        void acceptFriendRequest(String id);
        void rejectFriendRequest(String id);
        void removeFriend(String id);
        void cancelFriendRequest(String id);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.freind_item, parent, false);
        FriendsListAdapter.ViewHolder viewHolder = new FriendsListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User friend = users.get(position);

        holder.friendName.setText(friend.getFullName());
        holder.negativeBtn.setVisibility(View.INVISIBLE);

        holder.negativeBtn.setTag(friend.getId());
        holder.positiveBtn.setTag(friend.getId());

        final String type = friend.getType();

        if (type.equals(FriendsActivity.FRIEND)){
            holder.positiveBtn.setImageResource(R.drawable.remove_friend);
        } else if (type.equals(FriendsActivity.FRIEND_REQUESTED)){
            holder.negativeBtn.setVisibility(View.VISIBLE);
            holder.positiveBtn.setImageResource(R.drawable.ic_accept);
        } else if (type.equals(FriendsActivity.FRIEND_REQUESTED_SENT)){
            holder.positiveBtn.setImageResource(R.drawable.ic_reject);
        } else {
            holder.positiveBtn.setImageResource(R.drawable.ic_add_friend);
        }

        holder.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView reject = (ImageView) view;
                IData.rejectFriendRequest(reject.getTag().toString());
            }
        });

        holder.positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView positive = (ImageView) view;
                if (type.equals(FriendsActivity.FRIEND)){
                    IData.removeFriend(positive.getTag().toString());
                } else if (type.equals(FriendsActivity.FRIEND_REQUESTED)){
                    IData.acceptFriendRequest(positive.getTag().toString());
                } else if (type.equals(FriendsActivity.FRIEND_REQUESTED_SENT)){
                    IData.cancelFriendRequest(positive.getTag().toString());
                } else{
                    IData.addFriend(positive.getTag().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        ImageView positiveBtn, negativeBtn;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            friendName  = itemView.findViewById(R.id.freindName);
            positiveBtn = itemView.findViewById(R.id.buttonPositive);
            negativeBtn = itemView.findViewById(R.id.buttonNegative);
        }
    }
}
