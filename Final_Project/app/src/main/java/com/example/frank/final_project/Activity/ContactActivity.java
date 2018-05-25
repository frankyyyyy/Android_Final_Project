package com.example.frank.final_project.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.frank.final_project.Adapter.ContactListViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Contact;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactActivity extends AppCompatActivity {

    @BindView(R.id.contact_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.contact_list_Rv)
    RecyclerView mContactList;

    private DatabaseReference contactRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);


        // Setup tool bar button
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        // Set back button function
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        if(CurrentUser.getUserRole() == User.Role.CUSTOMER){
            contactRef = FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(CurrentUser.getUserId()).child(Constant.CONTACT);
        }else{
            contactRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(CurrentUser.getUserId()).child(Constant.CONTACT);
        }


        attachMenu();
    }

    /**
     *  Attach cake info to list
     */
    private void attachMenu() {
        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>()
                        .setQuery(contactRef, Contact.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter contactListViewAdapter = new ContactListViewAdapter(options, this);
        mContactList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mContactList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mContactList.setLayoutManager(mLayoutManager);
        mContactList.setItemAnimator(new DefaultItemAnimator());
        mContactList.setAdapter(contactListViewAdapter);
    }
}
