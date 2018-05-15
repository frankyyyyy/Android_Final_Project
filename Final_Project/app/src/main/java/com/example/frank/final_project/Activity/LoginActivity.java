package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.frank.final_project.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

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
                Intent DashboardIntent = new Intent(this, DashboardActivity.class);
                startActivity(DashboardIntent);
                finish();
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
}
