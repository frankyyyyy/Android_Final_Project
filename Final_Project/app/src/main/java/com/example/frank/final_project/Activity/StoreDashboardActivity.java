package com.example.frank.final_project.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.frank.final_project.Adapter.ContactListViewAdapter;
import com.example.frank.final_project.Adapter.MessageListViewAdapter;
import com.example.frank.final_project.Adapter.StoreListViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.Model.Contact;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.Message;
import com.example.frank.final_project.R;
import com.example.frank.final_project.Service.MessageNotifier;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreDashboardActivity extends AppCompatActivity {

    @BindView(R.id.store_dashboard_toolbar)
    Toolbar toolbar;

    @BindView(R.id.customer_dashboard_page_Pb)
    ProgressBar progressBarView;

    @BindView(R.id.customer_dashboard_page_chefList_Rv)
    RecyclerView chefList;

    private DatabaseReference mChefRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_dashboard);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        // Show loading progress bar
        showLoading();
        // Read chef snapshot reference
        mChefRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF);
        // Attach chefs
        attachChefList();

        // Start message service if it is not running
        if(!messageServiceRunning()){
            Intent messageNotifier = new Intent(this, MessageNotifier.class);
            startService(messageNotifier);
        }
        // Show contents
        showContents();
    }



    /**
     *  Check if the message notifier service is running
     * @return result
     */
    private boolean messageServiceRunning() {
        ActivityManager myManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for(ActivityManager.RunningServiceInfo service: runningService){
            if(service.service.getClassName().toString().equals(Constant.MESSAGE_NOTIFIER_SERVICE)){
                return true;
            }
        }
        return false;
    }

    /**
     *
     *  On menu created
     * @param menu
     * @return result
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     *  On menu item selected
     * @param item
     * @return result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_message:
                Intent contactIntent = new Intent(this, ContactActivity.class);
                startActivity(contactIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        chefList.setVisibility(View.GONE);
        progressBarView.setVisibility(View.VISIBLE);
    }

    /**
     *  Show chef list contents
     */
    private void showContents(){
        chefList.setVisibility(View.VISIBLE);
        progressBarView.setVisibility(View.GONE);
    }

    private void attachChefList() {
        FirebaseRecyclerOptions<Chef> options =
                new FirebaseRecyclerOptions.Builder<Chef>()
                        .setQuery(mChefRef, Chef.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter storeListViewAdapter = new StoreListViewAdapter(options, this);
        chefList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        chefList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        chefList.setLayoutManager(mLayoutManager);
        chefList.setItemAnimator(new DefaultItemAnimator());
        chefList.setAdapter(storeListViewAdapter);
    }

}
