package com.unsalgasimliapplicationsnsug.projectnsug;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class NudgeReceiver extends BroadcastReceiver {
    public static final String EXTRA_TITLE   = "nudge_title";
    public static final String EXTRA_MESSAGE = "nudge_message";
    private static final String CHANNEL_ID    = "nudge_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title   = intent.getStringExtra(EXTRA_TITLE);
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel on O+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Nudges",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.pngegg) // your drawable
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        // Use timestamp as unique ID
        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}
