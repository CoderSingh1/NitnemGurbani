package com.satnamsinghmaggo.nitnemgurbani;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmDebug", "Alarm received!");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "daily_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Nitnem Reminder")
                .setContentText("Waheguru Ji Ka Khalsa 🙏🏻")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (android.os.Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
            manager.notify(101, builder.build());
        } else {
            Log.e("AlarmDebug", "Notification permission not granted");
        }
    }
}
