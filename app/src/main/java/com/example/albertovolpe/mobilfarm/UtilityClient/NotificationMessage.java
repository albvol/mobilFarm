package com.example.albertovolpe.mobilfarm.UtilityClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.albertovolpe.mobilfarm.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationMessage extends BroadcastReceiver {

    private static final String TAG = "NotificationMessage";
    private static String title, message;

    @Override
    public void onReceive(Context context, Intent intent) {
        title =  intent.getStringExtra("Title");
        message = intent.getStringExtra("Message");

        showNotification(context);
    }

    private void showNotification(Context context){

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, NotificationMessage.class), 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
