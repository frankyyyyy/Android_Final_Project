package com.example.frank.final_project.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.frank.final_project.Constant.Constant;
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

public class MessageNotifier extends Service {

    private DatabaseReference messageRef;
    private String userId;
    private String oppositeId;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;
    private String name = "mChannel";
    private String id = "mChannel_1";

    private String newMessageKey;
    private int count;

    public MessageNotifier() {
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
    public void createNotification(String senderName, String content){
        // Create new notification manager
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        // Check the running environment to decide the builder function version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = mNotificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        // Input attributes into notification
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_page_logo));
        builder.setAutoCancel(true);
        builder.setOngoing(false);
        builder.setShowWhen(true);
        builder.setContentTitle(senderName);
        builder.setContentText(content);
        builder.setDefaults(Notification.DEFAULT_ALL);
        Notification notification = builder.build();
        count++;
        mNotificationManager.notify(count, notification);
        // Set message status to read
        messageRef.child(oppositeId).child(newMessageKey).child(Constant.STATUS).setValue(Message.Status.Read.toString());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Notification count
        count = 0;
        // Get user id
        userId = CurrentUser.getUserId();
        // Get user message reference
        messageRef = FirebaseDatabase.getInstance().getReference();
        messageRef = (CurrentUser.getUserRole() == User.Role.CUSTOMER) ?
                messageRef.child(Constant.CUSTOMER).child(userId).child(Constant.MESSAGES) :
                messageRef.child(Constant.CHEF).child(userId).child(Constant.MESSAGES);
        // Add listener on new message
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                oppositeId = dataSnapshot.getKey();
                for(DataSnapshot messageSnapshot: dataSnapshot.getChildren()){
                    Message newMessage = messageSnapshot.getValue(Message.class);
                    newMessageKey = messageSnapshot.getKey();
                    String senderId = newMessage.getSenderId();
                    String senderName = newMessage.getSender();
                    String content = newMessage.getContent();
                    String status = newMessage.getStatus();
                    if((status != null) && status.equals(Message.Status.UnRead.toString())){
                        if((senderId != null) && !senderId.equals(userId)){
                            createNotification(senderName, content);
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
        });
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
}
