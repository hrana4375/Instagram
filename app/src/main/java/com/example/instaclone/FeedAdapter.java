package com.example.instaclone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.NumberViewHolder>{

    private Context context;
    public List<Post> postsList;

    public FeedAdapter(Context context) {
        this.context = context;
    }
    @NonNull
    @Override
    public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postlayout,parent,false);
        NumberViewHolder numberViewHolder = new NumberViewHolder(view);

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NumberViewHolder holder, int position) {
        Post post = postsList.get(position);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    void swap(ArrayList<Post> postsList) {
        this.postsList = postsList;

        notifyDataSetChanged();

    }

    public class NumberViewHolder extends RecyclerView.ViewHolder {


        public NumberViewHolder(View itemView) {
            super(itemView);
        }
    }
}
