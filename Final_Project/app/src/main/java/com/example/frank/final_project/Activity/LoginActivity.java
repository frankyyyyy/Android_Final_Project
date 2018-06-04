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
    private String mEmail;
    private String mPassword;

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
                mEmail = this.email.getText().toString();
                mPassword = this.password.getText().toString();
                // Check inputs validation
                if(allInputsAreValid(mEmail, mPassword)) {
                    // Attempt to login
                    loginAttempt();
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
     */
    private void loginAttempt(){
        // Show progressbar.
        showLoading();
        // Log in attempt with email and password input.
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Login successful, open dashboard for user.
                if(task.isSuccessful()){
                    verifyRoleAndStartDashboard();
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
     */
    private void verifyRoleAndStartDashboard(){
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
                            // Read chef data
                            Chef chef = dataSnapshot.getValue(Chef.class);
                            // Save chef as local user
                            CurrentUser.setChef(chef);
                            CurrentUser.setUserRole(User.Role.CHEF);
                            setBasicInfoInLocal(chef.getName());
                            // Set up user presented name
                            String welcomeName = (chef.getName() == null) ? mEmail : chef.getName();
                            // Start store dashboard
                            Intent storeIntent = new Intent(getApplicationContext(), MenuDashboardActivity.class);
                            startActivity(storeIntent);
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.login_page_login_success) + welcomeName, Toast.LENGTH_LONG).show();
                            finish();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            String message = databaseError.getMessage();
                            Toast.makeText(getApplicationContext(), getString(R.string.error_info) + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // user is a customer
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    // Save customer as local user
                    CurrentUser.setCustomer(customer);
                    CurrentUser.setUserRole(User.Role.CUSTOMER);
                    setBasicInfoInLocal(customer.getName());
                    // Set up user presented name
                    String welcomeName = (customer.getName() == null) ? mEmail : customer.getName();
                    // Start customer dashboard
                    Intent customerIntent = new Intent(getApplicationContext(), StoreDashboardActivity.class);
                    startActivity(customerIntent);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.login_page_login_success) + welcomeName, Toast.LENGTH_LONG).show();
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
     * Save login user info as local
     * @param userName
     */
    private void setBasicInfoInLocal(String userName){
        CurrentUser.setUserId(mUserId);
        CurrentUser.setUserName(userName);
        CurrentUser.setUserEmail(mEmail);
        CurrentUser.setUserPassword(mPassword);
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
