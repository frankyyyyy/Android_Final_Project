package com.example.frank.final_project.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.frank.final_project.Adapter.ChefListViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.R;
import com.example.frank.final_project.ViewHolder.ChefListViewHolder;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class CustomerDashboard extends AppCompatActivity {

    @BindView(R.id.customer_dashboard_page_chefList)
    RecyclerView chefList;

    @BindView(R.id.customer_dashboard_page_progressBar)
    ProgressBar progressBar;

    private DatabaseReference chefRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        ButterKnife.bind(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        chefRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF);

        attachChefList();

        chefList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
//
//
//        getChefIds();
    }

    private void attachChefList(){
        FirebaseRecyclerOptions<Chef> options =
                new FirebaseRecyclerOptions.Builder<Chef>()
                        .setQuery(chefRef, Chef.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter chefListAdapter = new ChefListViewAdapter(options, this);
        chefList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        chefList.setLayoutManager(mLayoutManager);
        chefList.setItemAnimator(new DefaultItemAnimator());
        chefList.setAdapter(chefListAdapter);
    }

}
