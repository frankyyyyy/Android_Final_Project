package com.example.frank.final_project.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.frank.final_project.Adapter.MessageListViewAdapter;
import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Constant.Constant_Debug;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.Message;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  Chat room activity
 */
public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.chat_toolbar)
    Toolbar toolbar;

    @BindView(R.id.chat_page_chat_contents_progressbar)
    ProgressBar progressBar;

    @BindView(R.id.chat_page_chat_contents_Rv)
    RecyclerView messageList;

    @BindView(R.id.chat_page_send_contents_Et)
    EditText sendContent;

    private String mUserId;
    private String mUserName;
    private User.Role userRole;
    private String mChatTargetId;
    private String mChatTargetName;
    private DatabaseReference mSelfMessageRef;
    private DatabaseReference mOppositeMessageRef;
    private FirebaseRecyclerAdapter mMessageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        Log.d(Constant_Debug.TAG_CHAT, Constant_Debug.CHAT_CREATED);

        // Setup tool bar button
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        // Set back button function
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Show loading
        showLoading();
        // Load user info
        mUserId = CurrentUser.getUserId();
        mUserName = CurrentUser.getUserName();
        userRole = CurrentUser.getUserRole();
        mChatTargetId = CurrentUser.getChatTargetId();
        mChatTargetName = CurrentUser.getChatTargetName();
        // Read self message snapshot reference
        mSelfMessageRef = selfMessageRef();
        // Read opposite message snapshot reference
        mOppositeMessageRef = oppositeMessageRef();
        // Attach messages
        attachMessageList();
        // Set tittle for chatting target
        setTitleForChatTarget();
        // Show contents
        showContents();
    }

    @Override
    protected void onDestroy() {
        Log.d(Constant_Debug.TAG_CHAT, Constant_Debug.CHAT_DESTROYED);
        super.onDestroy();
    }

    /**
     *  Set title to user name if user name exhausts,
     *  otherwise set id as title.
     */
    private void setTitleForChatTarget(){
        if(mChatTargetName != null){
            this.setTitle(mChatTargetName);
        }else{
            this.setTitle(mChatTargetId);
        }
    }

    /**
     *  Write message into database when send button is clicked
     */
    @OnClick(R.id.chat_page_send_Btn)
    public void sendMessage(){
        // Read message content
        String newMessageContent = sendContent.getText().toString();
        // If input is valid, create new message and send it.
        if(newMessageContent != null){
            // Build a new message
            Message newMessage = createNewMessage(newMessageContent);
            // Push message to database regarding to snapshot reference
            String key = mOppositeMessageRef.push().getKey();
            newMessage.setStatus(Message.Status.UnRead.toString());
            mOppositeMessageRef.child(key).setValue(newMessage);
            newMessage.setStatus(Message.Status.Read.toString());
            mSelfMessageRef.child(key).setValue(newMessage);
            Log.d(Constant_Debug.TAG_CHAT, Constant_Debug.NEW_MESSAGE_SENT);
            // Clear input line
            sendContent.setText("");
        }else{
            // If input is not valid, show warning message.
            Toast.makeText(getApplicationContext(), getString(R.string.message_empty_deny), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Create a new message according to user input
     * @param newMessageContent
     * @return
     */
    private Message createNewMessage(String newMessageContent){
        // Read time from system
        String timeStamp = getDatetime();
        // Create new message
        Message newMessage = new Message();
        newMessage.setSenderId(mUserId);
        if(mUserName != null) {
            newMessage.setSender(mUserName);
        }
        newMessage.setContent(newMessageContent);
        newMessage.setTime(timeStamp);
        Log.d(Constant_Debug.TAG_CHAT, Constant_Debug.NEW_MESSAGE_CREATED);
        return newMessage;
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
     *  Get corresponding reference to message
     * @return opposite regarded message reference
     */
    private DatabaseReference oppositeMessageRef(){
        return (userRole == User.Role.CUSTOMER) ?
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(mChatTargetId).child(Constant.MESSAGES).child(mUserId) :
                FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(mChatTargetId).child(Constant.MESSAGES).child(mUserId);
    }

    /**
     *  Get corresponding reference to message
     * @return self regarded message reference
     */
    private DatabaseReference selfMessageRef() {
        return (userRole == User.Role.CUSTOMER) ?
                FirebaseDatabase.getInstance().getReference(Constant.CUSTOMER).child(mUserId).child(Constant.MESSAGES).child(mChatTargetId) :
                FirebaseDatabase.getInstance().getReference(Constant.CHEF).child(mUserId).child(Constant.MESSAGES).child(mChatTargetId);
    }

    /**
     *  Attach message info to list
     */
    private void attachMessageList(){
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(mSelfMessageRef, Message.class)
                        .setLifecycleOwner(this)
                        .build();
        mMessageListAdapter = new MessageListViewAdapter(options, this);
        messageList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        messageList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        messageList.setLayoutManager(mLayoutManager);
        messageList.setItemAnimator(new DefaultItemAnimator());
        messageList.setAdapter(mMessageListAdapter);
        Log.d(Constant_Debug.TAG_CHAT, Constant_Debug.MESSAGE_ATTACHED);
    }

    /**
     *  Show loading progress bar
     */
    private void showLoading(){
        messageList.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     *  Show contents
     */
    private void showContents(){
        messageList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
