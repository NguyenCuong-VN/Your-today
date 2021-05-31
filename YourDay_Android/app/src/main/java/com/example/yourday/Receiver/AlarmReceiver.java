package com.example.yourday.Receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.yourday.MainActivity;
import com.example.yourday.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 101;
    final String CHANNEL_ID = "101";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra("title") != null || intent.getStringExtra("des") != null){
            Log.e("Rev","rev");
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel1 = new NotificationChannel(
                        CHANNEL_ID,
                        "Channel 1",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel1.setDescription("This is Channel 1");
                manager.createNotificationChannel(channel1);
            }
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            Log.e("trong receiver", "alo");
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(intent.getStringExtra("title"))
                    .setContentText(intent.getStringExtra("des"))
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(bitmap)
                    .setAutoCancel(true);
            Intent i = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
            notification.setContentIntent(pendingIntent);
            manager.notify(12345, notification.build());
        }
        else {
            Log.e("ngoai receiver", "k noti");
        }

    }
}
