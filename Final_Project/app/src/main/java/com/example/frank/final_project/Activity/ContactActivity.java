package com.example.frank.final_project.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.frank.final_project.Adapter.ContactListViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Contact;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactActivity extends AppCompatActivity {

    @BindView(R.id.contact_page_Rv)
    RecyclerView mContactList;

    @BindView(R.id.contact_page_empty_Tv)
    TextView mEmptyText;

    private DatabaseReference contactRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);


        if(CurrentUser.getUserRole() == User.Role.CUSTOMER){
            contactRef = FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(CurrentUser.getUserId()).child(Constant.CONTACT);
        }else{
            contactRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(CurrentUser.getUserId()).child(Constant.CONTACT);
        }

        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    attachChefList();
                }else{
                    mContactList.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     *  Attach chef info to list
     */
    private void attachChefList(){
        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>()
                        .setQuery(contactRef, Contact.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter chefListAdapter = new ContactListViewAdapter(options, this);
        mContactList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mContactList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mContactList.setLayoutManager(mLayoutManager);
        mContactList.setItemAnimator(new DefaultItemAnimator());
        mContactList.setAdapter(chefListAdapter);
    }
}
