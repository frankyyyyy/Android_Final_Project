package com.example.frank.final_project.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.frank.final_project.Constant.Constant;
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

    public MessageNotifier() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void createNotification(String senderName, String content){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_page_logo));
        builder.setAutoCancel(true);
        builder.setOngoing(false);
        builder.setShowWhen(true);
        builder.setContentTitle(senderName);
        builder.setContentText(content);
        builder.setDefaults(Notification.DEFAULT_ALL);
        Notification notification = builder.build();
        mNotificationManager.notify(1234, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "Message Service Start ");
        // Recognize user role
        User.Role role = (intent.getStringExtra(Constant.CHEF) == null) ?
                User.Role.CUSTOMER : User.Role.CHEF;
        // Get user id
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get user message reference
        messageRef = FirebaseDatabase.getInstance().getReference();
        messageRef = (role == User.Role.CUSTOMER) ?
                messageRef.child(Constant.CUSTOMER).child(userId).child(Constant.MESSAGES) :
                messageRef.child(Constant.CHEF).child(userId).child(Constant.MESSAGES);
        // Add listener on new message
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String senderName = dataSnapshot.child(Constant.SENDER).getValue(String.class);
                String content = dataSnapshot.child(Constant.CONTENT).getValue(String.class);
                createNotification(senderName, content);
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
//         Add listener to user login status. Stop service when user sign out.
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    stopSelf();
                    Log.d("service", "Message Service Stop ");
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
