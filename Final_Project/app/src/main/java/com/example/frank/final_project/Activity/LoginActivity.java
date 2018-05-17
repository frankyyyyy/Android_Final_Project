package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.frank.final_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

    private DatabaseReference roleRef;
    private ValueEventListener valueEventListener;

    private boolean loginSuccessful = false;
    private boolean connectionSuccessful = false;

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
                    new loginAttempt(email, password).execute();
                }
                break;
            // Open register page
            case R.id.login_page_cancel_Btn:
                Intent mainPageIntent = new Intent(this, MainActivity.class);
                startActivity(mainPageIntent);
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
     * Load email and password and spend 2 seconds for verification.
     */
    private class loginAttempt extends AsyncTask<Void, Void, Void> {

        private final String email;
        private final String password;

        public loginAttempt(String email, String password){
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mLoginForm.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... input) {
            try {
                // Loading for 2 seconds
                Thread.sleep(Constant.LOGIN_IN_LOADING_TIME);
                try{
                    // Verify identity with database
                    verifyIdentity(email, password);
                } catch (Exception e){
                    // Connection issue happens.
                    connectionSuccessful = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressBar.setVisibility(View.GONE);
            mLoginForm.setVisibility(View.VISIBLE);
            // Connection to Firebase in fail. Demonstrate warning message.
            if(!connectionSuccessful){
                Toast.makeText(getApplicationContext(), getString(R.string.connection_issue_warning), Toast.LENGTH_LONG).show();
            }
            // If account email and password are not verified, then show warning message
            else if(!loginSuccessful){
                Toast.makeText(getApplicationContext(), getString(R.string.login_page_warning_login_fail), Toast.LENGTH_LONG).show();
            }
            // If account email and password are verified, then start dashboard
            else{
                Intent intent = new Intent(getApplicationContext(), CustomerDashboard.class);
                startActivity(intent);
                finish();
//                checkRoleNOpenDashboard();
            }
        }
    }

    private void checkRoleNOpenDashboard(){

    }

    /**
     *  Verify identity with database
     * @param email input
     * @param password input
     */
    private void verifyIdentity(String email, String password){
        // Log in attempt with email and password input
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Connection successful, set values in return.
                connectionSuccessful = true;
                // Login successful, set values in return.
                if(task.isSuccessful()){
                    loginSuccessful = true;
                }
                // Login failed, set values in return.
                else{
                    loginSuccessful = false;
                }
            }
        });
    }


}
