package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Constant.Utils;
import com.example.frank.final_project.Model.Chef;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.Customer;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Login activity
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_page_login_progress)
    ProgressBar progressBar;

    @BindView(R.id.login_page_login_form)
    LinearLayout loginForm;

    @BindView(R.id.login_page_email_Et)
    EditText email;

    @BindView(R.id.login_page_password_Et)
    EditText password;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    /**
     * Assign functions to different buttons
     * @param view
     */
    @OnClick({R.id.login_page_sign_in_Btn, R.id.login_page_cancel_Btn})
    public void onClick(View view){
        switch (view.getId()){
            // Open login page
            case R.id.login_page_sign_in_Btn:
                // Read input from front end
                String email = this.email.getText().toString();
                String password = this.password.getText().toString();
                // Check inputs validation
                if(allInputsAreValid(email, password)) {
                    // Attempt to login
                    loginAttempt(email, password);
                }
                break;
            // Go back to main page
            case R.id.login_page_cancel_Btn:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     *  Check all input validity
     * @param email input
     * @param password input
     * @return areValid
     */
    private boolean allInputsAreValid(String email, String password){
        // Check email input validity
        if(!Utils.emailInputIsLegal(email)){
            this.email.setError(getString(R.string.login_page_email_error));
        }
        // Check password input validity
        else if(!Utils.passwordInputIsLegal(password)){
            this.password.setError(getString(R.string.login_page_password_error));
        }
        else{
            return true;
        }
        return false;
    }

    /**
     *  Verify identity with database
     * @param email input
     * @param password input
     */
    private void loginAttempt(final String email, final String password){
        // Show progressbar.
        showLoading();
        // Log in attempt with email and password input.
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Login successful, open dashboard for user.
                if(task.isSuccessful()){
                    verifyRoleAndStartDashboard(email, password);
                }
                // Login failed, show error message
                else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), getString(R.string.login_page_login_fail) + errorMessage, Toast.LENGTH_LONG).show();
                    showContents();
                }
            }
        });
    }

    /**
     *  Verify the current user role and go to corresponding dashboard
     * @param email
     */
    private void verifyRoleAndStartDashboard(final String email, final String password){
        // Read user id;
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    // user is a chef, go to store dashboard
                    FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String userName;
                            // Read chef data
                            Chef chef = dataSnapshot.getValue(Chef.class);
                            // Save chef as local user
                            CurrentUser.setUserId(mUserId);
                            CurrentUser.setUserRole(User.Role.CHEF);
                            CurrentUser.setStore(chef.getStore());
                            CurrentUser.setStoreStatus(chef.getStoreStatus());
                            CurrentUser.setUserEmail(email);
                            CurrentUser.setUserPassword(password);
                            // Set up user presented name
                            if(chef.getName() != null){
                                CurrentUser.setUserName(chef.getName());
                                userName = chef.getName();
                            }else{
                                userName = email;
                            }
                            // Start store dashboard
                            Intent storeIntent = new Intent(getApplicationContext(), MenuDashboardActivity.class);
                            startActivity(storeIntent);
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.login_page_login_success) + userName, Toast.LENGTH_LONG).show();
                            finish();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            String message = databaseError.getMessage();
                            Toast.makeText(getApplicationContext(), getString(R.string.error_info) + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String userName;
                    // user is a customer
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    // Save customer as local user
                    CurrentUser.setUserId(mUserId);
                    // Set up user presented name
                    if(customer.getName() != null){
                        CurrentUser.setUserName(customer.getName());
                        userName = customer.getName();
                    }else {
                        userName = email;
                    }
                    CurrentUser.setUserRole(User.Role.CUSTOMER);
                    CurrentUser.setUserEmail(email);
                    CurrentUser.setUserPassword(password);
                    // Start customer dashboard
                    Intent customerIntent = new Intent(getApplicationContext(), StoreDashboardActivity.class);
                    startActivity(customerIntent);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.login_page_login_success) + userName, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = databaseError.getMessage();
                Toast.makeText(getApplicationContext(), getString(R.string.error_info) + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        loginForm.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     *  Show sumbit form contents
     */
    private void showContents(){
        loginForm.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
