package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.frank.final_project.Adapter.MenuViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Cake;
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

    private String userId;
    private User.Role userRole;
    private DatabaseReference menuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_dashboard);
        ButterKnife.bind(this);

        loading();
    }

    private void loading() {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        menuRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.STORE).child(Constant.MENU);
//        verifyRole();
        attachMenu();
    }

    private void verifyRole(){
        if(FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(userId) == null){
            userRole = User.Role.CHEF;
            menuRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.STORE).child(Constant.MENU);
        }else{
            userRole = User.Role.CUSTOMER;
            Intent intent = getIntent();
            if(intent != null){
                String chefId = intent.getStringExtra(Constant.CHEF_ID);
                menuRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(chefId).child(Constant.STORE).child(Constant.MENU);
            }
        }
    }

    @OnClick(R.id.store_dashboard_page_add_Btn)
    public void addNewCake(){
        Intent addCakeIntent = new Intent(this, AddCakeActivity.class);
        startActivity(addCakeIntent);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.chef_dashboard_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

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

    @Override
    protected void onResume() {
        super.onResume();
        attachMenu();
    }
}
