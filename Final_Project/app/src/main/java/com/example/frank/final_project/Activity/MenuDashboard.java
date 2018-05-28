package com.example.frank.final_project.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.frank.final_project.Adapter.MenuViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Cake;
import com.example.frank.final_project.Model.Contact;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.example.frank.final_project.Service.MessageNotifier;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuDashboard extends AppCompatActivity {

    @BindView(R.id.store_dashboard_page_menu_Rv)
    RecyclerView menuList;

    @BindView(R.id.store_dashboard_page_Pb)
    ProgressBar progressbarView;

    @BindView(R.id.store_dashboard_page_float_Btn)
    FloatingActionButton floatBtn;

    private String mUserId;
    private DatabaseReference mMenuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_dashboard);
        ButterKnife.bind(this);

        // Show loading progress bar
        showLoading();
        // Load current user Id
        mUserId = CurrentUser.getUserId();
        // Load menu reference in database
        mMenuRef = (CurrentUser.getUserRole() == User.Role.CHEF) ?
                // Current user is a chef
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(mUserId).child(Constant.STORE).child(Constant.MENU) :
                // Current user is a customer
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(CurrentUser.getOppositeId()).child(Constant.STORE).child(Constant.MENU);
        // Change button for customer user
        if(CurrentUser.getUserRole() == User.Role.CUSTOMER){
            floatBtn.setImageResource(R.drawable.message_btn);
        }
        // Bind store menu list
        attachMenu();

        // Start message notification service if it is not activated
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
     * Float button on click function
     */
    @OnClick(R.id.store_dashboard_page_float_Btn)
    public void onClickFloatBtn(){
        // Provide chat service to customer user
        if(CurrentUser.getUserRole() == User.Role.CUSTOMER){
            showLoading();
            writeContact();
        }else{
            // Provide cake add service to chef user
            Intent addCakeIntent = new Intent(this, AddCakeActivity.class);
            startActivity(addCakeIntent);
        }
    }

    /**
     *  Add new contact into personal contact list
     */
    private void writeContact() {
        // Set references
        final DatabaseReference customerContactRef = FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(mUserId).child(Constant.CONTACT);
        final DatabaseReference chefContactRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(CurrentUser.getOppositeId()).child(Constant.CONTACT);
        // Find contact existence
        FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(mUserId).child(Constant.MESSAGES).child(CurrentUser.getOppositeId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Contact is not existed
                if(dataSnapshot.getValue() == null){
                    // Get contact key
                    final String contactKey = customerContactRef.push().getKey();
                    final Contact contact = new Contact();
                    // Write chef contact to customer
                    contact.setOppositeId(CurrentUser.getOppositeId());
                    if(CurrentUser.getOppositeName() != null){
                        contact.setOppositeName(CurrentUser.getOppositeName());
                    }
                    customerContactRef.child(contactKey).setValue(contact);
                    // Write customer contact to chef
                    FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(mUserId).child(Constant.NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null){
                                contact.setOppositeName(dataSnapshot.getValue(String.class));
                            }
                            contact.setOppositeId(CurrentUser.getUserId());
                            chefContactRef.child(contactKey).setValue(contact);
                            startContactActivity();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    // Contact is existed
                    startContactActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    /**
     * Start contact activity
     */
    private void startContactActivity(){
        Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
        startActivity(chatIntent);
        showContents();
    }

    /**
     *  Attach cake info to list
     */
    private void attachMenu() {
        FirebaseRecyclerOptions<Cake> options =
                new FirebaseRecyclerOptions.Builder<Cake>()
                        .setQuery(mMenuRef, Cake.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter menuAdapter = new MenuViewAdapter(options, this);
        menuList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        menuList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        menuList.setLayoutManager(mLayoutManager);
        menuList.setItemAnimator(new DefaultItemAnimator());
        menuList.setAdapter(menuAdapter);
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        menuList.setVisibility(View.GONE);
        progressbarView.setVisibility(View.VISIBLE);
    }

    /**
     *  Show menu list contents
     */
    private void showContents(){
        menuList.setVisibility(View.VISIBLE);
        progressbarView.setVisibility(View.GONE);
    }
}
