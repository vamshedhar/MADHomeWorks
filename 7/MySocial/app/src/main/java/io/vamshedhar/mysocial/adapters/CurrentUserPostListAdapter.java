package io.vamshedhar.mysocial.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

import io.vamshedhar.mysocial.R;
import io.vamshedhar.mysocial.objects.Post;
import io.vamshedhar.mysocial.objects.User;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/21/17 6:19 PM.
 * vchinta1@uncc.edu
 */

public class CurrentUserPostListAdapter extends RecyclerView.Adapter<CurrentUserPostListAdapter.ViewHolder> {

    ArrayList<Post> posts;
    Context context;
    CurrentUserPostListInterface IData;
    User currentUser;

    public interface CurrentUserPostListInterface {
        void deletePost(Post post);
    }

    public CurrentUserPostListAdapter(ArrayList<Post> posts, Context context, CurrentUserPostListInterface IData, User currentUser) {
        this.posts = posts;
        this.context = context;
        this.IData = IData;
        this.currentUser = currentUser;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currentuser_post_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Post post = posts.get(position);

        holder.userName.setText(currentUser.getFullName());

        holder.deletePost.setTag(position);

        holder.deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView deleteBtn = (ImageView) view;

                int index = (int) deleteBtn.getTag();

                IData.deletePost(posts.get(index));
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
        ImageView deletePost;
        View itemSeparator;

        public ViewHolder(View itemView) {
            super(itemView);
            userName  = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            postContent = itemView.findViewById(R.id.postContent);
            itemSeparator = itemView.findViewById(R.id.itemSeparator);
            deletePost = itemView.findViewById(R.id.deletePostBtn);
        }
    }
}
