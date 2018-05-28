package com.example.frank.final_project.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
    Toolbar toolbar;

    @BindView(R.id.cake_dashboard_name_Tv)
    TextView cakeName;

    @BindView(R.id.cake_details_Lyout)
    LinearLayout cakeDetailsLayout;

    @BindView(R.id.add_cake_photo)
    FloatingActionButton addCakePhotoBtn;

    @BindView(R.id.add_cake_Pb)
    ProgressBar progressBar;

    @BindViews({R.id.cake_dashboard_size_Tv, R.id.cake_dashboard_price_Tv, R.id.cake_dashboard_ingredients_Tv, R.id.cake_dashboard_description_Tv})
    List<TextView> cakeDetails;

    @BindView(R.id.cake_dashboard_Rv)
    RecyclerView cakePicturesList;

    private ArrayList<String> mCakePictures;
    private Cake mCake;
    private Uri mLocalPictureUri;
    private String mFileName;
    private LinearLayoutManager mLayoutManager;
    private CakePictureAdapter mPhotoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cake_dashboard);
        ButterKnife.bind(this);

        // Setup tool bar button
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        setBackButtonFunction();

        // Customer can not add photo in mCake details
        if(CurrentUser.getUserRole() == User.Role.CUSTOMER){
            addCakePhotoBtn.setVisibility(View.GONE);
        }

        // Read current cake
        mCake = CurrentUser.getCurrentCake();
        // Attach mCake info into list demonstration
        attachCakeInfo();
        // Attach photo from database to performance
        attachPhotos();


    }

    /**
     *  Open local system to choose a picture file as upload photo
     */
    @OnClick(R.id.add_cake_photo)
    public void addCakePhoto(){
        openSystemFileSelection();
    }

    /**
     * Browse file folder and select the certificate file
     */
    private void openSystemFileSelection() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType(Constant.FILE_TYPE);
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
                    mLocalPictureUri = data.getData();
                    mFileName = mLocalPictureUri.getLastPathSegment();
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
        confirmDialogFragment.show(getString(R.string.exit_tittle), getString(R.string.cake_choose_tittle) + mFileName,
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

    /**
     * Save photo info into database and upload photo into storage
     */
    private void savePhoto(){
        showLoading();
        StorageReference cakePictureRef = FirebaseStorage.getInstance().getReference(Constant.CHEF).child(CurrentUser.getUserId()).child(Constant.STORE).child(Constant.MENU).child(mCake.getId()).child(Constant.CAKE_PHOTO_ID + Integer.toString(mCake.getPhotoNum()+1));
        cakePictureRef.putFile(mLocalPictureUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    // Save mCake picture link uri in mCake database
                    final String cakeNewPictureUri = task.getResult().getDownloadUrl().toString();
                    String currentCakeUri = CurrentUser.getCurrentCake().getImageUrl();
                    DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(CurrentUser.getUserId()).child(Constant.STORE).child(Constant.MENU);
                    menuRef.child(mCake.getId()).child(Constant.CAKE_IMAGEURI).setValue(currentCakeUri + " " + cakeNewPictureUri);
                    menuRef.child(mCake.getId()).child(Constant.CAKE_PHOTO_NUM).setValue(mCake.getPhotoNum()+1);
                    menuRef.child(mCake.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CurrentUser.setCurrentCake(dataSnapshot.getValue(Cake.class));
                            mCakePictures.add(cakeNewPictureUri);
                            mPhotoAdapter.notifyDataSetChanged();
                            showContents();
                            Toast.makeText(getApplicationContext(), getString(R.string.cake_add_picture_add_confirm), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), getString(R.string.error_info) + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     *  Attach mCake info into list demonstration
     */
    private void attachCakeInfo() {
        cakeName.setText(mCake.getName());
        cakeDetails.get(0).setText(mCake.getSize());
        cakeDetails.get(1).setText(Double.toString(mCake.getPrice()));
        cakeDetails.get(2).setText(mCake.getIngredients());
        cakeDetails.get(3).setText(mCake.getDescription());
    }

    /**
     * Set back button function for tool bar
     */
    private void setBackButtonFunction(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     *  Attach photo from database to performance
     */
    private void attachPhotos(){
        String cakeImageUri = mCake.getImageUrl();
        mCakePictures = new ArrayList<>();
        if(cakeImageUri != null){
            String[] temp = mCake.getImageUrl().split(" ");
            for(String x : temp){
                mCakePictures.add(x);
            }
            mPhotoAdapter = new CakePictureAdapter(mCakePictures, this);
            cakePicturesList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            cakePicturesList.setLayoutManager(mLayoutManager);
            cakePicturesList.setItemAnimator(new DefaultItemAnimator());
            cakePicturesList.setAdapter(mPhotoAdapter);
        }
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        cakeDetailsLayout.setVisibility(View.GONE);
        addCakePhotoBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     *  Show contents
     */
    private void showContents(){
        cakeDetailsLayout.setVisibility(View.VISIBLE);
        addCakePhotoBtn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

}
