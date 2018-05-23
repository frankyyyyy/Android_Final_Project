package com.example.frank.final_project.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.frank.final_project.Adapter.MessageListViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.Message;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.chat_page_chat_contents_progressbar_lyout)
    LinearLayout mProgressBarView;

    @BindView(R.id.chat_page_chat_contents_Sv)
    ScrollView mMessageListView;

    @BindView(R.id.chat_page_chat_contents_Rv)
    RecyclerView mMessageList;

    @BindView(R.id.chat_page_send_contents_Et)
    EditText mSendContent;

    private String userId;
    private User.Role userRole;
    private String oppositeUserId;
    private String oppositeUserName;
    private DatabaseReference selfMessageRef;
    private DatabaseReference oppositeMessageRef;
    private FirebaseRecyclerAdapter messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        // Load page contents
        loading();
    }

    @OnClick(R.id.chat_page_send_Btn)
    public void sendMessage(){
        // Read message content
        String newMessageContent = mSendContent.getText().toString();
        // Read time from system
        String timeStamp = getDatetime();
        // Build a new message
        Message newMessage = new Message(oppositeUserName, timeStamp, newMessageContent);
        // Push message to database regarding to snapshot reference
        String key = oppositeMessageRef.push().getKey();
        oppositeMessageRef.child(key).setValue(newMessage);
        selfMessageRef.child(key).setValue(newMessage);
        // Clear input line
        mSendContent.setText("");
    }

    /**
     *  Read time from system
     * @return system time
     */
    private static String getDatetime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.TIME_FORMAT);
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }


    /**
     *  Load page contents
     */
    private void loading() {
        showLoading();
        // Load current user Id
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Load chat target user Id
        loadOppositeUserInfo();
        // Read self message snapshot reference
        selfMessageRef = selfMessageRef();
        // Read opposite message snapshot reference
        oppositeMessageRef = oppositeMessageRef();
        // Attach messages
        attachMessageList();
        // Show contents
        showContents();
    }

    /**
     *  Load chat target user id from previous page
     */
    private void loadOppositeUserInfo(){
        Intent intent = getIntent();
        if(intent != null){
            // The user is a chef, chat target is a customer.
            if(intent.getStringExtra(Constant.CHEF_ID) == null){
                userRole = User.Role.CHEF;
                oppositeUserId = intent.getStringExtra(Constant.CUSTOMER_ID);
                oppositeUserName = intent.getStringExtra(Constant.CUSTOMER_NAME);
            }
            // The user is a customer, chat target is a chef.
            else{
                userRole = User.Role.CUSTOMER;
                oppositeUserId = intent.getStringExtra(Constant.CHEF_ID);
                oppositeUserName = intent.getStringExtra(Constant.CHEF_NAME);
            }
        }else{
            tryAgainWarning();
        }
    }

    /**
     *  Get corresponding reference to message
     * @return opposite regarded message reference
     */
    private DatabaseReference oppositeMessageRef(){
        return (userRole == User.Role.CUSTOMER) ?
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(oppositeUserId).child(Constant.MESSAGES).child(userId) :
                FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(oppositeUserId).child(Constant.MESSAGES).child(userId);
    }

    /**
     *  Get corresponding reference to message
     * @return self regarded message reference
     */
    private DatabaseReference selfMessageRef() {
        return (userRole == User.Role.CUSTOMER) ?
                FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(userId).child(Constant.MESSAGES).child(oppositeUserId) :
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(userId).child(Constant.MESSAGES).child(oppositeUserId);
    }

    /**
     *  Attach message info to list
     */
    private void attachMessageList(){
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(oppositeMessageRef, Message.class)
                        .setLifecycleOwner(this)
                        .build();
        messageListAdapter = new MessageListViewAdapter(options, this);
        mMessageList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mMessageList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mMessageList.setLayoutManager(mLayoutManager);
        mMessageList.setItemAnimator(new DefaultItemAnimator());
        mMessageList.setAdapter(messageListAdapter);
    }


    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        mMessageListView.setVisibility(View.GONE);
        mProgressBarView.setVisibility(View.VISIBLE);
    }

    /**
     *  Show contents
     */
    private void showContents(){
        mMessageListView.setVisibility(View.VISIBLE);
        mProgressBarView.setVisibility(View.GONE);
    }


    /**
     *  Loading fail, return to previous activity and show error message
     */
    private void tryAgainWarning(){
        finish();
        Toast.makeText(getApplicationContext(), getString(R.string.chat_page_try_again_warning), Toast.LENGTH_LONG).show();
    }
}
