package com.example.blackmail_alarm;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.widget.CursorAdapter;

import com.example.blackmail_alarm.data.SecretContract;

public class SecretRepository extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
    SecretCursorAdapter mCursorAdapter;
    private static final int SECRET_LOADER = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_repository);
        ListView secretListView = findViewById(R.id.list);
        View emptyView =  findViewById(R.id.empty_view);
        secretListView.setEmptyView(emptyView);

        mCursorAdapter = new SecretCursorAdapter(this,null);
        secretListView.setAdapter(mCursorAdapter);

        secretListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(SecretRepository.this, SecretEditor.class);

                // Form the content URI that represents the specific secret that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link secretEntry#CONTENT_URI}.
                Uri currentSecretUri = ContentUris.withAppendedId(SecretContract.SecretEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentSecretUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(SECRET_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                SecretContract.SecretEntry._ID,
                SecretContract.SecretEntry.COLUMN_SECRET_TITLE,
                SecretContract.SecretEntry.COLUMN_SECRET_CONTENT};
        return new CursorLoader(this,   // Parent activity context
                SecretContract.SecretEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_secret,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_secret_data:
                insertSecret();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllSecrets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void insertSecret()
    {
        Intent intent = new Intent(SecretRepository.this, SecretEditor.class);
        startActivity(intent);
    }
    private void deleteAllSecrets() {
        int rowsDeleted = getContentResolver().delete(SecretContract.SecretEntry.CONTENT_URI, null, null);
        Log.v("SecretRepository", rowsDeleted + " rows deleted from secret database");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
