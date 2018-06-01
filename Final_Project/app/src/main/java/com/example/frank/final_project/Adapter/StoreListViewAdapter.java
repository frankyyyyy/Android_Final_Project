package com.example.frank.final_project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frank.final_project.Activity.MenuDashboardActivity;
import com.example.frank.final_project.Constant.CircleTransform;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.R;
import com.example.frank.final_project.ViewHolder.StoreListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 *  Store list adapter to load store info
 *  inherited from FirebaseRecyclerAdapter.
 */
public class StoreListViewAdapter extends FirebaseRecyclerAdapter<Chef, StoreListViewHolder>{
    private Context context;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public StoreListViewAdapter(@NonNull FirebaseRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull StoreListViewHolder holder, int position, @NonNull final Chef chef) {
        holder.bind(chef);
        // Set chef photo as head photo
        String photoUri = chef.getHeadPhotoUri();
        Picasso.with(context).
                load(photoUri).
                placeholder(R.drawable.chef_default_headphoto).
                error(R.drawable.chef_default_headphoto).
                transform(new CircleTransform()).
                into(holder.getChefHeadPhoto());
        // Set up store item on click funtion
        final String chefId = chef.getId();
        final String chefName = chef.getName();
        holder.getStoreItem().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Save chef info as opposite
                CurrentUser.setOppositeId(chefId);
                if(chefName != null){
                    CurrentUser.setOppositeName(chefName);
                }
                CurrentUser.setStore(chef.getStore());
                // Go to store dashboard
                Intent storeIntent = new Intent(context, MenuDashboardActivity.class);
                context.startActivity(storeIntent);
            }
        });
    }

    @NonNull
    @Override
    public StoreListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoreListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chef_list_chef_container, parent, false));
    }
}
