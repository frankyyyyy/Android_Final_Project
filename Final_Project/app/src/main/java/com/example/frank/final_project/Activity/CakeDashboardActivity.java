package com.example.frank.final_project.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.example.frank.final_project.Adapter.CakePictureAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Cake;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CakeDashboardActivity extends AppCompatActivity {

    @BindView(R.id.cake_dashboard_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.cake_dashboard_Gv)
    GridView mCakePicturesList;


    String[] cakePictures;

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

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Cake cake = CurrentUser.getCurrentCake();
        String cakeImageUri = cake.getImageUrl();
        if(cakeImageUri != null){
            cakePictures = cake.getImageUrl().split(" ");
            mCakePicturesList.setAdapter(new CakePictureAdapter(cakePictures, this));
        }


    }


}
