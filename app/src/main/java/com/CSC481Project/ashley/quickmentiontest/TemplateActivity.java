package com.CSC481Project.ashley.quickmentiontest;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class TemplateActivity extends AppCompatActivity {
    private static final String TAG = "TemplateActivity";
    DatabaseHelper dbHelper;
    private ListView listViewTemplates;
    private int catId;
    private String catName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        setTitle("Choose Template");

        // Gets chosen category id and name
        Bundle bundle = getIntent().getExtras();
        if (bundle != null ) {
            catId = bundle.getInt("categoryID");
            catName = bundle.getString("categoryName");     // May use this at top of activity as a header
        }


        listViewTemplates = (ListView) findViewById(R.id.listViewTemplates);
        dbHelper = new DatabaseHelper(this);

        populateListView();

        listViewTemplates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Gets template name that is chosen by user from listView
                String tempName = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "TemplateActivity: onItemClick: You've clicked on: " + tempName);

                // Gets repeat value for chosen template name
                Cursor tempData = dbHelper.getTemplateRepeatValue(tempName);
                String repeats = null;
                while(tempData.moveToNext()) {
                    repeats = tempData.getString(0);
                }
                // If not null, template name and repeat value are passed to CreateTaskActivity
                if (repeats != null) {
                    Log.d(TAG, "TemplateActivity: onItemClick: the repeat value is: " + repeats);
                    Intent intent = new Intent(TemplateActivity.this, CreateTaskActivity.class);
                    intent.putExtra("templateRepeats", repeats);
                    intent.putExtra("templateName", tempName);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(TemplateActivity.this, "No repeat value associated with that template.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying Templates to ListView");



        // Pulls templates from database for chosen category
        Cursor data = dbHelper.getTemplates(catId);
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()) {
            listData.add(data.getString(1));
        }
        // Displays templates to listView
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listViewTemplates.setAdapter(adapter);
    }
}
