package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.frank.final_project.Adapter.StoreListViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.Model.Customer;
import com.example.frank.final_project.R;
import com.example.frank.final_project.Service.MessageNotifier;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerDashboard extends AppCompatActivity {

    @BindView(R.id.customer_dashboard_page_Pb)
    ProgressBar mProgressBarView;

    @BindView(R.id.customer_dashboard_page_chefList_Rv)
    RecyclerView mChefList;

    private DatabaseReference chefRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        ButterKnife.bind(this);

        // Show loading progress bar
        showLoading();
        // Read chef snapshot reference
        chefRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF);
        // Attach chefs
        attachChefList();
        // Show contents
        showContents();
        // Start message service
//        startMessageService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
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
        mChefList.setVisibility(View.GONE);
        mProgressBarView.setVisibility(View.VISIBLE);
    }

    /**
     *  Show chef list contents
     */
    private void showContents(){
        mChefList.setVisibility(View.VISIBLE);
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
        FirebaseRecyclerAdapter chefListAdapter = new StoreListViewAdapter(options, this);
        mChefList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mChefList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mChefList.setLayoutManager(mLayoutManager);
        mChefList.setItemAnimator(new DefaultItemAnimator());
        mChefList.setAdapter(chefListAdapter);
    }

//    @Override
//    protected void onStop() {
//        FirebaseAuth.getInstance().signOut();
//        super.onStop();
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        FirebaseAuth.getInstance().signOut();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mChefList.requestFocus();
//        Log.d("wodetian", "wentizaizheli");
//    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//        if(FirebaseAuth.getInstance().getCurrentUser() == null){
//            FirebaseAuth.getInstance().signInWithEmailAndPassword(mUserEmail, mUserPassword);
//        }
//    }
}
