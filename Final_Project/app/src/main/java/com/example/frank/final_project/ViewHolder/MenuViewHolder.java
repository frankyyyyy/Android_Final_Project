package com.example.frank.final_project.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frank.final_project.Model.Cake;
import com.example.frank.final_project.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *  Holding cake views
 *  providing to menu list.
 */
public class MenuViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.cake_item)
    LinearLayout cakeItem;

    @BindView(R.id.menu_list_cake_container_Iv)
    ImageView cakeImage;

    @BindView(R.id.menu_list_cake_container_cakeName_Tv)
    TextView cakeName;

    @BindView(R.id.menu_list_cake_container_price_Tv)
    TextView cakePrice;

    public MenuViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Bind attributes to views
     * @param cake
     */
    public void bind(Cake cake){
        cakeName.setText(cake.getName());
        cakePrice.setText(Double.toString(cake.getPrice()) + " AUD");
    }

    /**
     *  Return cake image view to adapter
     * @return cakeImage
     */
    public ImageView getCakeImageView(){
        return cakeImage;
    }

    /**
     *  Return cake view to adapter
     * @return cakeItem
     */
    public LinearLayout getCakeItem(){
        return cakeItem;
    }
}
