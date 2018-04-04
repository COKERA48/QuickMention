package com.CSC481Project.ashley.quickmentiontest;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Matts on 3/12/2018.
 */

public class Alarm extends BroadcastReceiver {
    private static final String TAG = "Alarm";
    private String taskName;
    private int id;
    private long interval, initialTime;
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar c = Calendar.getInstance();

        Bundle bundle = intent.getExtras();
        if (bundle != null ) {
            taskName = bundle.getString("taskName");
            id = bundle.getInt("alarmID");
            interval = bundle.getLong("interval");
            initialTime = bundle.getLong("initialTime");
        }
        Log.d(TAG, "Alarm worked." + c.getTime().toString() + " initialTime: " + initialTime + " interval: " + interval);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Builder builder = new Builder(context)
                .setContentIntent(pendingIntent)
                .setContentTitle(taskName)
                .setSound(alarmSound)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_logo_transparent);
            builder.setColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            builder.setSmallIcon(R.drawable.ic_logo_transparent);
        }
        notificationManager.notify(id,builder.build());

        if (interval != 0) {
            rescheduleAlarm(context, intent);
        }
    }

    public void rescheduleAlarm(Context context, Intent intent) {
        initialTime += interval;
        intent.putExtra("initialTime", initialTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, initialTime, pendingIntent);
            }
            else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, initialTime, pendingIntent);
            }
        }
    }

}
