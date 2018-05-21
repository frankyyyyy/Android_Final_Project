package com.example.frank.final_project.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frank.final_project.Model.Cake;
import com.example.frank.final_project.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank on 2018/5/21.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.menu_list_cake_container_Iv)
    ImageView mCakeImage;

    @BindView(R.id.menu_list_cake_container_cakeName_Tv)
    TextView mCakeName;

    @BindView(R.id.menu_list_cake_container_cakeDescription_Tv)
    TextView mCakeDescription;

    public MenuViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Cake cake){
        mCakeName.setText(cake.getName());
        mCakeDescription.setText(cake.getDescription());
    }

    public ImageView getCakeImageView(){
        return mCakeImage;
    }
}
