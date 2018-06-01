package com.example.frank.final_project.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *  Holding store views
 *  providing to store list.
 */
public class StoreListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.chef_list_headPhoto_Iv)
    ImageView chefHeadPhoto;

    @BindView(R.id.chef_list_storeName_Tv)
    TextView storeName;

    @BindView(R.id.chef_list_chefName_Tv)
    TextView chefName;

    @BindView(R.id.chef_list_storeRate_Rb)
    RatingBar ratingBar;

    @BindView(R.id.store_item)
    LinearLayout storeItem;

    public StoreListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    /**
     *  Bind attributes to model data
     * @param chef
     */
    public void bind(Chef chef){
        storeName.setText(chef.getStore().getName());
        chefName.setText(chef.getName());
        ratingBar.setRating(chef.getStore().getRate());
    }

    /**
     *  Return store item to adapter
     * @return storeItem
     */
    public LinearLayout getStoreItem() { return storeItem;}

    /**
     *  Return store item to adapter
     * @return chefHeadPhoto
     */
    public ImageView getChefHeadPhoto(){ return chefHeadPhoto; }
}
