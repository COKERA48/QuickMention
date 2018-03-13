package com.CSC481Project.ashley.quickmentiontest;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ListView listViewUpcomingTasks;
    private Button buttonNewTask;
    private Button buttonSignOut;
    private static final String TAG = "MainActivity";
    DatabaseHelper dbHelper;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");


        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigation = (NavigationView)findViewById(R.id.navigationView);
        navigation.setNavigationItemSelectedListener(this);
        listViewUpcomingTasks = (ListView) findViewById(R.id.listViewUpcomingTasks);

        // == Not working for me for some reason ==
        // listViewUpcomingTasks.setLayoutParams(new LinearLayout.LayoutParams
        //                                      (LinearLayout.LayoutParams.FILL_PARENT, 4));



        dbHelper = new DatabaseHelper(this);
        populateListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newTask:
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                return true;
            case R.id.calendar:
                startActivity(new Intent(getApplicationContext(), CalendarViewActivity.class));
                return true;

        }
        return false;
    }

    // Displays tasks to listView
    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data to ListView");

        Cursor data = dbHelper.getTasks();

        ArrayList<String> taskNames = new ArrayList<>();
        ArrayList<String> taskDates = new ArrayList<>();
        ArrayList<String> taskTimes = new ArrayList<>();
        while(data.moveToNext()) {
            taskNames.add(data.getString(1));
            taskDates.add(data.getString(2));
            taskTimes.add(data.getString(3));
        }


        TaskListAdapter adapter = new TaskListAdapter(this, taskNames, taskDates, taskTimes);
        listViewUpcomingTasks.setAdapter(adapter);
    }


}
