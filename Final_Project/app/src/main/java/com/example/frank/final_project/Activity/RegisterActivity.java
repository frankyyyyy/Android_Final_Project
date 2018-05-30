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
    ProgressBar progressBar;

    @BindView(R.id.register_page_register_form)
    LinearLayout registerForm;

    @BindView(R.id.register_page_email_Et)
    EditText email;

    @BindView(R.id.register_page_password_Et)
    EditText password;

    @BindView(R.id.register_page_password_confirm_Et)
    EditText confirmPassword;

    @BindView(R.id.register_page_name_Et)
    EditText name;

    @BindView(R.id.register_page_phoneNum_Et)
    EditText phoneNum;

    @BindView(R.id.register_page_role_Rg)
    RadioGroup roleSelection;

    @BindView(R.id.register_page_chef_layout)
    LinearLayout chefLayout;

    @BindView(R.id.register_page_store_name_Et)
    EditText storeName;

    @BindView(R.id.register_page_businessStyle_Rg)
    RadioGroup businessStyleSelection;

    @BindView(R.id.register_page_retailAddress_Et)
    EditText retailAddress;

    @BindView(R.id.register_page_certificate_filename_Tv)
    TextView certificateName;

    @BindView(R.id.register_page_postalAddress_Et)
    EditText postalAddress;

    @BindView(R.id.register_page_agreement_Cb)
    CheckBox agreement;

    private String mUserId;
    private String mEmail;
    private String mPassword;
    private String mConfirmPassword;
    private String mName;
    private String mPhoneNum;
    private int mRoleSelectionId;
    private String mStoreName;
    private String mRetailAddress;
    private String mPostalAddress;
    private Uri mCertificateUri;

    private Store mNewStore;

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
                    chefLayout.setVisibility(View.VISIBLE);
                    postalAddress.setVisibility(View.GONE);
                    agreement.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.register_page_customer_Rb:
                if (isChanged) {
                    chefLayout.setVisibility(View.GONE);
                    postalAddress.setVisibility(View.VISIBLE);
                    agreement.setVisibility(View.VISIBLE);
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
            // Individual business chose
            case R.id.register_page_individual_Rb:
                if (isChanged) {
                    retailAddress.setVisibility(View.GONE);
                }
                break;
            // Retail business chose
            case R.id.register_page_retail_Rb:
                if (isChanged) {
                    retailAddress.setVisibility(View.VISIBLE);
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
        mEmail = email.getText().toString();
        mPassword = password.getText().toString();
        mConfirmPassword = confirmPassword.getText().toString();
        mName = name.getText().toString();
        mPhoneNum = phoneNum.getText().toString();
        mRoleSelectionId = roleSelection.getCheckedRadioButtonId();
        mStoreName = storeName.getText().toString();
        mRetailAddress = retailAddress.getText().toString();
        mPostalAddress = postalAddress.getText().toString();
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
                    mCertificateUri = data.getData();
                    certificateName.setText(mCertificateUri.getLastPathSegment());
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
        // Check mEmail input validity
        if (!Utils.emailInputIsLegal(mEmail)) {
            email.setError(getString(R.string.login_page_email_error));
        }
        // Check mPassword input validity
        else if (!Utils.passwordInputIsLegal(mPassword)) {
            password.setError(getString(R.string.login_page_password_error));
        }
        // Check if mPassword is confirmed
        else if(!Utils.confirmPassword(mPassword, mConfirmPassword)){
            confirmPassword.setError(getString(R.string.register_page_password_confirm_error));
        }
        // Check mName input validity
        else if ((!mName.isEmpty()) && !Utils.nameInputIsLegal(mName)) {
            name.setError(getString(R.string.register_page_name_error));
        }
        // Check phone input validity
        else if ((!mPhoneNum.isEmpty()) && !Utils.phoneNumInputIsLegal(mPhoneNum)) {
            phoneNum.setError(getString(R.string.register_page_phoneNum_error));
        }
        // Check store name validity
        else if(mStoreName.isEmpty() && (mRoleSelectionId == R.id.register_page_chef_Rb)){
            storeName.setError(getString(R.string.register_page_store_name_error));
        }
        // Check store address validity
        else if((mRoleSelectionId == R.id.register_page_chef_Rb) && mRetailAddress.isEmpty() && (businessStyleSelection.getCheckedRadioButtonId() == R.id.register_page_retail_Rb)){
            retailAddress.setError(getString(R.string.register_page_retail_address_error));
        }
        // Check agreement checkbox
        else if (!agreement.isChecked()) {
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
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Register successful, show message and go on creating user data snapshot
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_page_verify_account_creation_success), Toast.LENGTH_LONG).show();
                    mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        registerForm.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     *  Show contents
     */
    private void showContents(){
        registerForm.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Create a snapshot in database to store user data
     */
    private void createUserData(){
        switch (mRoleSelectionId) {
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
        chef.setId(mUserId);
        if (mName != null) chef.setName(mName);
        if (mPhoneNum != null) chef.setPhone(mPhoneNum);
        DatabaseReference chefRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(mUserId);
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
    }

    /**
     * Create a new customer at snapshot
     */
    private void createCustomer() {
        Customer customer = new Customer();
        customer.setId(mUserId);
        if (mName != null) customer.setName(mName);
        if (mPhoneNum != null) customer.setPhone(mPhoneNum);
        if (mPostalAddress != null) customer.setPostalAddress(mPostalAddress);
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(mUserId);
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
        mNewStore = new Store();
        mNewStore.setName(mStoreName);
        if (businessStyleSelection.getCheckedRadioButtonId() == R.id.register_page_retail_Rb
                && mRetailAddress != null) mNewStore.setAddress(mRetailAddress);
        DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(mUserId).child(Constant.STORE);
        storeRef.setValue(mNewStore).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        StorageReference chefRef = FirebaseStorage.getInstance().getReference(Constant.CHEF).child(mUserId).child(Constant.CERTIFICATE);
        chefRef.putFile(mCertificateUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
        switch (mRoleSelectionId) {
            // Go to store dashboard
            case R.id.register_page_chef_Rb:
                loadNewUserInfo(User.Role.CHEF);
                Intent storeIntent = new Intent(getApplicationContext(), MenuDashboardActivity.class);
                startActivity(storeIntent);
                finish();
                // Show welcome message
                Toast.makeText(getApplicationContext(), getString(R.string.register_page_register_success), Toast.LENGTH_LONG).show();
                break;
            // Go to customer dashboard
            case R.id.register_page_customer_Rb:
                loadNewUserInfo(User.Role.CUSTOMER);
                Intent customerIntent = new Intent(getApplicationContext(), StoreDashboardActivity.class);
                startActivity(customerIntent);
                finish();
                // Show welcome message
                Toast.makeText(getApplicationContext(), getString(R.string.register_page_register_success), Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    /**
     * Load user info as
     * @param role
     */
    private void loadNewUserInfo(User.Role role){
        CurrentUser.setUserId(mUserId);
        CurrentUser.setUserName(mName);
        CurrentUser.setUserRole(role);
        CurrentUser.setUserEmail(mEmail);
        CurrentUser.setUserPassword(mPassword);
        if(role == User.Role.CHEF){
            CurrentUser.setStore(mNewStore);
        }
    }
}
