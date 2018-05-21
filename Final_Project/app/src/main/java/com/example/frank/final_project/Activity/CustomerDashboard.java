package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.example.frank.final_project.Adapter.ChefListViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.R;
import com.example.frank.final_project.Service.MessageNotifier;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerDashboard extends AppCompatActivity {

    @BindView(R.id.customer_dashboard_page_Pb)
    ProgressBar mProgressBarView;

    @BindView(R.id.customer_dashboard_page_chefList_lyout)
    ScrollView mChefListView;

    @BindView(R.id.customer_dashboard_page_chefList_Rv)
    RecyclerView mChefList;

    private DatabaseReference chefRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        ButterKnife.bind(this);

        // Load page contents
        loading();
    }

    /**
     *  Load page contents
     */
    private void loading() {
        // Show loading progress bar
        showLoading();
        // Read chef snapshot reference
        chefRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF);
        // Attach chefs
        attachChefList();
        // Show contents
        showContents();
        // Start message service
        startMessageService();
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        mChefListView.setVisibility(View.GONE);
        mProgressBarView.setVisibility(View.VISIBLE);
    }

    /**
     *  Show chef list contents
     */
    private void showContents(){
        mChefListView.setVisibility(View.VISIBLE);
        mProgressBarView.setVisibility(View.GONE);
    }

    /**
     *  Start message notification service
     */
    private void startMessageService() {
        Intent messageNotifier = new Intent(this, MessageNotifier.class);
        startService(messageNotifier);
    }

    /**
     *  Attach chef info to list
     */
    private void attachChefList(){
        FirebaseRecyclerOptions<Chef> options =
                new FirebaseRecyclerOptions.Builder<Chef>()
                        .setQuery(chefRef, Chef.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter chefListAdapter = new ChefListViewAdapter(options, this);
        mChefList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mChefList.setLayoutManager(mLayoutManager);
        mChefList.setItemAnimator(new DefaultItemAnimator());
        mChefList.setAdapter(chefListAdapter);
    }
}
