package com.example.frank.final_project.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.frank.final_project.Constant.Constant;
import com.example.frank.final_project.Model.User;
import com.example.frank.final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageNotifier extends Service {
    public MessageNotifier() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void createNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_page_logo));
        builder.setAutoCancel(true);
        builder.setOngoing(false);
        builder.setShowWhen(true);
        builder.setContentTitle();
        builder.setContent();
        Notification notification = builder.build();
        startForeground(1234, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        User.Role role = (intent.getStringExtra(Constant.CHEF) == null) ?
                User.Role.CUSTOMER : User.Role.CHEF;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference(Constant.CHEF);
        messageRef = (role == )
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
