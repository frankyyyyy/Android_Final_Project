package com.example.frank.final_project.ViewHolder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.frank.final_project.Activity.ChatActivity;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Frank on 2018/5/17.
 */

public class ChefListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.chef_list_storeName)
    TextView storeName;

    @BindView(R.id.chef_list_chefName)
    TextView chefName;

    @BindView(R.id.chef_list_storeRate)
    RatingBar storeRate;

    @BindView(R.id.chef_list_chef_item)
    LinearLayout chefItem;

    public ChefListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Chef chef){
        storeName.setText(chef.getStore().getName());
        chefName.setText(chef.getName());
        storeRate.setRating(chef.getStore().getRate());
    }

    public LinearLayout getChefItem(){
        return chefItem;
    }


}
