package com.example.frank.final_project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frank.final_project.Activity.CakeDashboardActivity;
import com.example.frank.final_project.Model.Cake;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.R;
import com.example.frank.final_project.ViewHolder.MenuViewHolder;
import com.example.frank.final_project.ViewHolder.MessageListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

/**
 * Created by Frank on 2018/5/21.
 */

public class MenuViewAdapter extends FirebaseRecyclerAdapter<Cake, MenuViewHolder> {
    private final Context context;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MenuViewAdapter(@NonNull FirebaseRecyclerOptions<Cake> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull final Cake cake) {
        holder.bind(cake);
        if(cake.getImageUrl() != null){
            Picasso.with(context).load(cake.getImageUrl()).into(holder.getCakeImageView());
        }
        holder.getCakeItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cakeIntent = new Intent(context, CakeDashboardActivity.class);
                CurrentUser.setCurrentCake(cake);
                context.startActivity(cakeIntent);
            }
        });
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_cake_container, parent, false));
    }
}
