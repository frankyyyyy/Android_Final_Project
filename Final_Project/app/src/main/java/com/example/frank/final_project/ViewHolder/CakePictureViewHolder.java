package com.example.frank.final_project.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.frank.final_project.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *  Holding cake picture views
 *  providing to cake detail picture list.
 */
public class CakePictureViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.cake_item)
    ImageView cakeItem;

    public CakePictureViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     *  Return cake view to adapter
     * @return cakeItem
     */
    public ImageView getCakeItem(){
        return cakeItem;
    }
}