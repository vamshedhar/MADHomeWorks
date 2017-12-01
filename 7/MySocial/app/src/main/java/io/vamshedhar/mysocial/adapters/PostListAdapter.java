package io.vamshedhar.mysocial.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.vamshedhar.mysocial.R;
import io.vamshedhar.mysocial.objects.Post;
import io.vamshedhar.mysocial.objects.User;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/18/17 9:26 PM.
 * vchinta1@uncc.edu
 */

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    ArrayList<Post> posts;
    Context context;
    PostListInterface IData;
    HashMap<String, User> userMap;


    public PostListAdapter(ArrayList<Post> posts, Context context, PostListInterface IData, HashMap userMap) {
        this.posts = posts;
        this.context = context;
        this.IData = IData;
        this.userMap = userMap;
    }

    public interface PostListInterface {
        void showWall(User user);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Post post = posts.get(position);

        holder.userName.setText(userMap.get(post.getUserId()).getFullName());

        holder.userName.setTag(position);

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView userName = (TextView) view;

                int index = (int) userName.getTag();

                IData.showWall(posts.get(index).getUser());
            }
        });

        Date createdTime = new Date(post.getCreateTimestamp());
        PrettyTime p = new PrettyTime();
        holder.postTime.setText(p.format(createdTime));

        holder.postContent.setText(post.getContent());

        holder.itemSeparator.setVisibility(View.VISIBLE);

        if (position == posts.size() - 1){
            holder.itemSeparator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postTime, postContent;
        View itemSeparator;

        public ViewHolder(View itemView) {
            super(itemView);
            userName  = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            postContent = itemView.findViewById(R.id.postContent);
            itemSeparator = itemView.findViewById(R.id.itemSeparator);
        }
    }

}
