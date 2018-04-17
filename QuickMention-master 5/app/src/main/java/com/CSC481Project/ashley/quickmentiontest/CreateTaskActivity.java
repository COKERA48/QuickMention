package com.CSC481Project.ashley.quickmentiontest;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private EditText editTextTaskName, editTextNotes;
    private TextView tvStartDate, tvStartTime, tvEndDate, tvEndTime;
    private DatePickerDialog.OnDateSetListener StartDateSetListener, EndDateSetListener;
    private TimePickerDialog.OnTimeSetListener StartTimeSetListener, EndTimeSetListener;
    Calendar calStart, calEnd;
    private Button buttonSaveTask;
    private static final String TAG = "TaskActivity";
    Spinner spinner;
    DateFormat dateFormat, timeFormat;
    String sourceClass = "";
    int alarmId = 0;
    Uri newUri;
    long timestamp;
    String templateName, templateRepeats;
    int idTempCat, usageTemplate;


    private Uri mCurrentReminderUri;

    private static final int EXISTING_VEHICLE_LOADER = 0;

    // Values for orientation change
    private static final String KEY_NAME = "title_key";
    private static final String KEY_START_TIME = "start_time_key";
    private static final String KEY_START_DATE = "start_date_key";
    private static final String KEY_END_TIME = "end_time_key";
    private static final String KEY_END_DATE = "end_date_key";
    private static final String KEY_REPEATS = "repeats_key";
    private static final String KEY_NOTES = "notes_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbarTask);
        setSupportActionBar(toolbar);

        // Set up layout variables
        editTextTaskName = findViewById(R.id.editTextTaskName);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvEndTime = findViewById(R.id.tvEndTime);
        editTextNotes = findViewById(R.id.editTextNotes);
        buttonSaveTask = findViewById(R.id.buttonSaveTask);
        Button buttonDeleteTask = findViewById(R.id.buttonDeleteTask);
        spinner = findViewById(R.id.spinner);

        // Get values passed by previous activity
        Intent intent = getIntent();
        mCurrentReminderUri = intent.getData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sourceClass = bundle.getString("ClassName");
        }
        if (sourceClass != null) {
            // If sourceClass is TemplateActivity, a new task is being created
            if (sourceClass.equals("TemplateActivity")) {
                Log.d(TAG, "test");
                setTitle("New Task");
                getLoaderManager().initLoader(1, null, this);
                buttonDeleteTask.setVisibility(View.GONE);
            }
            // if sourceClass is not TemplateActivity, a task that is already created is being updated/viewed
            else {
                setTitle("Edit Task");
                buttonSaveTask.setText("Update Task");
                getLoaderManager().initLoader(EXISTING_VEHICLE_LOADER, null, this);
            }
        }

        // Format for dates and times
        dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        timeFormat = new SimpleDateFormat( "hh:mm a", Locale.US);

        // Create calendar for start date and set initial display of start date and time.
        calStart = Calendar.getInstance();
        calStart.set(Calendar.SECOND, 0);
        tvStartDate.setText(dateFormat.format(calStart.getTime()));
        tvStartTime.setText(timeFormat.format(calStart.getTime()));

        // Create calendar for end date. Task end time is set to one hour after start time.
        calEnd = Calendar.getInstance();
        calEnd.set(Calendar.SECOND, 0);
        calEnd.add(Calendar.HOUR_OF_DAY, 1);
        tvEndDate.setText(dateFormat.format(calEnd.getTime()));
        tvEndTime.setText(timeFormat.format(calEnd.getTime()));

        // Set up click listeners
        tvStartDate.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        buttonSaveTask.setOnClickListener(this);
        buttonDeleteTask.setOnClickListener(this);

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedName = savedInstanceState.getString(KEY_NAME);
            editTextTaskName.setText(savedName);

            String savedStartDate = savedInstanceState.getString(KEY_START_DATE);
            tvStartDate.setText(savedStartDate);

            String savedStartTime = savedInstanceState.getString(KEY_START_TIME);
            tvStartTime.setText(savedStartTime);

            String savedEndDate = savedInstanceState.getString(KEY_END_DATE);
            tvEndDate.setText(savedEndDate);

            String savedEndTime = savedInstanceState.getString(KEY_END_TIME);
            tvEndTime.setText(savedEndTime);

            String savedRepeats = savedInstanceState.getString(KEY_REPEATS);
            setRepeatsValue(savedRepeats);

            String savedNotes = savedInstanceState.getString(KEY_NOTES);
            editTextNotes.setText(savedNotes);

        }

        // Listener for start date DatePicker. Gets date picked and displays to start date textview.
        // Resets end date to the same as start date.
        StartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                calStart.set(y, m, d);
                tvStartDate.setText(dateFormat.format(calStart.getTime()));
                calEnd.set(y, m, d);
                tvEndDate.setText(dateFormat.format(calEnd.getTime()));


            }
        };
        // Listener for start time TimePicker. Gets time picked and displays to start time textView.
        // Resets the end time to an hour after the start time.
        StartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                calStart.set(Calendar.HOUR_OF_DAY, h);
                calStart.set(Calendar.MINUTE, m);
                calEnd.set(Calendar.HOUR_OF_DAY, h+1);
                calEnd.set(Calendar.MINUTE, m);
                tvStartTime.setText(timeFormat.format(calStart.getTime()));
                tvEndTime.setText(timeFormat.format(calEnd.getTime()));

            }
        };
        // Listener for end date DatePicker. Gets date picked and displays to end date textview.
        EndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                calEnd.set(y, m, d);
                tvEndDate.setText(dateFormat.format(calEnd.getTime()));

            }
        };
        // Listener for end time TimePicker. Gets time picked and displays to end time textView.
        EndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                calEnd.set(Calendar.HOUR_OF_DAY, h);
                calEnd.set(Calendar.MINUTE, m);
                tvEndTime.setText(timeFormat.format(calEnd.getTime()));

            }
        };

    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_NAME, editTextTaskName.getText());
        outState.putCharSequence(KEY_START_TIME, tvStartTime.getText());
        outState.putCharSequence(KEY_START_DATE, tvStartDate.getText());
        outState.putCharSequence(KEY_END_TIME, tvEndTime.getText());
        outState.putCharSequence(KEY_END_DATE, tvEndDate.getText());
        outState.putCharSequence(KEY_REPEATS, spinner.getSelectedItem().toString());
        outState.putCharSequence(KEY_NOTES, editTextNotes.getText());
    }

    // Gets string array of spinner options. Compares the options to the repeat value of
    // template. Sets spinner selected value to that option.
    private void setRepeatsValue(String repeats) {
        String[] repeatStrings = this.getResources().getStringArray(R.array.repeat_options);
        for (int i = 0; i < repeatStrings.length; i++)
        {
            if (repeatStrings[i].equals(repeats)) {
                spinner.setSelection(i);
            }
        }
    }

    public boolean checkTimes() {

        Calendar now = Calendar.getInstance();
        // If start date/time is after end date/time user may not save task
        if (calStart.compareTo(calEnd) > 0) {
            Toast.makeText(this, "Start time must be before end time.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        // If start date/time is before now, user may not save task
        if (calStart.compareTo(now) < 0 ) {
            Toast.makeText(this, "Start time must be set to a future time.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }


    public void saveTask() {
        if (checkTimes()) {

            // Get values from layout
            String name = editTextTaskName.getText().toString();
            String startDate = dateFormat.format(calStart.getTime());
            String startTime = timeFormat.format(calStart.getTime());
            String endDate = dateFormat.format(calEnd.getTime());
            String endTime = timeFormat.format(calEnd.getTime());
            String repeats = String.valueOf(spinner.getSelectedItem());
            String notes = editTextNotes.getText().toString();

            // Create string that contains start date and time.
            // Convert to milliseconds for timestamp.
            String startDateTime = startDate + " " + startTime;
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
            Calendar c = Calendar.getInstance();
            try {
                Date start = df.parse(startDateTime);
                c.setTimeInMillis(start.getTime());

            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timestamp = c.getTimeInMillis();

            // if the task does not already have an alarmId, create a unique one.
            if (alarmId == 0) {
                alarmId = (int) System.currentTimeMillis();
            }

            ContentValues values = new ContentValues();

            values.put(QMContract.TaskEntry.KEY_NAME, name);
            values.put(QMContract.TaskEntry.KEY_START_DATE, startDate);
            values.put(QMContract.TaskEntry.KEY_START_TIME, startTime);
            values.put(QMContract.TaskEntry.KEY_END_DATE, endDate);
            values.put(QMContract.TaskEntry.KEY_END_TIME, endTime);
            values.put(QMContract.TaskEntry.KEY_REPEATS, repeats);
            values.put(QMContract.TaskEntry.KEY_NOTES, notes);
            values.put(QMContract.TaskEntry.KEY_ALARM_ID, alarmId);
            values.put(QMContract.TaskEntry.KEY_TIMESTAMP, timestamp);

            if (sourceClass.equals("TemplateActivity")) {
                // This is a NEW reminder, so insert a new reminder into the provider,
                // returning the content URI for the new reminder.
                newUri = getContentResolver().insert(QMContract.TaskEntry.CONTENT_URI, values);

                // Increment template usage value and update in database
                usageTemplate++;
                ContentValues templateValues = new ContentValues();
                templateValues.put(QMContract.TemplateEntry.KEY_USAGE, usageTemplate);
                getContentResolver().update(mCurrentReminderUri, templateValues, null, null);
                Log.d(TAG, "template usage: " + usageTemplate);

                // Get data for category that template belongs to
                String selection = QMContract.CategoryEntry._ID2 + " = " + idTempCat;
                Cursor cursor = getContentResolver().query(QMContract.CategoryEntry.CONTENT_URI, new String[] {
                        QMContract.CategoryEntry._ID2, QMContract.CategoryEntry.KEY_USAGE }, selection, null, null);


                if (cursor != null && cursor.moveToFirst()) {
                    int catId = cursor.getInt(cursor.getColumnIndex(QMContract.CategoryEntry._ID2));
                    int usageCat = cursor.getInt(cursor.getColumnIndex(QMContract.CategoryEntry.KEY_USAGE));

                    // Increment usage value for category and update in database
                    usageCat++;

                    ContentValues categoryValues = new ContentValues();
                    categoryValues.put(QMContract.CategoryEntry.KEY_USAGE, usageCat);
                    Uri categoryUri = ContentUris.withAppendedId(QMContract.CategoryEntry.CONTENT_URI, catId);
                    getContentResolver().update(categoryUri, categoryValues, null, null);
                    Log.d(TAG, "category usage: " + usageCat);
                }

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, "Something went wrong.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, "Task Saved!",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    setAlarm();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentReminderUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, "Something went wrong.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, "Task Updated!",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    setAlarm();
                }
            }
        }


    }

    private void setAlarm() {
        long interval = 0;
        // Set interval milliseconds based on repeat value
        switch (spinner.getSelectedItem().toString()) {
            case "Every Day":
                interval = 86400000;
                break;
            case "Every Other Day":
                interval = 172800000;
                break;
            case "Every Week":
                interval = 604800000;
                break;
            case "Every 2 Weeks":
                interval = 1209600000;
                break;
            case "Every Month":
                interval = 2629746000L;
                break;
            case "Every Year":
                interval = 31556952000L;
                break;
        }

        // Time in milliseconds the reminder should trigger
        long initialTime = calStart.getTimeInMillis();

        // Setup for alarm
        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // If this is a new task, pass new task data to alarm class
        if (newUri != null) {
            intent.setData(newUri);
        }
        // If this is an existing task, pass updated task data to alarm class
        else intent.setData(mCurrentReminderUri);

        // Also pass trigger time and interval data
        intent.putExtra("initialTime", initialTime);
        intent.putExtra("interval", interval);

        //create pendingintent again because intent was modified
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set alarm using method corresponding to sdk version
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, initialTime, pendingIntent);

            }
            else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, initialTime, pendingIntent);
            }

            Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "setAlarm: time" + calStart.getTime().toString() + " " + interval);

        }
    }

    public void deleteAlarm() {
        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }


    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            // Listener for start date textView
            case R.id.tvStartDate:
                // Create date picker
                DatePickerDialog dpdStart = new DatePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, StartDateSetListener, calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH), calStart.get(Calendar.DAY_OF_MONTH));
                if (dpdStart.getWindow() != null)
                    dpdStart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpdStart.show();
                break;
            // Listener for start time textView
            case R.id.tvStartTime:
                // Create time picker
                TimePickerDialog tpdStart = new TimePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, StartTimeSetListener, calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), false);
                if(tpdStart.getWindow() != null)
                    tpdStart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tpdStart.show();
                break;
            // Listener for end date textView
            case R.id.tvEndDate:
                // Create date picker
                DatePickerDialog dpdEnd = new DatePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, EndDateSetListener, calEnd.get(Calendar.YEAR), calEnd.get(Calendar.MONTH), calEnd.get(Calendar.DAY_OF_MONTH));
                if (dpdEnd.getWindow() != null)
                    dpdEnd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpdEnd.show();
                break;
            // Listener for end time textView
            case R.id.tvEndTime:
                // Create time picker
                TimePickerDialog tpdEnd = new TimePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, EndTimeSetListener, calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE), false);
                if (tpdEnd.getWindow() != null)
                    tpdEnd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tpdEnd.show();
                break;
            case R.id.buttonSaveTask:
                saveTask();
                break;
            case R.id.buttonDeleteTask:
                deleteTask();
                break;

        }


    }

    private void deleteTask() {

        // Only perform the delete if this is an existing reminder.
        if (!sourceClass.equals("TemplateActivity")) {
            // Call the ContentResolver to delete the reminder at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentreminderUri
            // content URI already identifies the reminder that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentReminderUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error Deleting Task.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Task Deleted!",
                        Toast.LENGTH_SHORT).show();
                deleteAlarm();
            }
        }

        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader;
        switch (i){
            // Get data for already existing task that has been selected
            case EXISTING_VEHICLE_LOADER:
                String[] projection = {
                        QMContract.TaskEntry._ID1,
                        QMContract.TaskEntry.KEY_NAME,
                        QMContract.TaskEntry.KEY_START_DATE,
                        QMContract.TaskEntry.KEY_START_TIME,
                        QMContract.TaskEntry.KEY_END_DATE,
                        QMContract.TaskEntry.KEY_END_TIME,
                        QMContract.TaskEntry.KEY_REPEATS,
                        QMContract.TaskEntry.KEY_NOTES,
                        QMContract.TaskEntry.KEY_ALARM_ID,
                        QMContract.TaskEntry.KEY_TIMESTAMP
                };

                // This loader will execute the ContentProvider's query method on a background thread
                loader = new CursorLoader(this,
                        mCurrentReminderUri,
                        projection,
                        null,
                        null,
                        null);
                break;
            case 1:
                // Get data for template that has been selected to create new task
                String[] projection2 = {
                        QMContract.TemplateEntry._ID3,
                        QMContract.TemplateEntry.KEY_NAME,
                        QMContract.TemplateEntry.KEY_REPEATS,
                        QMContract.TemplateEntry.KEY_TEMP_CAT,
                        QMContract.TemplateEntry.KEY_USAGE
                };
                loader = new CursorLoader(this,   // Parent activity context
                        mCurrentReminderUri,         // Query the content URI for the current reminder
                        projection2,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
                break;
            default:
                return null;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        switch (loader.getId()) {
            case 0:
                // Reading task data from cursor (Already existing task)
                if (cursor.moveToFirst()) {
                    int nameColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_NAME);
                    int startDateColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_START_DATE);
                    int startTimeColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_START_TIME);
                    int endDateColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_END_DATE);
                    int endTimeColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_END_TIME);
                    int repeatsColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_REPEATS);
                    int notesColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_NOTES);
                    int alarmIdColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_ALARM_ID);
                    int timestampColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_TIMESTAMP);

                    // Extract out the value from the Cursor for the given column index
                    String name = cursor.getString(nameColumnIndex);
                    String startDate = cursor.getString(startDateColumnIndex);
                    String startTime = cursor.getString(startTimeColumnIndex);
                    String endDate = cursor.getString(endDateColumnIndex);
                    String endTime = cursor.getString(endTimeColumnIndex);
                    String repeats = cursor.getString(repeatsColumnIndex);
                    String notes = cursor.getString(notesColumnIndex);
                    alarmId = cursor.getInt(alarmIdColumnIndex);
                    timestamp = cursor.getLong(timestampColumnIndex);


                    // Update the views with the task values from the database
                    editTextTaskName.setText(name);
                    tvStartDate.setText(startDate);
                    tvStartTime.setText(startTime);
                    tvEndDate.setText(endDate);
                    tvEndTime.setText(endTime);
                    setRepeatsValue(repeats);
                    editTextNotes.setText(notes);

                    // Set calendar objects with date and time values from database
                    try {
                        String endDateTime = endDate + " " + endTime;
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
                        calStart.setTimeInMillis(timestamp);
                        Date end = df.parse(endDateTime);
                        calEnd.setTimeInMillis(end.getTime());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                if (cursor.moveToFirst()){
                    // Reading template data from cursor (New task)
                    int nameColumnIndex = cursor.getColumnIndex(QMContract.TemplateEntry.KEY_NAME);
                    int repeatsColumnIndex = cursor.getColumnIndex(QMContract.TemplateEntry.KEY_REPEATS);
                    int idTempCatIndex = cursor.getColumnIndex(QMContract.TemplateEntry.KEY_TEMP_CAT);
                    int usageColumnIndex = cursor.getColumnIndex(QMContract.TemplateEntry.KEY_USAGE);

                    // Extract the values from the Cursor for the given column index
                    templateName = cursor.getString(nameColumnIndex);
                    templateRepeats = cursor.getString(repeatsColumnIndex);
                    idTempCat = cursor.getInt(idTempCatIndex);
                    usageTemplate = cursor.getInt(usageColumnIndex);

                    // Update the views with the template values from the database
                    editTextTaskName.setText(templateName);
                    setRepeatsValue(templateRepeats);
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}