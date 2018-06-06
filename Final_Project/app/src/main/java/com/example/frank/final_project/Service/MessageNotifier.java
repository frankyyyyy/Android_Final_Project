package com.example.frank.final_project.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Constant.Constant_Debug;
import com.example.frank.final_project.Model.CurrentUser;
import com.example.frank.final_project.Model.Message;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *  Message notifier service
 *  Notify new unread message when activated.
 */
public class MessageNotifier extends Service {

    private DatabaseReference mMessageRef;
    private ChildEventListener mMessageListener;
    private ChildEventListener mMessageDetailsListener;
    private String mUserId;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private String mNewMessageKey;
    private int count;

    public MessageNotifier() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constant_Debug.TAG_NOTIFICATION, Constant_Debug.SERVICE_CREATED);
        // Notification count
        count = 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    /**
     * Create and publish a new notification
     * @param senderName
     * @param content
     */
    public void createNotification(String senderName, String content, String mOppositeId){
        // Create new notification manager
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        // Check the running environment to decide the mBuilder function version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = mNotificationManager.getNotificationChannel(Constant.CHANNEL_1);
            if (mChannel == null) {
                mChannel = new NotificationChannel(Constant.CHANNEL_1, Constant.CHANNEL, importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }
            mBuilder = new NotificationCompat.Builder(this, Constant.CHANNEL_1);
        } else {
            mBuilder = new NotificationCompat.Builder(this);
        }

        // Input attributes into notification
        mBuilder.setSmallIcon(R.drawable.notification_icon);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_page_logo));
        mBuilder.setAutoCancel(true);
        mBuilder.setOngoing(false);
        mBuilder.setShowWhen(true);
        mBuilder.setContentTitle(senderName);
        mBuilder.setContentText(content);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        Notification notification = mBuilder.build();
        count++;
        mNotificationManager.notify(count, notification);
        Log.d(Constant_Debug.TAG_NOTIFICATION, Constant_Debug.SERVICE_NOTIFICATION_PUBLISHED);
        // Set message status to read
        mMessageRef.child(mOppositeId).child(mNewMessageKey).child(Constant.STATUS).setValue(Message.Status.Read.toString());
        Log.d(Constant_Debug.TAG_NOTIFICATION, Constant_Debug.SERVICE_MESSAGE_STATUS_CHANGED);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constant_Debug.TAG_NOTIFICATION, Constant_Debug.SERVICE_ONSTARTCOMMAND);
        // Get user id
        mUserId = CurrentUser.getUserId();
        // Get user message reference
        mMessageRef = FirebaseDatabase.getInstance().getReference();
        mMessageRef = (CurrentUser.getUserRole() == User.Role.CUSTOMER) ?
                mMessageRef.child(Constant.CUSTOMER).child(mUserId).child(Constant.MESSAGES) :
                mMessageRef.child(Constant.CHEF).child(mUserId).child(Constant.MESSAGES);
        // Add listener on new message
        mMessageListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // add listener to every person
                mMessageDetailsListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // add listener to every message
                        Message newMessage = dataSnapshot.getValue(Message.class);
                        String temp = dataSnapshot.getKey();
                        if(mNewMessageKey != temp){
                            mNewMessageKey = temp;
                            String senderId = newMessage.getSenderId();
                            String senderName = newMessage.getSender();
                            String content = newMessage.getContent();
                            String status = newMessage.getStatus();
                            if((status != null) && status.equals(Message.Status.UnRead.toString())){
                                if((senderId != null) && !senderId.equals(mUserId)){
                                    String mOppositeId = dataSnapshot.getRef().getParent().getKey();
                                    createNotification(senderName, content, mOppositeId);
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mMessageRef.child(dataSnapshot.getKey()).addChildEventListener(mMessageDetailsListener);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mMessageRef.addChildEventListener(mMessageListener);
        // Add listener to user login status. Stop service when user sign out.
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    stopSelf();
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
//         Remove listener on every chat target
        mMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    mMessageRef.child(snapshot.getKey()).removeEventListener(mMessageDetailsListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Remove listener on message snapshot
        mMessageRef.removeEventListener(mMessageListener);
        super.onDestroy();
        Log.d(Constant_Debug.TAG_NOTIFICATION, Constant_Debug.SERVICE_DESTROYED);
    }
}
