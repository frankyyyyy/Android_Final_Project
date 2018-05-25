package com.example.frank.final_project.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.frank.final_project.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CakePictureAdapter extends BaseAdapter {


    private String[] pictureUris;
    private Context context;

    public CakePictureAdapter(String[] pictureUris, Context context){
        super();
        this.pictureUris = pictureUris;
        this.context = context;
    }
    @Override
    public int getCount() {
        return pictureUris.length;
    }

    @Override
    public String getItem(int position) {
        return pictureUris[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_cake, null);
        ViewHolder viewHolder = new ViewHolder(convertView);
        Picasso.with(context).load(getItem(position)).into(viewHolder.mCakePicture);
        return null;
    }

    class ViewHolder {
        @BindView(R.id.cake_item)
        ImageView mCakePicture;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
