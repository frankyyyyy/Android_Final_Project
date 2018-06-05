package com.example.frank.final_project.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.frank.final_project.Adapter.StoreListViewAdapter;
import com.example.frank.final_project.Constant.CircleTransform;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Constant.Constant_Debug;
import com.example.frank.final_project.Fragment.ConfirmDialogFragment;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.example.frank.final_project.Service.MessageNotifier;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *  Display all stores in list to customer
 */
public class StoreDashboardActivity extends AppCompatActivity {

    @BindView(R.id.store_dashboard_toolbar)
    Toolbar toolbar;

    @BindView(R.id.store_dashboard_headphoto_Iv)
    ImageView headPhoto;

    @BindView(R.id.customer_dashboard_page_Pb)
    ProgressBar progressBarView;

    @BindView(R.id.customer_dashboard_page_chefList_Rv)
    RecyclerView chefList;

    private DatabaseReference mChefRef;
    private Uri photoLocalUri;
    private String photoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_dashboard);
        ButterKnife.bind(this);
        Log.d(Constant_Debug.TAG_STORE, Constant_Debug.STORE_CREATED);
        setSupportActionBar(toolbar);


        // Show loading progress bar
        showLoading();
        // Read chef snapshot reference
        mChefRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF);
        // Show head photo
        setupHeadPhoto();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Constant_Debug.TAG_STORE, Constant_Debug.STORE_DESTROYED);
    }

    /**
     *  Setup head photo from database resource
     */
    private void setupHeadPhoto(){
        Picasso.with(this).
                load(CurrentUser.getUserHeadPhotoUri()).
                placeholder(R.drawable.man_default_headphoto).
                error(R.drawable.man_default_headphoto).
                transform(new CircleTransform()).
                into(headPhoto);
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
        // Hide store switch in store dashboard to customer
        menu.findItem(R.id.menu_store_switch).setVisible(false);
        // Hide map in store dashboard to customer
        menu.findItem(R.id.menu_map).setVisible(false);
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
            case R.id.menu_headphoto:
                showPictureWarningDialog();
                return true;
            case R.id.menu_sign_out:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Samsung phone picture upload warning
     */
    private void showPictureWarningDialog() {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        confirmDialogFragment.show(getString(R.string.upload_dialog_samsung_warning_tittle), getString(R.string.upload_dialog_samsung_warning_content),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openSystemFileSelection();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }, getFragmentManager());
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
                    photoLocalUri = data.getData();
                    photoName = photoLocalUri.getLastPathSegment();
                    showConfirmWarningDialog();
                }
                break;
        }
    }

    /**
     *  Confirm button on click dialog
     */
    private void showConfirmWarningDialog() {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        confirmDialogFragment.show(getString(R.string.exit_tittle), getString(R.string.add_photo_warning) + photoName,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadHeadPhoto();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }, getFragmentManager());
    }

    /**
     *  Upload new photo file to user profile
     */
    private void uploadHeadPhoto(){
        showLoading();
        // Upload photo file to cloud storage
        final User.Role userRole = CurrentUser.getUserRole();
        final String userId = CurrentUser.getUserId();
        StorageReference photoRef = FirebaseStorage.getInstance().
                getReference(userRole.toString().toLowerCase()).
                child(userId).
                child(Constant.HEAD_PHOTO_URI);
        photoRef.putFile(photoLocalUri).
                addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(Constant_Debug.TAG_STORE, Constant_Debug.STORE_DASHBOARD_HEAD_PORTRAIT_UPLOADED);
                    // Save photo info into database if photo file uploaded successfully
                    // Get photo download uri from task
                    final Uri photoUri = task.getResult().getDownloadUrl();
                    // Find photo reference in database
                    DatabaseReference photoRef = FirebaseDatabase.getInstance().
                            getReference(Constant.CUSTOMER).
                            child(userId).
                            child(Constant.HEAD_PHOTO_URI);
                    // Save uri into reference
                    photoRef.setValue(photoUri.toString()).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(Constant_Debug.TAG_STORE, Constant_Debug.STORE_DASHBOARD_HEAD_PHOTO_DATA_INSERTED);
                                CurrentUser.setUserHeadPhotoUri(photoUri.toString());
                                setupHeadPhoto();
                                Toast.makeText(getApplicationContext(), getString(R.string.add_head_photo_success), Toast.LENGTH_SHORT).show();
                                showContents();
                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), getString(R.string.error_info) + message, Toast.LENGTH_SHORT).show();
                                showContents();
                            }
                        }
                    });
                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), getString(R.string.error_info) + message, Toast.LENGTH_SHORT).show();
                    showContents();
                }
            }
        });
    }

    /**
     * Attach store and chef list to presentation
     */
    private void attachChefList() {
        Query filterQuery = mChefRef.orderByChild(Constant.STORE_STATUS).equalTo(true);
        FirebaseRecyclerOptions<Chef> options =
                new FirebaseRecyclerOptions.Builder<Chef>()
                        .setQuery(filterQuery, Chef.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter storeListViewAdapter = new StoreListViewAdapter(options, this);
        chefList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        chefList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        chefList.setLayoutManager(mLayoutManager);
        chefList.setItemAnimator(new DefaultItemAnimator());
        chefList.setAdapter(storeListViewAdapter);
        Log.d(Constant_Debug.TAG_STORE, Constant_Debug.STORE_DASHBOARD_STORE_ATTACHED);
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

}
