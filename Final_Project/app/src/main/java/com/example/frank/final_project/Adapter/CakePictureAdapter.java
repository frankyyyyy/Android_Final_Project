package com.example.frank.final_project.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.frank.final_project.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CakePictureAdapter extends RecyclerView.Adapter<CakePictureAdapter.ViewHolder> {


    private ArrayList<String> pictureUris;
    private Context context;

    public CakePictureAdapter(ArrayList<String> pictureUris, Context context){
        super();
        this.pictureUris = pictureUris;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cake, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.with(context).load(pictureUris.get(position)).placeholder(R.drawable.loading).error(R.drawable.loading_error_404).into(holder.getCakeItem());
    }

    @Override
    public int getItemCount() {
        return pictureUris.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cake_item)
        ImageView mCakeItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public ImageView getCakeItem(){
            return mCakeItem;
        }
    }

}
