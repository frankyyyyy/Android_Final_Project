package com.example.frank.final_project.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frank.final_project.Adapter.CakePictureAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Fragment.ConfirmDialogFragment;
import com.example.frank.final_project.Model.Cake;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CakeDashboardActivity extends AppCompatActivity {

    @BindView(R.id.cake_dashboard_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.cake_dashboard_name_Tv)
    TextView mCakeName;

    @BindView(R.id.cake_details_Lyout)
    LinearLayout mCakeDetailsLayout;

    @BindView(R.id.add_cake_photo)
    FloatingActionButton mAddCakePhotoBtn;

    @BindView(R.id.add_cake_Pb)
    ProgressBar mProgressBar;

    @BindViews({R.id.cake_dashboard_size_Tv, R.id.cake_dashboard_price_Tv, R.id.cake_dashboard_ingredients_Tv, R.id.cake_dashboard_description_Tv})
    List<TextView> mCakeDetails;

    @BindView(R.id.cake_dashboard_Rv)
    RecyclerView mCakePicturesList;

    private ArrayList<String> cakePictures;
    private Cake cake;
    private Uri localPictureUri;
    private String fileName;
    private LinearLayoutManager mLayoutManager;
    private CakePictureAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cake_dashboard);
        ButterKnife.bind(this);

        // Setup tool bar button
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        setBackButtonFunction();

        // Customer can not add photo in cake details
        if(CurrentUser.getUserRole() == User.Role.CUSTOMER){
            mAddCakePhotoBtn.setVisibility(View.GONE);
        }


        cake = CurrentUser.getCurrentCake();

        attachCakeInfo();

        attachPhotos();


    }

    @OnClick(R.id.add_cake_photo)
    public void addCakePhoto(){
        openSystemFileSelection();
    }

    /**
     * Browse file folder and select the certificate file
     */
    private void openSystemFileSelection() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(fileIntent, 1234);
    }

    /**
     *  Receive file target for uploading purpose
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1234:
                if (resultCode == RESULT_OK) {
                    localPictureUri = data.getData();
                    fileName = localPictureUri.getLastPathSegment();
                    showConfirmDialog();
                }
                break;
        }
    }

    /**
     *  Show confirm dialog after selection on file
     */
    private void showConfirmDialog() {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        confirmDialogFragment.show(getString(R.string.exit_tittle), "You chose file: " + fileName,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savePhoto();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }, getFragmentManager());
    }

    private void savePhoto(){
        showLoading();
        StorageReference cakePictureRef = FirebaseStorage.getInstance().getReference(Constant.CHEF).child(CurrentUser.getUserId()).child(Constant.STORE).child(Constant.MENU).child(cake.getId()).child("photo_" + Integer.toString(cake.getPhotoNum()));
        cakePictureRef.putFile(localPictureUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    // Save cake picture link uri in cake database
                    final String cakeNewPictureUri = task.getResult().getDownloadUrl().toString();
                    String currentCakeUri = CurrentUser.getCurrentCake().getImageUrl();
                    DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(CurrentUser.getUserId()).child(Constant.STORE).child(Constant.MENU);
                    menuRef.child(cake.getId()).child(Constant.CAKE_IMAGEURI).setValue(currentCakeUri + " " + cakeNewPictureUri);
                    menuRef.child(cake.getId()).child(Constant.CAKE_PHOTO_NUM).setValue(cake.getPhotoNum()+1);
                    menuRef.child(cake.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CurrentUser.setCurrentCake(dataSnapshot.getValue(Cake.class));
                            cakePictures.add(cakeNewPictureUri);
                            photoAdapter.notifyDataSetChanged();
                            showContents();
                            Toast.makeText(getApplicationContext(), "New photo added.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    showContents();
                    String message = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void attachCakeInfo() {
        mCakeName.setText(cake.getName());
        mCakeDetails.get(0).setText(cake.getSize());
        mCakeDetails.get(1).setText(Double.toString(cake.getPrice()));
        mCakeDetails.get(2).setText(cake.getIngredients());
        mCakeDetails.get(3).setText(cake.getDescription());
    }

    private void setBackButtonFunction(){
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void attachPhotos(){
        String cakeImageUri = cake.getImageUrl();
        cakePictures = new ArrayList<>();
        if(cakeImageUri != null){
            String[] temp = cake.getImageUrl().split(" ");
            for(String x : temp){
                cakePictures.add(x);
            }
            photoAdapter = new CakePictureAdapter(cakePictures, this);
            mCakePicturesList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mCakePicturesList.setLayoutManager(mLayoutManager);
            mCakePicturesList.setItemAnimator(new DefaultItemAnimator());
            mCakePicturesList.setAdapter(photoAdapter);
        }
    }

//    @Override
//    protected void onResume() {
//        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        }else{
//            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        }
//        attachCakeInfo();
//        super.onResume();
//    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        mCakeDetailsLayout.setVisibility(View.GONE);
        mAddCakePhotoBtn.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     *  Show contents
     */
    private void showContents(){
        mCakeDetailsLayout.setVisibility(View.VISIBLE);
        mAddCakePhotoBtn.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

}
