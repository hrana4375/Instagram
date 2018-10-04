package com.example.instaclone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.NumberViewHolder>{

    public List<Post> postsList;

    public FeedAdapter(List<Post> postsList) {
        this.postsList = postsList;
    }
    @NonNull
    @Override
    public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postlayout,parent,false);

        return new NumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NumberViewHolder holder, int position) {

        holder.author.setText(postsList.get(position).getAuthor());
        holder.postImg.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {

        return  postsList.size();

    }


    public class NumberViewHolder extends RecyclerView.ViewHolder {

        TextView author;
        ImageView postImg;
        
        public NumberViewHolder(View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.author);
            postImg = itemView.findViewById(R.id.postImg);

        }
    }
}
