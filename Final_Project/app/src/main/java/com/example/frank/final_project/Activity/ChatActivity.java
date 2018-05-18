package com.example.frank.final_project.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.chat_page_chat_contents_Pb)
    ProgressBar mProgressBar;

    @BindView(R.id.error_refresh_kit)
    LinearLayout refresh;

    @BindView(R.id.chat_page_chat_contents_Rv)
    RecyclerView mMessageList;

    @BindView(R.id.chat_page_send_contents_Et)
    EditText mSendContent;

    private String userId;
    private User.role userRole;
    private String oppositeUserId;
    private DatabaseReference messageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        loading();






    }

    private void loading() {
        // Load current user Id
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Load chat target user Id
        loadOppositeUserId();
        // Read message snapshot reference
        messageRef = messageRef();
    }

    private void loadOppositeUserId(){
        Intent intent = getIntent();
        if(intent != null){
            // The user is a chef, chat target is a customer.
            if(intent.getStringExtra(Constant.CHEF_ID) == null){
                userRole = User.role.CHEF;
                oppositeUserId = intent.getStringExtra(Constant.CUSTOMER_ID);
            }
            // The user is a customer, chat target is a chef.
            else{
                userRole = User.role.CUSTOMER;
                oppositeUserId = intent.getStringExtra(Constant.CHEF_ID);
            }
        }
        tryAgainWarning();
    }

    private DatabaseReference messageRef(){
        return (userRole == User.role.CUSTOMER) ?
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(oppositeUserId).child(Constant.MESSAGES).child(userId) :
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.MESSAGES).child(oppositeUserId);
    }














    private void tryAgainWarning(){
        finish();
        Toast.makeText(getApplicationContext(), getString(R.string.chat_page_try_again_warning), Toast.LENGTH_LONG).show();
    }

    private void loadingFailWarning() {
        Toast.makeText(this, getString(R.string.chat_page_loading_fail_warning), Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.error_refresh_kit)
    public void refresh(){
        finish();
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}
