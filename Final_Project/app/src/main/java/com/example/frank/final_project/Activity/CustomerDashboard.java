package com.example.frank.final_project.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.frank.final_project.Adapter.ChefListViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerDashboard extends AppCompatActivity {

    @BindView(R.id.customer_dashboard_page_chefList_Rv)
    RecyclerView mChefList;

    @BindView(R.id.customer_dashboard_page_Pb)
    ProgressBar mProgressBar;

    private DatabaseReference chefRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        ButterKnife.bind(this);

        loading();





    }

    private void loading() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        chefRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF);
        attachChefList();
        mChefList.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

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
