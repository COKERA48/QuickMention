package com.CSC481Project.ashley.quickmentiontest;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ashley on 3/18/2018.
 */

public class Alarm extends BroadcastReceiver {
    private static final String TAG = "Alarm";
    private String taskName, startDate, startTime, endDate, endTime;
    private long interval, initialTime, timestamp;
    private int alarmId;
    Calendar c;
    private Uri mCurrentReminderUri;
    @Override
    public void onReceive(Context context, Intent intent) {
        c = Calendar.getInstance();
        mCurrentReminderUri = intent.getData();

        Bundle bundle = intent.getExtras();
        if (bundle != null ) {
            interval = bundle.getLong("interval");
            initialTime = bundle.getLong("initialTime");
        }

        ContentResolver mResolver = context.getContentResolver();
        Cursor cursor = mResolver.query(mCurrentReminderUri, new String[] {
                        QMContract.TaskEntry.KEY_NAME,
                        QMContract.TaskEntry.KEY_START_DATE,
                        QMContract.TaskEntry.KEY_START_TIME,
                        QMContract.TaskEntry.KEY_END_DATE,
                        QMContract.TaskEntry.KEY_END_TIME,
                        QMContract.TaskEntry.KEY_ALARM_ID,
                        QMContract.TaskEntry.KEY_TIMESTAMP},
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            taskName = cursor.getString(cursor.getColumnIndex(QMContract.TaskEntry.KEY_NAME));
            startDate = cursor.getString(cursor.getColumnIndex(QMContract.TaskEntry.KEY_START_DATE));
            startTime = cursor.getString(cursor.getColumnIndex(QMContract.TaskEntry.KEY_START_TIME));
            endDate = cursor.getString(cursor.getColumnIndex(QMContract.TaskEntry.KEY_END_DATE));
            endTime = cursor.getString(cursor.getColumnIndex(QMContract.TaskEntry.KEY_END_TIME));
            alarmId = cursor.getInt(cursor.getColumnIndex(QMContract.TaskEntry.KEY_ALARM_ID));
            timestamp = cursor.getLong(cursor.getColumnIndex(QMContract.TaskEntry.KEY_TIMESTAMP));
            cursor.close();
        }

        Log.d(TAG, "Alarm worked." + c.getTime().toString() + " initialTime: " + initialTime + " interval: " + interval + " timestamp: " + timestamp + " alarmId: " + alarmId);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        notificationManager.notify(alarmId,builder.build());

        if (interval != 0) {
            try {
                rescheduleAlarm(context, intent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void rescheduleAlarm(Context context, Intent intent) throws ParseException {
        //increment time of alarm by interval
        initialTime += interval;

        //reset timestamp for database
        c.setTimeInMillis(initialTime);
        timestamp = c.getTimeInMillis();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        DateFormat tf = new SimpleDateFormat("hh:mm a", Locale.US);

        //reset start date and time for database
        startDate = df.format(c.getTime());
        startTime = tf.format(c.getTime());

        DateFormat dtf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);

        //reset end date and time by getting the old time in millis and adding interval
        Date end = dtf.parse(endDate + " " + endTime);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(end.getTime());
        long endTimeInMillis = c2.getTimeInMillis();
        endTimeInMillis += interval;
        c2.setTimeInMillis(endTimeInMillis);
        endDate = df.format(c2.getTime());
        endTime = tf.format(c2.getTime());

        //setting values to be updated in db
        ContentValues values = new ContentValues();
        values.put(QMContract.TaskEntry.KEY_NAME, taskName);
        values.put(QMContract.TaskEntry.KEY_START_DATE, startDate);
        values.put(QMContract.TaskEntry.KEY_START_TIME, startTime);
        values.put(QMContract.TaskEntry.KEY_END_DATE, endDate);
        values.put(QMContract.TaskEntry.KEY_END_TIME, endTime);
        values.put(QMContract.TaskEntry.KEY_TIMESTAMP, timestamp);

        //update values in db
        context.getContentResolver().update(mCurrentReminderUri, values, null, null);

        //send new time for alarm to onRecieve()
        intent.putExtra("initialTime", initialTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
