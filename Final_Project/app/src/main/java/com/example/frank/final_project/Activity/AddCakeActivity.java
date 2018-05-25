package com.example.frank.final_project.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
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
import com.example.frank.final_project.Constant.Utils;
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

public class AddCakeActivity extends AppCompatActivity {

    @BindView(R.id.add_cake_Pv)
    ProgressBar mProgressBarView;

    @BindView(R.id.add_cake_form_view)
    LinearLayout mAddCakeForm;

    @BindView(R.id.add_cake_name_Et)
    EditText mCakeName;

    @BindViews({R.id.ingredients_alcohol, R.id.ingredients_egg, R.id.ingredients_gluten, R.id.ingredients_milk, R.id.ingredients_nuts, R.id.ingredients_sugar})
    List<CheckBox> mIngredientList;

    @BindView(R.id.cake_size_Et)
    EditText mCakeSize;
    @BindView(R.id.cake_size_unit_Spinner)
    Spinner mSizeUnit;

    @BindView(R.id.cake_price_Et)
    EditText mCakePrice;

    @BindView(R.id.add_cake_description_Et)
    EditText mCakeDescription;

    private String cakeName;
    private String cakeIngredients;
    private String cakeSize;
    private double cakePrice;
    private String cakeDescription;
    private String cakePictureUri;

    private String userId;
    private Uri localPictureUri;
    private DatabaseReference menuRef;
    private StorageReference cakePictureRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cake);
        ButterKnife.bind(this);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }






    @OnClick(R.id.add_cake_picture_Iv)
    public void addPicture(){
        openSystemFileSelection();
    }

    /**
     * Browse file folder and select the certificate file
     */
    private void openSystemFileSelection() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
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
                    localPictureUri = data.getData();
                }
                break;
        }
    }









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
     *  Confirm button on click dialog
     */
    private void showCancelWarningDialog() {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        confirmDialogFragment.show(getString(R.string.exit_tittle), "You want to leave with saving.",
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
     *  Cancel button on click dialog
     */
    private void showSubmitWarningDialog() {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        confirmDialogFragment.show(getString(R.string.exit_tittle), "You want to submit.",
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









    private void addNewCakeToDatabase() {
        showLoading();
        // Read data from form
        cakeName = mCakeName.getText().toString();
        for (CheckBox ingredient: mIngredientList) {
            if(ingredient.isChecked()){
                cakeIngredients = (cakeIngredients == null) ?
                        ingredient.getText().toString() :
                        cakeIngredients + " " + ingredient.getText().toString();
            }
        }
        cakePrice = Double.parseDouble(mCakePrice.getText().toString());
        cakeSize = mCakeSize.getText().toString() + mSizeUnit.getSelectedItem().toString();
        cakeDescription = mCakeDescription.getText().toString();
        if(allInputsAreValid()){
            // Create new cake
            Cake newCake = new Cake();
            newCake.setName(cakeName);
            newCake.setIngredients(cakeIngredients);
            newCake.setSize(cakeSize);
            newCake.setPrice(cakePrice);
            newCake.setDescription(cakeDescription);
            // Insert new cake
            menuRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.STORE).child(Constant.MENU);
            final String cakeId = menuRef.push().getKey();
            menuRef.child(cakeId).setValue(newCake).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        // Upload cake picture.
                        cakePictureRef = FirebaseStorage.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.STORE).child(Constant.MENU).child(cakeId);
                        cakePictureRef.putFile(localPictureUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    // Save cake picture link uri in cake database
                                    cakePictureUri = task.getResult().getDownloadUrl().toString();
                                    menuRef.child(cakeId).child(Constant.CAKE_IMAGEURI).setValue(cakePictureUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            finish();
                                            Toast.makeText(getApplicationContext(), "New cake has been added to menu.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else
                                {
                                    showContents();
                                    String message = task.getException().getMessage();
                                    Toast.makeText(AddCakeActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    else
                    {
                        showContents();
                        String message = task.getException().getMessage();
                        Toast.makeText(AddCakeActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Check all input validity
     *
     * @return areValid
     */
    private boolean allInputsAreValid() {
        // Check name input validity
        if (cakeName == null) {
            mCakeName.setError("Need a cake name.");
        }
        // Check size input validity
        else if (mCakeSize.getText().toString() == null) {
            Toast.makeText(AddCakeActivity.this, "Cake size required", Toast.LENGTH_SHORT).show();
        }
        // Check price input validity
        else if((Double)cakePrice == null){
            Toast.makeText(AddCakeActivity.this, "Cake prize required", Toast.LENGTH_SHORT).show();
        }
        // Check picture input validity
        else if (localPictureUri == null) {
            Toast.makeText(AddCakeActivity.this, "Cake picture required", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        mAddCakeForm.setVisibility(View.GONE);
        mProgressBarView.setVisibility(View.VISIBLE);
    }

    /**
     *  Show contents
     */
    private void showContents(){
        mAddCakeForm.setVisibility(View.VISIBLE);
        mProgressBarView.setVisibility(View.GONE);
    }
}
