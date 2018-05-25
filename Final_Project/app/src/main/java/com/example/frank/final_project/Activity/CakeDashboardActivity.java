package com.example.frank.final_project.Activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.frank.final_project.Adapter.CakeDashboardMenuAdapter;
import com.example.frank.final_project.Adapter.CakePictureAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Cake;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CakeDashboardActivity extends AppCompatActivity {

    @BindView(R.id.cake_dashboard_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.cake_dashboard_Gv)
    GridView mCakePicturesList;

    @BindView(R.id.cake_dashboard_Vp)
    ViewPager mCakeDashboardMenu;

    private String[] cakePictures;
    private Cake cake;

    private ArrayList<View> aList;
    private ArrayList<String> sList;
    private CakeDashboardMenuAdapter cakeDashboardMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cake_dashboard);
        ButterKnife.bind(this);

        // Setup tool bar button
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        setBackButtonFunction();

        cake = CurrentUser.getCurrentCake();


        attachMenu();

        attachPhotos();
    }

    private void setBackButtonFunction(){
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void attachMenu(){
        aList = new ArrayList<View>();
        LayoutInflater li = getLayoutInflater();
        aList.add(li.inflate(R.layout.content_cake_dashboard_info,null,false));
        aList.add(li.inflate(R.layout.content_cake_dashboard_photo,null,false));
        sList = new ArrayList<String>();
        sList.add("Cake Info.");
        sList.add("Photo");
        cakeDashboardMenuAdapter = new CakeDashboardMenuAdapter(aList, sList);
        mCakeDashboardMenu.setAdapter(cakeDashboardMenuAdapter);
    }

    private void attachPhotos(){
        String cakeImageUri = cake.getImageUrl();
        if(cakeImageUri != null){
            cakePictures = cake.getImageUrl().split(" ");
            mCakePicturesList.setAdapter(new CakePictureAdapter(cakePictures, this));
        }
    }




}
