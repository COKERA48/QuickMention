package com.CSC481Project.ashley.quickmentiontest;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;



public class CategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CategoryActivity";
    SimpleCursorAdapter adapter;

    private static final int VEHICLE_LOADER = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setTitle("Choose Category");

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbarCategory);
        setSupportActionBar(toolbar);

        ListView listViewCategories = findViewById(R.id.listViewCategories);


        // Gets category data to be displayed to listView
        adapter = new SimpleCursorAdapter(this,
                R.layout.single_row_category,
                null,
                new String[] { QMContract.CategoryEntry.KEY_ICON,QMContract.CategoryEntry.KEY_NAME },
                new int[] { R.id.icon, R.id.textView }, 0);

        listViewCategories.setAdapter(adapter);
        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // When a category is clicked, it will start the template activity and pass
                // the selected category's data

                Intent intent = new Intent(CategoryActivity.this, TemplateActivity.class);

                Uri currentVehicleUri = ContentUris.withAppendedId(QMContract.CategoryEntry.CONTENT_URI, id);

                intent.setData(currentVehicleUri);

                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);


    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                QMContract.CategoryEntry._ID2,
                QMContract.CategoryEntry.KEY_NAME,
                QMContract.CategoryEntry.KEY_ICON,
                QMContract.CategoryEntry.KEY_USAGE


        };

        return new CursorLoader(this,
                QMContract.CategoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                QMContract.CategoryEntry.KEY_USAGE + " DESC");   // Sort categories by usage
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
