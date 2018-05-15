package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.frank.final_project.Model.Customer;
import com.example.frank.final_project.Model.Store;
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

    @BindView(R.id.register_page_register_progress)
    ProgressBar mProgressBar;

    @BindView(R.id.register_page_register_form)
    LinearLayout mRegisterForm;

    @BindView(R.id.register_page_email_Et)
    EditText mEmail;

    @BindView(R.id.register_page_password_Et)
    EditText mPassword;

    @BindView(R.id.register_page_name_Et)
    EditText mName;

    @BindView(R.id.register_page_phoneNum_Et)
    EditText mPhoneNum;

    @BindView(R.id.register_page_role_Rg)
    RadioGroup roleSelection;

    @BindView(R.id.register_page_chef_layout)
    LinearLayout mChefLayout;

    @BindView(R.id.register_page_businessStyle_Rg)
    RadioGroup businessStyleSelection;

    @BindView(R.id.register_page_retailAddress_Et)
    EditText mRetailAddress;

    @BindView(R.id.register_page_certificate_filename)
    TextView mCertificateName;

    @BindView(R.id.register_page_postalAddress_Et)
    EditText mPostalAddress;

    @BindView(R.id.register_page_agreement)
    CheckBox mAgreement;

    private String userId;
    private String email;
    private String password;
    private String name;
    private String phoneNum;
    private String retailAddress;
    private String postalAddress;

    private Boolean createVerifyAccountSuccessful = false;
    private Boolean createUserSuccessful = false;
    private Boolean connectionSuccessful = false;
    private Boolean uploadCertificateSuccessful = false;

    private Uri certificateUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    /**
     *  Role selection radio button events
     * @param view
     * @param isChanged
     */
    @OnCheckedChanged({R.id.register_page_chef_Rb, R.id.register_page_customer_Rb})
    public void roleOnCheckedChangeListener(CompoundButton view, boolean isChanged){
        switch (view.getId()){
            case R.id.register_page_chef_Rb:
                if (isChanged){
                    mChefLayout.setVisibility(View.VISIBLE);
                    mPostalAddress.setVisibility(View.GONE);
                    mAgreement.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.register_page_customer_Rb:
                if (isChanged){
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
     *  Business style selection radio button events
     * @param view
     * @param isChanged
     */
    @OnCheckedChanged({R.id.register_page_individual_Rb, R.id.register_page_retail_Rb})
    public void businessStyleOnCheckedChangeListener(CompoundButton view, boolean isChanged){
        switch (view.getId()){
            case R.id.register_page_individual_Rb:
                if (isChanged){
                    mRetailAddress.setVisibility(View.GONE);
                }
                break;
            case R.id.register_page_retail_Rb:
                if (isChanged){
                    mRetailAddress.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    /**
     *  Read inputs from front end
     */
    private void readInputs(){
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        name = mName.getText().toString();
        phoneNum = mPhoneNum.getText().toString();
        retailAddress = mRetailAddress.getText().toString();
        postalAddress = mPostalAddress.getText().toString();
    }

    /**
     * Assign functions to navigation buttons
     * @param view
     */
    @OnClick({R.id.register_page_register_Btn, R.id.register_page_cancel_Btn, R.id.register_page_add_certificate_Btn})
    public void onClick(View view){
        switch (view.getId()){
            // On click register
            case R.id.register_page_register_Btn:
                // Read inputs from front end
                readInputs();
                // Check inputs validation
                if(allInputsAreValid()){
                    new registerAttempt().execute();
                }
                break;
            // On click cancel
            case R.id.register_page_cancel_Btn:
                // Go back to main page
                Intent mainPageIntent = new Intent(this, MainActivity.class);
                startActivity(mainPageIntent);
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
     *  Browse file folder and select the certificate file
     */
    private void openSystemFileSelection(){
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(fileIntent, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1234:
                if (resultCode == RESULT_OK) {
                    certificateUri = data.getData();
                    mCertificateName.setText(certificateUri.getLastPathSegment());
                }
                break;
        }
    }

    /**
     *  Check all input validity
     * @return areValid
     */
    private boolean allInputsAreValid(){
        // Check email input validity
        if(!Utils.emailInputIsLegal(email)){
            mEmail.setError(getString(R.string.login_page_email_error));
        }
        // Check password input validity
        else if(!Utils.passwordInputIsLegal(password)){
            mPassword.setError(getString(R.string.login_page_password_error));
        }
        // Check name input validity
        else if((name != null) && !Utils.nameInputIsLegal(name)){
            mName.setError(getString(R.string.register_page_name_error));
        }
        // Check phone input validity
        else if((phoneNum != null) && !Utils.phoneNumInputIsLegal(phoneNum)){
            mPhoneNum.setError(getString(R.string.register_page_phoneNum_error));
        }
        // Check agreement checkbox
        else if(!mAgreement.isChecked()){
            mAgreement.setError(getString(R.string.register_page_agreement_error));
        }
        else{
            return true;
        }
        return false;
    }

    /**
     * Load email and password and spend 4 seconds for verification.
     */
    private class registerAttempt extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mRegisterForm.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Create a brand new user account pack in database.
            createNewUserAccount();
            return null;
        }

        /**
         *  Create a brand new user account pack in database
         */
        private void createNewUserAccount(){
            try {
                // Loading for 4 seconds.
                Thread.sleep(Constant.REGISTER_LOADING_TIME);
                // Create a new verify purpose account in database
                try{
                    createVerifyAccount();
                }catch (Exception e){
                    // Connection issue happens.
                    connectionSuccessful = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(createVerifyAccountSuccessful){
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Create a corresponding snapshot to hold data of user in database
                try {
                    // Loading for 4 seconds.
                    Thread.sleep(Constant.REGISTER_LOADING_TIME);
                    // Create a new verify purpose account in database
                    try{
                        createUserSnapshot();
                    } catch (Exception e){
                        // Connection issue happens.
                        connectionSuccessful = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Upload certificate to database storage
                try {
                    // Loading for 4 seconds.
                    Thread.sleep(Constant.REGISTER_LOADING_TIME);
                    // Create a new verify purpose account in database
                    try{
                        uploadCertificate();
                    } catch (Exception e){
                        // Connection issue happens.
                        connectionSuccessful = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressBar.setVisibility(View.GONE);
            mRegisterForm.setVisibility(View.VISIBLE);
            judgeOnRegisterResult();
        }

        /**
         *  Open dashboard if register is successful, otherwise show error message.
         */
        private void judgeOnRegisterResult() {
            // Connection to Firebase in fail. Demonstrate warning message.
            if(!connectionSuccessful){
                Toast.makeText(getApplicationContext(), getString(R.string.connection_issue_warning), Toast.LENGTH_LONG).show();
            }
            // If account creation failed, then show warning message.
            else if(!createVerifyAccountSuccessful){
                clearData();
                Toast.makeText(getApplicationContext(), getString(R.string.register_page_warning_register_fail), Toast.LENGTH_LONG).show();
            }
            // If corresponding data snapshot creation failed, restore the verification account and show error message.
            else if(!createUserSuccessful){
                clearData();
                Toast.makeText(getApplicationContext(), getString(R.string.register_page_warning_register_fail), Toast.LENGTH_LONG).show();
            }
            // If certificate upload failed, show error message.
            else if(!uploadCertificateSuccessful){
                clearData();
                Toast.makeText(getApplicationContext(), getString(R.string.register_page_warning_register_fail), Toast.LENGTH_LONG).show();
            }
            // If account created successfully, then start dashboard.
            else{
                Intent DashboardIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(DashboardIntent);
                finish();
            }
        }

        public void clearData(){
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                FirebaseAuth.getInstance().getCurrentUser().delete();
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId).removeValue();
                FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(userId).removeValue();
                FirebaseStorage.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.CERTIFICATE).delete();
            }
        }
    }

    /**
     *  Create a new verify purpose account in database
     */
    private void createVerifyAccount(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Connection successful, set values in return.
                connectionSuccessful = true;
                // Register successful, set values in return.
                if(task.isSuccessful()){
                    // Sign in automatically
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            createVerifyAccountSuccessful = true;
                        }
                    });
                }
                // Register failed, set values in return.
                else{
                    createVerifyAccountSuccessful = false;
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
                // Connection successful, set values in return.
                connectionSuccessful = true;
                // Upload certificate successful, set values in return.
                if(task.isSuccessful()){
                    uploadCertificateSuccessful = true;
                }
                // Upload certificate failed, set values in return.
                else{
                    uploadCertificateSuccessful = false;
                }
            }
        });
    }

    /**
     *  Create a snapshot in database to store user data
     */
    private void createUserSnapshot(){
        switch (roleSelection.getCheckedRadioButtonId()){
            case R.id.register_page_chef_Rb:
                createChef();
                break;
            case R.id.register_page_customer_Rb:
                createCustomer();
                break;
            default:
                break;
        }
    }

    /**
     *  Create a new chef at snapshot
     */
    private void createChef(){
        Chef chef = new Chef();
        if(name != null) chef.setName(name);
        if(phoneNum != null) chef.setPhone(phoneNum);
        try{
            DatabaseReference chefRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId);
            chefRef.setValue(chef);
            createStore();
        }catch (Exception e){
            // Connection issue happens.
            connectionSuccessful = false;
        }
    }

    /**
     *  Create a new customer at snapshot
     */
    private void createCustomer(){
        Customer customer = new Customer();
        if(name != null) customer.setName(name);
        if(phoneNum != null) customer.setPhone(phoneNum);
        if(postalAddress != null) customer.setPostalAddress(postalAddress);
        try{
            DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(userId);
            customerRef.setValue(customer);
            createUserSuccessful = true;
        }catch (Exception e){
            // Connection issue happens.
            connectionSuccessful = false;
        }
    }

    /**
     *  Create a new store at snapshot
     */
    private void createStore(){
        Store store = new Store();
        if(businessStyleSelection.getCheckedRadioButtonId() == R.id.register_page_retail_Rb
                && retailAddress != null) store.setAddress(retailAddress);
        try{
            DatabaseReference storeRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.STORE);
            storeRef.setValue(store);
            createUserSuccessful = true;
        }catch (Exception e){
            // Connection issue happens.
            connectionSuccessful = false;
        }
    }
}
