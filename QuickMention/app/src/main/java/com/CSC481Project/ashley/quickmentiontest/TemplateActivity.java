package com.CSC481Project.ashley.quickmentiontest;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class TemplateActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "TemplateActivity";
    private int catId;
    private String catName, templateName, repeats;
    private Integer catIcon;
    private int createdByUser;
    private ImageView image;
    private static final int VEHICLE_LOADER = 0;
    private ContentResolver mResolver;
    private Cursor cursorTemplates;
    private TemplateListAdapter customAdapter;
    private boolean editMode = false, newTemplate = false;
    private Uri currentVehicleUri;
    private MenuItem addTemplate, editTemplate, doneEditing;
    private ListView listViewTemplates;
    private EditText editTextName;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        setTitle("Choose Template");

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbarTemplate);
        setSupportActionBar(toolbar);

        // Receive data from category that has been selected
        Intent intent = getIntent();
        Uri mCurrentReminderUri = intent.getData();

        // Create cursor to extract category data
        mResolver = this.getContentResolver();
        Cursor cursor = null;
        if (mCurrentReminderUri != null) {
            cursor = mResolver.query(
                    mCurrentReminderUri,
                    new String[] {
                    QMContract.CategoryEntry._ID, QMContract.CategoryEntry.KEY_NAME, QMContract.CategoryEntry.KEY_ICON },
                    null,
                    null,
                    null);
        }

        // Save category data to be used for display
        if (cursor != null && cursor.moveToFirst()) {
            catId = cursor.getInt(cursor
                    .getColumnIndex(QMContract.CategoryEntry._ID));
            catName = cursor.getString(cursor.getColumnIndex(QMContract.CategoryEntry.KEY_NAME));
            catIcon = cursor.getInt(cursor.getColumnIndex(QMContract.CategoryEntry.KEY_ICON));
            cursor.close();
        }
        // Display category icon and name at the top of activity
        image = findViewById(R.id.image);
        image.setImageResource(catIcon);
        TextView textViewCatName = findViewById(R.id.textViewCatName);
        textViewCatName.setText(catName);

        // Create cursor to hold templates that belong to the selected category
        String selection = "(" + QMContract.TemplateEntry.KEY_TEMP_CAT + " = '" + catId + "')";
        cursorTemplates = mResolver.query(QMContract.TemplateEntry.CONTENT_URI,
                new String [] { QMContract.TemplateEntry._ID3, QMContract.TemplateEntry.KEY_NAME,
                        QMContract.TemplateEntry.KEY_CREATED_BY_USER, QMContract.TemplateEntry.KEY_TEMP_CAT },
                selection,              // Only select templates matching selected category
                null,
                null);

        // Create list adapter with this cursor
        customAdapter = new TemplateListAdapter(getApplicationContext(), cursorTemplates, editMode);


        listViewTemplates = findViewById(R.id.listViewTemplates);
        listViewTemplates.setAdapter(customAdapter);

        listViewTemplates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                currentVehicleUri = ContentUris.withAppendedId(QMContract.TemplateEntry.CONTENT_URI, id);

                // Templates are not being edited and user has selected one
                if (!editMode) {
                    // CreateTaskActivity is started and the chosen template data is sent
                    Intent intent = new Intent(TemplateActivity.this, CreateTaskActivity.class);

                    // Set the URI on the data field of the intent
                    intent.setData(currentVehicleUri);
                    intent.putExtra("ClassName", "TemplateActivity");
                    startActivity(intent);
                }
                // User has selected a template to edit
                else {
                    // Extract data from template selected
                    Cursor cursor = getContentResolver().query(currentVehicleUri,
                            new String[]{ QMContract.TemplateEntry._ID3, QMContract.TemplateEntry.KEY_NAME,
                                    QMContract.TemplateEntry.KEY_REPEATS, QMContract.TemplateEntry.KEY_TEMP_CAT,
                                    QMContract.TemplateEntry.KEY_CREATED_BY_USER},
                            null,
                            null,
                            null);
                    // Save template data for edit dialog window
                    if (cursor != null && cursor.moveToFirst()) {
                        templateName = cursor.getString(cursor
                                .getColumnIndex(QMContract.TemplateEntry.KEY_NAME));
                        createdByUser = cursor.getInt(cursor.getColumnIndex(QMContract.TemplateEntry.KEY_CREATED_BY_USER));
                        repeats = cursor.getString(cursor.getColumnIndex(QMContract.TemplateEntry.KEY_REPEATS));


                    }
                    // An existing template has been selected, so a new one is not being created
                    newTemplate = false;
                    showInputDialog();
                }

            }
        });

        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);

    }

    public boolean onCreateOptionsMenu (Menu menu)

    {
        getMenuInflater().inflate(R.menu.toolbar_template, menu);
        addTemplate = menu.findItem(R.id.action_add);
        editTemplate = menu.findItem(R.id.action_edit);
        doneEditing = menu.findItem(R.id.action_done);
        addTemplate.setVisible(false);
        doneEditing.setVisible(false);

        return true;

    }



    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {

            case R.id.action_edit:
                addTemplate.setVisible(true);
                editMode = true;
                editTemplate.setVisible(false);
                doneEditing.setVisible(true);

                break;
            case R.id.action_add:
                newTemplate = true;
                showInputDialog();
                break;
            case R.id.action_done:
                editMode = false;
                addTemplate.setVisible(false);
                editTemplate.setVisible(true);
                doneEditing.setVisible(false);
        }
        customAdapter = new TemplateListAdapter(getApplicationContext(), cursorTemplates, editMode);
        listViewTemplates.setAdapter(customAdapter);

        return super.onOptionsItemSelected(item);



    }

    private void showInputDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TemplateActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(TemplateActivity.this);
        View promptView = layoutInflater.inflate(R.layout.template_input_dialog, null);
        alertDialogBuilder.setView(promptView);

        // Setup layout variables
        editTextName = promptView.findViewById(R.id.editTextTempName);
        TextView textViewName = promptView.findViewById(R.id.textViewName);
        spinner = promptView.findViewById(R.id.spinner2);
        TextView textViewTitle = promptView.findViewById(R.id.textViewTitle);

        // An already existing template is being edited
        if (!newTemplate) {
            // If the template was not created by the user, only the repeats value can be edited
            // The name is displayed as a textView and will not allow editing
            if (createdByUser == 0) {
                editTextName.setVisibility(View.GONE);
                textViewName.setVisibility(View.GONE);
                textViewTitle.setText(templateName);

            }
            // If the task was created by the user, the name and repeats value can be edited
            else {
                editTextName.setText(templateName);
                textViewTitle.setVisibility(View.GONE);
            }

            // Set spinner value to the repeat value of selected template
            String[] repeatStrings = getResources().getStringArray(R.array.repeat_options);
            for (int i = 0; i < repeatStrings.length; i++)
            {
                if (repeatStrings[i].equals(repeats)) {
                    spinner.setSelection(i);
                }
            }
        }

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                // Save button
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Save values to inserted to database
                        ContentValues values = new ContentValues();

                        values.put(QMContract.TemplateEntry.KEY_REPEATS, String.valueOf(spinner.getSelectedItem()));


                        // If this is a new template, get name input,  set created by user to 1 (true), and set category foreign key
                        if(newTemplate) {
                            values.put(QMContract.TemplateEntry.KEY_NAME, editTextName.getText().toString());
                            values.put(QMContract.TemplateEntry.KEY_CREATED_BY_USER, 1);
                            values.put(QMContract.TemplateEntry.KEY_TEMP_CAT, catId);
                            // insert new template to db
                            mResolver.insert(QMContract.TemplateEntry.CONTENT_URI, values);
                        }
                        // If existing template
                        else  {
                            // If created by user, get updated name
                            if (createdByUser == 1) {
                                values.put(QMContract.TemplateEntry.KEY_NAME, editTextName.getText().toString());
                            }
                            // update changes to template
                            mResolver.update(currentVehicleUri, values, null, null);
                        }

                    }
                })


                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // If the template was created by the user, they may delete it
        if(createdByUser == 1) {
            alertDialogBuilder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mResolver.delete(currentVehicleUri, null, null);
                }
            });
        }
        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                QMContract.TemplateEntry._ID3,
                QMContract.TemplateEntry.KEY_NAME,
                QMContract.TemplateEntry.KEY_REPEATS,
                QMContract.TemplateEntry.KEY_CREATED_BY_USER,
                QMContract.TemplateEntry.KEY_TEMP_CAT,
                QMContract.TemplateEntry.KEY_USAGE


        };

        String selection = "(" + QMContract.TemplateEntry.KEY_TEMP_CAT + " = '" + catId + "')";

        return new CursorLoader(this,
                QMContract.TemplateEntry.CONTENT_URI,
                projection,
                selection,                      // select templates that belong to chosen category
                null,
                QMContract.TemplateEntry.KEY_USAGE + " DESC");      // sort templates by usage
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        customAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        customAdapter.swapCursor(null);
    }
}
