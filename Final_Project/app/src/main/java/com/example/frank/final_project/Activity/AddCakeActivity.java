package com.example.frank.final_project.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Fragment.ConfirmDialogFragment;
import com.example.frank.final_project.Model.Cake;
import com.example.frank.final_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity page to add a new cake to a specific store.
 * Only open to Chef user.
 */
public class AddCakeActivity extends AppCompatActivity {

    @BindView(R.id.add_cake_Pv)
    ProgressBar progressBarView;

    @BindView(R.id.add_cake_form_view)
    LinearLayout addCakeForm;

    @BindView(R.id.add_cake_name_Et)
    EditText cakeName;

    @BindViews({R.id.ingredients_alcohol, R.id.ingredients_egg, R.id.ingredients_gluten, R.id.ingredients_milk, R.id.ingredients_nuts, R.id.ingredients_sugar})
    List<CheckBox> ingredientList;

    @BindView(R.id.cake_size_Et)
    EditText cakeSize;

    @BindView(R.id.cake_size_unit_Spinner)
    Spinner sizeUnit;

    @BindView(R.id.cake_price_Et)
    EditText cakePrice;

    @BindView(R.id.add_cake_description_Et)
    EditText cakeDescription;

    private String mCakeName;
    private String mCakeIngredients;
    private String mCakeSize;
    private double mCakePrice;
    private String mCakeDescription;
    private String mCakePictureUri;

    private String mUserId;
    private Uri mLocalPictureUri;
    private DatabaseReference mMenuRef;
    private StorageReference mCakePictureRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cake);
        ButterKnife.bind(this);
        // Read current user id
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    /**
     * Choose a picture file from system and upload
     */
    @OnClick(R.id.add_cake_picture_Iv)
    public void addPicture(){
        showPictureWarningDialog();
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
     * Browse file folder and select the cake photo file
     */
    private void openSystemFileSelection() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType(Constant.FILE_TYPE);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(fileIntent, 666);
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
            case 666:
                if (resultCode == RESULT_OK) {
                    mLocalPictureUri = data.getData();
                }
                break;
        }
    }


    /**
     * On click of submit and cancel functions
     * @param view
     */
    @OnClick({R.id.add_cake_submit_Btn, R.id.add_cake_cancel_Btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.add_cake_submit_Btn:
                showSubmitWarningDialog();
                break;
            case R.id.add_cake_cancel_Btn:
                showCancelWarningDialog();
                break;
            default:
                break;
        }
    }

    /**
     *  Cancel button on click dialog
     */
    private void showCancelWarningDialog() {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        confirmDialogFragment.show(getString(R.string.exit_tittle), getString(R.string.cake_add_cancel_warning),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }, getFragmentManager());
    }

    /**
     *  Confirm button on click dialog
     */
    private void showSubmitWarningDialog() {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        confirmDialogFragment.show(getString(R.string.exit_tittle), getString(R.string.cake_add_submit_warning),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addNewCakeToDatabase();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }, getFragmentManager());
    }


    /**
     * Write new cake info into database
     */
    private void addNewCakeToDatabase() {
        showLoading();
        // Read data from form
        readDataFromForm();
        if(allInputsAreValid()){
            // Create new cake
            final Cake newCake = createNewCake();
            // Insert new cake
            mMenuRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).
                    child(mUserId).
                    child(Constant.STORE).
                    child(Constant.MENU);
            final String cakeId = mMenuRef.push().getKey();
            newCake.setId(cakeId);
            mMenuRef.child(cakeId).setValue(newCake).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        // Upload cake picture.
                        mCakePictureRef = FirebaseStorage.getInstance().getReference(Constant.CHEF).
                                child(mUserId).
                                child(Constant.STORE).
                                child(Constant.MENU).
                                child(cakeId).
                                child(Constant.CAKE_PHOTO_ID + Integer.toString(newCake.getPhotoNum()));
                        mCakePictureRef.putFile(mLocalPictureUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    // Save cake picture link uri in cake database
                                    mCakePictureUri = task.getResult().getDownloadUrl().toString();
                                    mMenuRef.child(cakeId).child(Constant.CAKE_IMAGE_URI).setValue(mCakePictureUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            finish();
                                            Toast.makeText(getApplicationContext(), getString(R.string.cake_add_success), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else
                                {
                                    showContents();
                                    String message = task.getException().getMessage();
                                    Toast.makeText(AddCakeActivity.this, getString(R.string.error_info) + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    else
                    {
                        showContents();
                        String message = task.getException().getMessage();
                        Toast.makeText(AddCakeActivity.this, getString(R.string.error_info) + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     *  Read data from form
     */
    private void readDataFromForm(){
        mCakeName = cakeName.getText().toString();
        for (CheckBox ingredient: ingredientList) {
            if(ingredient.isChecked()){
                mCakeIngredients = (mCakeIngredients == null) ?
                        ingredient.getText().toString() :
                        mCakeIngredients + " " + ingredient.getText().toString();
            }
        }
        mCakePrice = Double.parseDouble(cakePrice.getText().toString());
        mCakeSize = cakeSize.getText().toString() + sizeUnit.getSelectedItem().toString();
        mCakeDescription = cakeDescription.getText().toString();
    }

    /**
     *  Create new cake according to input info.
     * @return new cake
     */
    private Cake createNewCake(){
        Cake newCake = new Cake();
        newCake.setName(mCakeName);
        newCake.setPhotoNum(1);
        newCake.setIngredients(mCakeIngredients);
        newCake.setSize(mCakeSize);
        newCake.setPrice(mCakePrice);
        newCake.setDescription(mCakeDescription);
        return newCake;
    }

    /**
     * Check all input validity
     *
     * @return areValid
     */
    private boolean allInputsAreValid() {
        // Check name input validity
        if (mCakeName == null) {
            cakeName.setError(getString(R.string.cake_add_name_error));
        }
        // Check size input validity
        else if (cakeSize.getText().toString() == null) {
            Toast.makeText(AddCakeActivity.this, getString(R.string.cake_add_size_error), Toast.LENGTH_SHORT).show();
        }
        // Check price input validity
        else if((Double) mCakePrice == null){
            Toast.makeText(AddCakeActivity.this, getString(R.string.cake_add_size_error), Toast.LENGTH_SHORT).show();
        }
        // Check picture input validity
        else if (mLocalPictureUri == null) {
            Toast.makeText(AddCakeActivity.this, getString(R.string.cake_add_picture_error), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        addCakeForm.setVisibility(View.GONE);
        progressBarView.setVisibility(View.VISIBLE);
    }

    /**
     *  Show contents
     */
    private void showContents(){
        addCakeForm.setVisibility(View.VISIBLE);
        progressBarView.setVisibility(View.GONE);
    }
}
