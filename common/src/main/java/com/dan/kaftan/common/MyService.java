package com.dan.kaftan.common;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyService extends FirebaseMessagingService {
    String TAG = "MyService";
    public MyService() {
        System.out.println("Registering to news");
        System.out.println("Registering to news");
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        System.out.println("Registered to news");

    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        System.out.println("Message from: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ false) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow(remoteMessage);
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            System.out.println("Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNow(remoteMessage) ;
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void handleNow(RemoteMessage remoteMessage){
        System.out.println("Alon" + remoteMessage.toString());
        // Intent to open your activity
        Intent intent = new Intent(this, DownloadCatalogActivity.class);
        NotificationUtils.notificatePush(this, 1, "Ticker", remoteMessage.getNotification().getTitle(), remoteMessage.getData().get("1stkey"), intent);
    }
}
