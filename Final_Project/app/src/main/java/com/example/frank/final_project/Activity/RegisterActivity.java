package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Constant.Utils;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.Customer;
import com.example.frank.final_project.Model.Store;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_page_register_Pb)
    ProgressBar mProgressBar;

    @BindView(R.id.register_page_register_form)
    LinearLayout mRegisterForm;

    @BindView(R.id.register_page_email_Et)
    EditText mEmail;

    @BindView(R.id.register_page_password_Et)
    EditText mPassword;

    @BindView(R.id.register_page_password_confirm_Et)
    EditText mConfirmPassword;

    @BindView(R.id.register_page_name_Et)
    EditText mName;

    @BindView(R.id.register_page_phoneNum_Et)
    EditText mPhoneNum;

    @BindView(R.id.register_page_role_Rg)
    RadioGroup mRoleSelection;

    @BindView(R.id.register_page_chef_layout)
    LinearLayout mChefLayout;

    @BindView(R.id.register_page_store_name_Et)
    EditText mStoreName;

    @BindView(R.id.register_page_businessStyle_Rg)
    RadioGroup businessStyleSelection;

    @BindView(R.id.register_page_retailAddress_Et)
    EditText mRetailAddress;

    @BindView(R.id.register_page_certificate_filename_Tv)
    TextView mCertificateName;

    @BindView(R.id.register_page_postalAddress_Et)
    EditText mPostalAddress;

    @BindView(R.id.register_page_agreement_Cb)
    CheckBox mAgreement;

    private String userId;
    private String email;
    private String password;
    private String confirmPassword;
    private String name;
    private String phoneNum;
    private int roleSelectionId;
    private String storeName;
    private String retailAddress;
    private String postalAddress;

    private Boolean createVerifyAccountSuccessful = false;
    private Boolean createUserSuccessful = false;
    private Boolean connectionSuccessful = false;
    private Boolean uploadCertificateSuccessful = false;

    private String[] chefIds;
    private Uri certificateUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    /**
     * Role selection radio button events
     *
     * @param view
     * @param isChanged
     */
    @OnCheckedChanged({R.id.register_page_chef_Rb, R.id.register_page_customer_Rb})
    public void roleOnCheckedChangeListener(CompoundButton view, boolean isChanged) {
        switch (view.getId()) {
            case R.id.register_page_chef_Rb:
                if (isChanged) {
                    mChefLayout.setVisibility(View.VISIBLE);
                    mPostalAddress.setVisibility(View.GONE);
                    mAgreement.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.register_page_customer_Rb:
                if (isChanged) {
                    mChefLayout.setVisibility(View.GONE);
                    mPostalAddress.setVisibility(View.VISIBLE);
                    mAgreement.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Business style selection radio button events
     *
     * @param view
     * @param isChanged
     */
    @OnCheckedChanged({R.id.register_page_individual_Rb, R.id.register_page_retail_Rb})
    public void businessStyleOnCheckedChangeListener(CompoundButton view, boolean isChanged) {
        switch (view.getId()) {
            case R.id.register_page_individual_Rb:
                if (isChanged) {
                    mRetailAddress.setVisibility(View.GONE);
                }
                break;
            case R.id.register_page_retail_Rb:
                if (isChanged) {
                    mRetailAddress.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Read inputs from front end
     */
    private void readInputs() {
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        confirmPassword = mConfirmPassword.getText().toString();
        name = mName.getText().toString();
        phoneNum = mPhoneNum.getText().toString();
        roleSelectionId = mRoleSelection.getCheckedRadioButtonId();
        storeName = mStoreName.getText().toString();
        retailAddress = mRetailAddress.getText().toString();
        postalAddress = mPostalAddress.getText().toString();
    }

    /**
     * Assign functions to navigation buttons
     * @param view
     */
    @OnClick({R.id.register_page_register_Btn, R.id.register_page_cancel_Btn, R.id.register_page_add_certificate_Btn})
    public void onClick(View view) {
        switch (view.getId()) {
            // On click register
            case R.id.register_page_register_Btn:
                // Read inputs from front end
                readInputs();
                // Check inputs validation
                if (allInputsAreValid()) {
                    // Start creation of a new account
                    createVerifyAccount();
                }
                break;
            // On click cancel
            case R.id.register_page_cancel_Btn:
                // Go back to main page
                finish();
                break;
            // On click certificate upload
            case R.id.register_page_add_certificate_Btn:
                openSystemFileSelection();
                break;
            default:
                break;
        }
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
                    certificateUri = data.getData();
                    mCertificateName.setText(certificateUri.getLastPathSegment());
                }
                break;
        }
    }

    /**
     * Check all input validity
     *
     * @return areValid
     */
    private boolean allInputsAreValid() {
        // Check email input validity
        if (!Utils.emailInputIsLegal(email)) {
            mEmail.setError(getString(R.string.login_page_email_error));
        }
        // Check password input validity
        else if (!Utils.passwordInputIsLegal(password)) {
            mPassword.setError(getString(R.string.login_page_password_error));
        }
        // Check if password is confirmed
        else if(!Utils.confirmPassword(password, confirmPassword)){
            mConfirmPassword.setError(getString(R.string.register_page_password_confirm_error));
        }
        // Check name input validity
        else if ((!name.isEmpty()) && !Utils.nameInputIsLegal(name)) {
            mName.setError(getString(R.string.register_page_name_error));
        }
        // Check phone input validity
        else if ((!phoneNum.isEmpty()) && !Utils.phoneNumInputIsLegal(phoneNum)) {
            mPhoneNum.setError(getString(R.string.register_page_phoneNum_error));
        }
        else if(storeName.isEmpty()){
            mStoreName.setError(getString(R.string.register_page_store_name_error));
        }
        // Check agreement checkbox
        else if (!mAgreement.isChecked()) {
            Toast.makeText(this, getString(R.string.register_page_agreement_error), Toast.LENGTH_LONG).show();
        } else {
            return true;
        }
        return false;
    }

    /**
     * Create a new verify purpose account in database
     */
    private void createVerifyAccount(){
        // Show progress bar
        showLoading();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Register successful, show message and go on creating user data snapshot
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_page_verify_account_creation_success), Toast.LENGTH_LONG).show();
                    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    createUserData();
                }
                // Register failed, show error message
                else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_page_account_creation_fail) + errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        mRegisterForm.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     *  Show contents
     */
    private void showContents(){
        mRegisterForm.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Create a snapshot in database to store user data
     */
    private void createUserData(){
        switch (roleSelectionId) {
            // Register a new chef
            case R.id.register_page_chef_Rb:
                createChef();
                break;
            // Register a new customer
            case R.id.register_page_customer_Rb:
                createCustomer();
                break;
            default:
                break;
        }
    }

    /**
     * Create a new chef at snapshot
     */
    private void createChef() {
        Chef chef = new Chef();
        chef.setId(userId);
        if (name != null) chef.setName(name);
        if (phoneNum != null) chef.setPhone(phoneNum);
        DatabaseReference chefRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId);
        chefRef.setValue(chef).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Input chef data successful, show message and go on input store data
                if(task.isSuccessful()){
                    createStore();
                }
                // Input chef data failed, show error message
                else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_page_account_creation_fail) + errorMessage, Toast.LENGTH_LONG).show();
                    showContents();
                }
            }
        });
        createStore();
    }

    /**
     * Create a new customer at snapshot
     */
    private void createCustomer() {
        Customer customer = new Customer();
        customer.setId(userId);
        if (name != null) customer.setName(name);
        if (phoneNum != null) customer.setPhone(phoneNum);
        if (postalAddress != null) customer.setPostalAddress(postalAddress);
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(userId);
        customerRef.setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Input customer data successful, show message and go to dashboard
                if (task.isSuccessful()) {
                    openDashboard();
                }
                // Input customer data failed, show error message
                else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_page_account_creation_fail) + errorMessage, Toast.LENGTH_LONG).show();
                    showContents();
                }
            }
        });
    }

    /**
     * Create a new store at snapshot
     */
    private void createStore() {
        Store store = new Store();
        store.setName(storeName);
        if (businessStyleSelection.getCheckedRadioButtonId() == R.id.register_page_retail_Rb
                && retailAddress != null) store.setAddress(retailAddress);
        DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.STORE);
        storeRef.setValue(store).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Input store data successful, show message and upload file
                if (task.isSuccessful()) {
                    uploadCertificate();
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_page_account_snapshot_creation_success), Toast.LENGTH_LONG).show();
                }
                // Input store data failed, show error message
                else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_page_account_creation_fail) + errorMessage, Toast.LENGTH_LONG).show();
                    showContents();
                }
            }
        });
    }

    /**
     * Upload certificate to database storage
     */
    private void uploadCertificate(){
        StorageReference chefRef = FirebaseStorage.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.CERTIFICATE);
        chefRef.putFile(certificateUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                // Upload certificate successful, open dashboard
                if (task.isSuccessful()) {
                    openDashboard();
                }
                // Upload certificate failed, show error message
                else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_page_account_creation_fail) + errorMessage, Toast.LENGTH_LONG).show();
                    showContents();
                }
            }
        });
    }

    /**
     *  Open dashboard page due to user Role
     */
    private void openDashboard(){
        switch (roleSelectionId) {
            // Go to store dashboard
            case R.id.register_page_chef_Rb:
                Intent storeIntent = new Intent(getApplicationContext(), MenuDashboard.class);
                CurrentUser.setUserId(userId);
                CurrentUser.setUserRole(User.Role.CHEF);
                CurrentUser.setUserEmail(email);
                CurrentUser.setUserPassword(password);
                startActivity(storeIntent);
                finish();
                // Show welcome message
                Toast.makeText(getApplicationContext(), getString(R.string.register_page_register_success), Toast.LENGTH_LONG).show();
                break;
            // Go to customer dashboard
            case R.id.register_page_customer_Rb:
                Intent customerIntent = new Intent(getApplicationContext(), StoreDashboard.class);
                CurrentUser.setUserId(userId);
                CurrentUser.setUserRole(User.Role.CUSTOMER);
                CurrentUser.setUserEmail(email);
                CurrentUser.setUserPassword(password);
                startActivity(customerIntent);
                finish();
                // Show welcome message
                Toast.makeText(getApplicationContext(), getString(R.string.register_page_register_success), Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}
