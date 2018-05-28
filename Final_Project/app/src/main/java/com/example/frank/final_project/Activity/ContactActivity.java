package com.example.frank.final_project.Activity;

import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactActivity extends AppCompatActivity {

    @BindView(R.id.contact_toolbar)
    Toolbar toolbar;

    @BindView(R.id.contact_list_Rv)
    RecyclerView contactList;

    private DatabaseReference mContactRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);

        // Setup tool bar button
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        // Set back button function
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Read current user role
        if(CurrentUser.getUserRole() == User.Role.CUSTOMER){
            mContactRef = FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(CurrentUser.getUserId()).child(Constant.CONTACT);
        }else{
            mContactRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(CurrentUser.getUserId()).child(Constant.CONTACT);
        }

        // Attach contact info to demonstration list
        attachContact();
    }

    /**
     *  Attach contact info to list
     */
    private void attachContact() {
        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>()
                        .setQuery(mContactRef, Contact.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter contactListViewAdapter = new ContactListViewAdapter(options, this);
        contactList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        contactList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        contactList.setLayoutManager(mLayoutManager);
        contactList.setItemAnimator(new DefaultItemAnimator());
        contactList.setAdapter(contactListViewAdapter);
    }
}
