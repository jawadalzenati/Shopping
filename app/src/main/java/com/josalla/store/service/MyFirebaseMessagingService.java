package com.josalla.store.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.josalla.store.BalanceActivity;
import com.josalla.store.MainActivity;
import com.josalla.store.OrdersActivity;
import com.josalla.store.R;
import com.josalla.store.WithdrawListActivity;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "notification00";

    int count = 0;
    Intent intent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        Log.d(TAG, token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(String title, String messageBody) {

        if (title.contains("طلب جديد")||title.contains("طلبات جديدة")) {
            intent = new Intent(this, OrdersActivity.class);

        } else if (title.contains("استلام الطلب")) {
            intent = new Intent(this, OrdersActivity.class);

        } else if (title.contains("طلب دفع")) {
            intent = new Intent(this, WithdrawListActivity.class);
        }else if (title.contains("اضافة دفعة")){
            intent = new Intent(this, BalanceActivity.class);

        } else {
            intent = new Intent(this, MainActivity.class);

        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_cart)
                        .setColor(getResources().getColor(R.color.gry))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        count += 1;
        notificationManager.notify(0/* ID of notification */, notificationBuilder.build());
    }
}
