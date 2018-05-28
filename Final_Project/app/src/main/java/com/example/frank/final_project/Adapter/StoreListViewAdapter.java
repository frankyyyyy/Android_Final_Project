package com.example.frank.final_project.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frank.final_project.Activity.MenuDashboard;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.Model.CurrentUser;
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
        holder.getStoreItem().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Save chef info as opposite
                CurrentUser.setOppositeId(chefId);
                if(chefName != null){
                    CurrentUser.setOppositeName(chefName);
                }
                // Go to store dashboard
                Intent storeIntent = new Intent(context, MenuDashboard.class);
                context.startActivity(storeIntent);
                // Stop previous activity
                Activity mainActivity = (Activity) context;
                mainActivity.finish();
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
