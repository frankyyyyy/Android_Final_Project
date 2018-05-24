package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.example.frank.final_project.Adapter.MenuViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Cake;
import com.example.frank.final_project.Model.Contact;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreDashboard extends AppCompatActivity {

    @BindView(R.id.store_dashboard_page_menu_Rv)
    RecyclerView mMenuList;

    @BindView(R.id.store_dashboard_page_Pb)
    ProgressBar mProgressbarView;

    @BindView(R.id.store_dashboard_page_float_Btn)
    FloatingActionButton mFloatBtn;

    private String userId;
    private DatabaseReference menuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_dashboard);
        ButterKnife.bind(this);

        showLoading();
        // Load current user Id
        userId = CurrentUser.getUserId();
        // Load menu reference in database
        menuRef = (CurrentUser.getUserRole() == User.Role.CHEF) ?
                // Current user is a chef
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.STORE).child(Constant.MENU) :
                // Current user is a customer
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(CurrentUser.getOppositeId()).child(Constant.STORE).child(Constant.MENU);
        // Change button for customer user
        if(CurrentUser.getUserRole() == User.Role.CUSTOMER){
            mFloatBtn.setImageResource(R.drawable.message_btn);
        }
        // Bind store menu list
        attachMenu();
        showContents();
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

    @OnClick(R.id.store_dashboard_page_float_Btn)
    public void onClickFloatBtn(){
        if(CurrentUser.getUserRole() == User.Role.CUSTOMER){
            showLoading();
            writeContact();
        }else{
            Intent addCakeIntent = new Intent(this, AddCakeActivity.class);
            startActivity(addCakeIntent);
        }
    }

    private void writeContact() {
        // Set references
        final DatabaseReference customerContactRef = FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(userId).child(Constant.CONTACT);
        final DatabaseReference chefContactRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(CurrentUser.getOppositeId()).child(Constant.CONTACT);
        // Find contact existence
        FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(userId).child(Constant.MESSAGES).child(CurrentUser.getOppositeId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(userId).child(Constant.NAME).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        .setQuery(menuRef, Cake.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter menuAdapter = new MenuViewAdapter(options, this);
        mMenuList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mMenuList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mMenuList.setLayoutManager(mLayoutManager);
        mMenuList.setItemAnimator(new DefaultItemAnimator());
        mMenuList.setAdapter(menuAdapter);
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        mMenuList.setVisibility(View.GONE);
        mProgressbarView.setVisibility(View.VISIBLE);
    }

    /**
     *  Show menu list contents
     */
    private void showContents(){
        mMenuList.setVisibility(View.VISIBLE);
        mProgressbarView.setVisibility(View.GONE);
    }

    /**
     *  Loading fail, return to previous activity and show error message
     */
    private void tryAgainWarning(){
        finish();
        Toast.makeText(getApplicationContext(), getString(R.string.try_again_warning), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        if(CurrentUser.getUserRole() == User.Role.CUSTOMER){
            Intent customerIntent = new Intent(this, CustomerDashboard.class);
            startActivity(customerIntent);
        }
        super.onStop();
    }

}
