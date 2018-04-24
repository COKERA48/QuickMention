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
import java.util.Locale;

/**
 * Created by Ashley on 3/18/2018.
 */

public class Alarm extends BroadcastReceiver {
    private static final String TAG = "Alarm";
    private String taskName, date, time, repeats, notes;
    private long interval, initialTime, timestamp;
    private int alarmId;
    Calendar c;

    @Override
    public void onReceive(Context context, Intent intent) {
        c = Calendar.getInstance();
        Uri mCurrentReminderUri = intent.getData();

        Bundle bundle = intent.getExtras();
        if (bundle != null ) {
            interval = bundle.getLong("interval");
            initialTime = bundle.getLong("initialTime");
        }

        ContentResolver mResolver = context.getContentResolver();
        Cursor cursor = mResolver.query(mCurrentReminderUri, new String[] {
                        QMContract.TaskEntry.KEY_NAME,
                        QMContract.TaskEntry.KEY_DATE,
                        QMContract.TaskEntry.KEY_TIME,
                        QMContract.TaskEntry.KEY_REPEATS,
                        QMContract.TaskEntry.KEY_NOTES,
                        QMContract.TaskEntry.KEY_ALARM_ID,
                        QMContract.TaskEntry.KEY_TIMESTAMP},
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            taskName = cursor.getString(cursor.getColumnIndex(QMContract.TaskEntry.KEY_NAME));
            date = cursor.getString(cursor.getColumnIndex(QMContract.TaskEntry.KEY_DATE));
            time = cursor.getString(cursor.getColumnIndex(QMContract.TaskEntry.KEY_TIME));
            repeats = cursor.getString(cursor.getColumnIndex(QMContract.TaskEntry.KEY_REPEATS));
            notes = cursor.getString(cursor.getColumnIndex(QMContract.TaskEntry.KEY_NOTES));
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
        if(alarmId != 0) {
            notificationManager.notify(alarmId,builder.build());

            if (interval != 0) {
                try {
                    rescheduleAlarm(context);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void rescheduleAlarm(Context context) throws ParseException {
        //increment time of alarm by interval
        initialTime += interval;

        //save new timestamp for new task
        c.setTimeInMillis(initialTime);
        timestamp = c.getTimeInMillis();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        DateFormat tf = new SimpleDateFormat("hh:mm a", Locale.US);

        //save new start date and time strings for new task
        date = df.format(c.getTime());
        time = tf.format(c.getTime());

        DateFormat dtf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);


        //setting values for new task to be created
        ContentValues values = new ContentValues();
        values.put(QMContract.TaskEntry.KEY_NAME, taskName);
        values.put(QMContract.TaskEntry.KEY_DATE, date);
        values.put(QMContract.TaskEntry.KEY_TIME, time);
        values.put(QMContract.TaskEntry.KEY_REPEATS, repeats);
        values.put(QMContract.TaskEntry.KEY_NOTES, notes);
        values.put(QMContract.TaskEntry.KEY_ALARM_ID, alarmId);
        values.put(QMContract.TaskEntry.KEY_TIMESTAMP, timestamp);

        // Create new task with updated values
        Intent intent = new Intent(context.getApplicationContext(), Alarm.class);
        Uri newUri = context.getContentResolver().insert(QMContract.TaskEntry.CONTENT_URI, values);
        intent.setData(newUri);

        //send new time for alarm to onReceive()
        intent.putExtra("initialTime", initialTime);
        //intent.putExtra("interval", interval);

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
