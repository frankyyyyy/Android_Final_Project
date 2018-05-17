package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {

    private String chefId;

    @BindView(R.id.hahahahhahahhahh)
    TextView textView;

    @BindView(R.id.chat_page_progressbar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        Intent chefInfoIntent = getIntent();
        if(chefInfoIntent != null){
            chefId = chefInfoIntent.getStringExtra(Constant.CHEF_ID);
        }

        textView.setText(chefId);
        textView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

    }
}
