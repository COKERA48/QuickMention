package com.CSC481Project.ashley.quickmentiontest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Matts on 3/12/2018.
 */

public class Alarm extends BroadcastReceiver {
    private static final String TAG = "Alarm";
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar c = Calendar.getInstance();
        Log.d(TAG, "Alarm worked." + c.getTime().toString());
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.qm_launch_logo)
                .setContentIntent(pendingIntent)
                .setContentText("this is my notification")
                .setContentTitle("my notification")
                .setSound(alarmSound)
                .setAutoCancel(true);
        notificationManager.notify(100,builder.build());
    }
}
