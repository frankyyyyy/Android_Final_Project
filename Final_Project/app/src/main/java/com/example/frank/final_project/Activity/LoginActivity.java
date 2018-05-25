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
import com.example.frank.final_project.Model.CurrentUser;
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

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_page_login_progress)
    ProgressBar mProgressBar;

    @BindView(R.id.login_page_login_form)
    LinearLayout mLoginForm;

    @BindView(R.id.login_page_email_Et)
    EditText mEmail;

    @BindView(R.id.login_page_password_Et)
    EditText mPassword;

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
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
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
            mEmail.setError(getString(R.string.login_page_email_error));
        }
        // Check password input validity
        else if(!Utils.passwordInputIsLegal(password)){
            mPassword.setError(getString(R.string.login_page_password_error));
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
                    Toast.makeText(getApplicationContext(), getString(R.string.login_page_warning_login_fail), Toast.LENGTH_LONG).show();
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
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    // user is a chef, go to store dashboard
                    Intent storeIntent = new Intent(getApplicationContext(), MenuDashboard.class);
                    CurrentUser.setUserId(userId);
                    CurrentUser.setUserRole(User.Role.CHEF);
                    CurrentUser.setUserEmail(email);
                    CurrentUser.setUserPassword(password);
//                    // Pass role identity
//                    storeIntent.putExtra(Constant.CHEF, Constant.CHEF);
//                    // Pass user email and password
//                    storeIntent.putExtra(Constant.EMAIL, email);
//                    storeIntent.putExtra(Constant.PASSWORD, password);
                    // Start store dashboard
                    startActivity(storeIntent);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.login_page_login_success) + ": " + email, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // user is a customer
                    Intent customerIntent = new Intent(getApplicationContext(), StoreDashboard.class);
                    CurrentUser.setUserId(userId);
                    CurrentUser.setUserRole(User.Role.CUSTOMER);
                    CurrentUser.setUserEmail(email);
                    CurrentUser.setUserPassword(password);
//                    // Pass user email and password
//                    customerIntent.putExtra(Constant.EMAIL, email);
//                    customerIntent.putExtra(Constant.PASSWORD, password);
                    // Start customer dashboard
                    startActivity(customerIntent);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.login_page_login_success) + ": " + email, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        mLoginForm.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     *  Show sumbit form contents
     */
    private void showContents(){
        mLoginForm.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }
}
