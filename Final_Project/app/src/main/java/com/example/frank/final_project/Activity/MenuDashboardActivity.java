package com.example.frank.final_project.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.frank.final_project.Adapter.MenuViewAdapter;
import com.example.frank.final_project.Constant.CircleTransform;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Constant.Constant_Debug;
import com.example.frank.final_project.Constant.Utils;
import com.example.frank.final_project.Fragment.ConfirmDialogFragment;
import com.example.frank.final_project.Model.Cake;
import com.example.frank.final_project.Model.Contact;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.Store;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  Show store menu to user.
 *  Store owner chef user is allowed to edit cakes.
 *  Customer is allowed to find store location on map
 *  if store is in retail business style with valid address
 */
public class MenuDashboardActivity extends AppCompatActivity {

    @BindView(R.id.menu_dashboard_toolbar)
    Toolbar toolbar;

    @BindView(R.id.menu_dashboard_headphoto_Iv)
    ImageView headPhoto;

    @BindView(R.id.menu_dashboard_page_menu_Rv)
    RecyclerView menuList;

    @BindView(R.id.menu_dashboard_page_Pb)
    ProgressBar progressbarView;

    @BindView(R.id.menu_dashboard_page_float_Btn)
    FloatingActionButton floatBtn;

    private ActionBar actionBar;
    private String mUserId;
    private User.Role userRole;
    private Uri photoLocalUri;
    private String photoName;
    private DatabaseReference mMenuRef;
    private String mStoreAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_dashboard);
        ButterKnife.bind(this);
        Log.d(Constant_Debug.TAG_MENU, Constant_Debug.MENU_CREATED);

        // Setup tool bar button
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);

        // Show loading progress bar
        showLoading();
        // Load current user Id
        mUserId = CurrentUser.getUserId();
        // Load user role
        userRole = CurrentUser.getUserRole();
        // Show customer demonstrations
        if(userRole == User.Role.CUSTOMER){
            displayWidgetToCustomer();
            // Current user is a customer
            mMenuRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(CurrentUser.getOppositeId()).child(Constant.STORE).child(Constant.MENU);
        }else{
            // Current user is a chef
            mMenuRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(mUserId).child(Constant.STORE).child(Constant.MENU);
        }
        // Show head photo
        setupHeadPhoto();
        // Bind store menu list
        attachMenu();
        // Set store name as tittle
        this.setTitle((CurrentUser.getChef().getStore() == null) ?
                CurrentUser.getStore().getName() : CurrentUser.getChef().getStore().getName());
        // Start message notification service if it is not activated
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
        Log.d(Constant_Debug.TAG_MENU, Constant_Debug.MENU_DESTROYED);
    }

    /**
     *  Setup head photo from database resource
     */
    private void setupHeadPhoto(){
        String uri = (userRole == User.Role.CUSTOMER) ?
                CurrentUser.getOppositePhotoUri() : CurrentUser.getUserHeadPhotoUri();
        Picasso.with(this).
                load(uri).
                placeholder(R.drawable.chef_default_headphoto).
                error(R.drawable.chef_default_headphoto).
                transform(new CircleTransform()).
                into(headPhoto);
    }

    /**
     * Display customized widgets to customer user
     */
    private void displayWidgetToCustomer(){
        // Set back button function
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Change button for customer user
        floatBtn.setImageResource(R.drawable.message_btn);
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
        // Show map button if store address is not null
        mStoreAddress = CurrentUser.getChef().getStore().getAddress();
        // Show customer menu items
        if(userRole == User.Role.CUSTOMER){
            // Hide message in menu to customer
            menu.findItem(R.id.menu_message).setVisible(false);
            // Hide photo configuration in menu to customer
            menu.findItem(R.id.menu_headphoto).setVisible(false);
            // Hide store switch in menu to customer
            menu.findItem(R.id.menu_store_switch).setVisible(false);
            // Show address map button if store has a valid address
            if(mStoreAddress == null){
                menu.findItem(R.id.menu_map).setVisible(false);
            }
            // Hide sign out button in menu to customer
            menu.findItem(R.id.menu_sign_out).setVisible(false);
        }
        // Show chef menu items
        else{
            menu.findItem(R.id.menu_map).setVisible(false);
            menu.findItem(R.id.menu_store_switch).setTitle(
                    (CurrentUser.getChef().getStoreStatus()) ?
                            getString(R.string.store_close) : getString(R.string.store_open)
            );
        }
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
                showLoading();
                Intent contactIntent = new Intent(this, ContactActivity.class);
                startActivity(contactIntent);
                return true;
            case R.id.menu_map:
                showLoading();
                Intent mapIntent = new Intent(this, MapsActivity.class);
                startActivity(mapIntent);
                return true;
            case R.id.menu_headphoto:
                showPictureWarningDialog();
                return true;
            case R.id.menu_store_switch:
                switchStoreStatus();
                item.setTitle(
                        (CurrentUser.getChef().getStoreStatus()) ?
                                getString(R.string.store_close) : getString(R.string.store_open)
                );
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
     *  Switch controlling the status of store
     */
    private void switchStoreStatus() {
        boolean currentStatus = CurrentUser.getChef().getStoreStatus();
        CurrentUser.getChef().setStoreStatus(
                (currentStatus) ? false : true
        );
        FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(mUserId).child(Constant.STORE_STATUS).setValue(
                (currentStatus) ? false : true
        );
        Log.d(Constant_Debug.TAG_MENU, Constant_Debug.MENU_DASHBOARD_STORE_STATUS_CHANGED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constant_Debug.TAG_MENU, Constant_Debug.MENU_RESUME);
        showContents();
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
        StorageReference photoRef = FirebaseStorage.getInstance().
                getReference(userRole.toString().toLowerCase()).
                child(mUserId).
                child(Constant.HEAD_PHOTO_URI);
        photoRef.putFile(photoLocalUri).
                addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(Constant_Debug.TAG_MENU, Constant_Debug.MENU_DASHBOARD_HEAD_PHOTO_UPLOADED);
                            // Save photo info into database if photo file uploaded successfully
                            // Get photo download uri from task
                            final Uri photoUri = task.getResult().getDownloadUrl();
                            // Find photo reference in database
                            DatabaseReference photoRef = FirebaseDatabase.getInstance().
                                    getReference(Constant.CHEF).
                                    child(mUserId).
                                    child(Constant.HEAD_PHOTO_URI);
                            // Save uri into reference
                            photoRef.setValue(photoUri.toString()).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Log.d(Constant_Debug.TAG_MENU, Constant_Debug.MENU_DASHBOARD_HEAD_PHOTO_DATA_INSERTED);
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
     * Float button on click function
     */
    @OnClick(R.id.menu_dashboard_page_float_Btn)
    public void onClickFloatBtn(){
        // Provide chat service to customer user
        if(userRole == User.Role.CUSTOMER){
            showLoading();
            CurrentUser.setChatTargetId(CurrentUser.getOppositeId());
            if(CurrentUser.getOppositeName() != null){
                CurrentUser.setChatTargetName(CurrentUser.getOppositeName());
            }
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
        customerContactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean contactExhausts = false;
                for(DataSnapshot contact: dataSnapshot.getChildren()){
                    if(contact.child(Constant.OPPOSITE_ID).getValue(String.class).equals(CurrentUser.getOppositeId())){
                        contactExhausts = true;
                    }
                }
                // Contact is not existed
                if(!contactExhausts){
                    // Get contact key
                    final String contactKey = customerContactRef.push().getKey();
                    final Contact contact = new Contact();
                    // Write chef contact to customer
                    contact.setOppositeId(CurrentUser.getOppositeId());
                    if(CurrentUser.getOppositeName() != null){
                        contact.setOppositeName(CurrentUser.getOppositeName());
                    }
                    customerContactRef.child(contactKey).setValue(contact);
                    Log.d(Constant_Debug.TAG_MENU, Constant_Debug.MENU_DASHBOARD_NEW_CONTACT_CREATED);
                    // Write customer contact to chef
                    FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).
                            child(mUserId).
                            child(Constant.NAME).
                            addListenerForSingleValueEvent(new ValueEventListener() {
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
                            showContents();
                            String errorMessage = databaseError.getMessage();
                            Toast.makeText(getApplicationContext(), getString(R.string.error_info) + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    // Contact is existed
                    startContactActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String errorMessage = databaseError.getMessage();
                Toast.makeText(getApplicationContext(), getString(R.string.error_info) + errorMessage, Toast.LENGTH_LONG).show();
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
        Log.d(Constant_Debug.TAG_MENU, Constant_Debug.MENU_DASHBOARD_MENU_ATTACHED);
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
