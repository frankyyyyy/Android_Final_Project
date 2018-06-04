package com.example.frank.final_project.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frank.final_project.R;
import com.example.frank.final_project.ViewHolder.CakePictureViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 *  Cake picture adapter on loading picture uri to fulfill widget
 */
public class CakePictureAdapter extends RecyclerView.Adapter<CakePictureViewHolder> {


    private ArrayList<String> pictureUris;
    private Context context;

    public CakePictureAdapter(ArrayList<String> pictureUris, Context context){
        super();
        this.pictureUris = pictureUris;
        this.context = context;
    }

    @NonNull
    @Override
    public CakePictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cake, parent, false);
        return new CakePictureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CakePictureViewHolder holder, int position) {
        Picasso.with(context).
                load(pictureUris.get(position)).
                placeholder(R.drawable.loading).
                error(R.drawable.loading_error_404).
                into(holder.getCakeItem());
    }

    @Override
    public int getItemCount() {
        return pictureUris.size();
    }

}
