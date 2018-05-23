package com.example.frank.final_project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frank.final_project.Activity.ChatActivity;
import com.example.frank.final_project.Activity.StoreDashboard;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.R;
import com.example.frank.final_project.ViewHolder.StoreListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

/**
 * Created by Frank on 2018/5/17.
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
        final String chefId = chef.getId();
        final String chefName = chef.getName();
        holder.getStoreItem().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra(Constant.CHEF_ID, chefId);
                chatIntent.putExtra(Constant.CHEF_NAME, chefName);
                context.startActivity(chatIntent);
                return true;
            }
        });
        holder.getStoreItem().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent storeIntent = new Intent(context, StoreDashboard.class);
                storeIntent.putExtra(Constant.CHEF_ID, chefId);
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
