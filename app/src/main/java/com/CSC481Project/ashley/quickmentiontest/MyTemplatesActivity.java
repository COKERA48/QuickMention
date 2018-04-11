package com.CSC481Project.ashley.quickmentiontest;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class MyTemplatesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final int VEHICLE_LOADER = 0;
    SimpleCursorAdapter adapter;
    AlertDialog.Builder alertDialogBuilder;
    EditText editTextName;
    Spinner spinner;
    boolean newTemplate;
    Uri currentVehicleUri;
    View promptView;
    String taskName, repeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_templates);

        Button buttonAddTemplate = findViewById(R.id.buttonAddTemplate);
        buttonAddTemplate.setOnClickListener(this);
        ListView listViewMyTemplates = findViewById(R.id.listViewMyTemplates);




        adapter = new SimpleCursorAdapter(this,
                R.layout.single_row_template,
                null,
                new String[] { QMContract.TemplateEntry.KEY_NAME },
                new int[] { R.id.textViewTemplateName }, 0);

        listViewMyTemplates.setAdapter(adapter);

        listViewMyTemplates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                newTemplate = false;
                currentVehicleUri = ContentUris.withAppendedId(QMContract.TemplateEntry.CONTENT_URI, id);

                Cursor cursor = getContentResolver().query(currentVehicleUri, new String[] {
                        QMContract.TemplateEntry.KEY_NAME, QMContract.TemplateEntry.KEY_REPEATS }, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    taskName = cursor.getString(cursor
                            .getColumnIndex(QMContract.TemplateEntry.KEY_NAME));

                    repeats = cursor.getString(cursor.getColumnIndex(QMContract.TemplateEntry.KEY_REPEATS));

                    cursor.close();
                }
                showInputDialog();
            }
        });
        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                QMContract.TemplateEntry._ID3,
                QMContract.TemplateEntry.KEY_NAME,
                QMContract.TemplateEntry.KEY_REPEATS,
                QMContract.TemplateEntry.KEY_TEMP_CAT


        };

        String selection = "(" + QMContract.TemplateEntry.KEY_TEMP_CAT + " = '" + 4 + "')";

        return new CursorLoader(this,   // Parent activity context
                QMContract.TemplateEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                selection,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public void showInputDialog() {
        alertDialogBuilder = new AlertDialog.Builder(MyTemplatesActivity.this);



        LayoutInflater layoutInflater = LayoutInflater.from(MyTemplatesActivity.this);
        promptView = layoutInflater.inflate(R.layout.template_input_dialog, null);

        alertDialogBuilder.setView(promptView);

        editTextName = (EditText) promptView.findViewById(R.id.editTextTempName);
        spinner = promptView.findViewById(R.id.spinner2);
        if (!newTemplate) {
            editTextName.setText(taskName);
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
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ContentValues values = new ContentValues();
                        values.put(QMContract.TemplateEntry.KEY_NAME, editTextName.getText().toString());
                        values.put(QMContract.TemplateEntry.KEY_REPEATS, String.valueOf(spinner.getSelectedItem()));
                        values.put(QMContract.TemplateEntry.KEY_TEMP_CAT, 4);
                        if(newTemplate) {
                            getContentResolver().insert(QMContract.TemplateEntry.CONTENT_URI, values);
                        } else getContentResolver().update(currentVehicleUri, values, null, null);
                        editTextName.setText("");
                        spinner.setSelection(0);

                    }
                })


                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        if(!newTemplate) {
            alertDialogBuilder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getContentResolver().delete(currentVehicleUri, null, null);
                }
            });
        }
        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonAddTemplate:
                newTemplate = true;
                showInputDialog();

        }
    }
}
