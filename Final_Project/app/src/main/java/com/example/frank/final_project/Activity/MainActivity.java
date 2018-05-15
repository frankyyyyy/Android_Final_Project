package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.frank.final_project.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    /**
     * Assign functions to different buttons
     * @param view
     */
    @OnClick({R.id.main_page_login_Btn, R.id.main_page_register_Btn})
    public void onClick(View view){
        switch (view.getId()){
            // Open login page
            case R.id.main_page_login_Btn:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            // Open register page
            case R.id.main_page_register_Btn:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
            default:
                break;
        }
    }
}
