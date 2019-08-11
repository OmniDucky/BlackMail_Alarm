package com.example.blackmail_alarm;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blackmail_alarm.ContactManager;
import com.example.blackmail_alarm.R;
import com.example.blackmail_alarm.data.Contact;
import com.example.blackmail_alarm.data.SecretContract;

import java.util.ArrayList;
import java.util.List;


public class SecretEditor extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
    private Uri mCurrentSecretUri;
    private static final int EXISTING_SECRET_LOADER = 0;

    private EditText mTitleEditText;

    private EditText mContentEditText;
    private AutoCompleteTextView mAutoComplete;
    private String mPerson = null;
    private boolean mSecretHasChanged = false;
    List<String> listName = new ArrayList<>();
    List<String> listNumber = new ArrayList<>();
    private ContactManager mContactManager;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the msecretHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSecretHasChanged = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_editor);

        mTitleEditText = (EditText) findViewById(R.id.edit_secret_title);
        mContentEditText = (EditText) findViewById(R.id.edit_secret_content);
        mAutoComplete = findViewById(R.id.auto_complete);

        mContactManager = new ContactManager(this);
        List<Contact> listContact= mContactManager.getListContact();
        listName.add("Unknow");listNumber.add("0");
        for (int i=0;i <listContact.size();++i)
        {
            listName.add(listContact.get(i).getName());
            listNumber.add(listContact.get(i).getPhoneNumber());
        }
        Intent intent = getIntent();
        mCurrentSecretUri = intent.getData();
        setupAutoComplete();
        if (mCurrentSecretUri == null) {

            setTitle(getString(R.string.editor_activity_title_new_secret));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a secret that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_secret));

            // Initialize a loader to read the secret data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_SECRET_LOADER,null,this);
        }

        // Find all relevant views that we will need to read user input from

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mTitleEditText.setOnTouchListener(mTouchListener);
        mContentEditText.setOnTouchListener(mTouchListener);
        mAutoComplete.setOnTouchListener(mTouchListener);
    }
    public void setupAutoComplete()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listName);
        mAutoComplete.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor_secret,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentSecretUri == null)
        {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save secret to database
                saveSecret();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the secret hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mSecretHasChanged) {
                    NavUtils.navigateUpFromSameTask(SecretEditor.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(SecretEditor.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveSecret()
    {
        //read input
        String titleString = mTitleEditText.getText().toString().trim();
        String contentString = mContentEditText.getText().toString().trim();
        mPerson = mAutoComplete.getText().toString().trim();
        if (mCurrentSecretUri == null && TextUtils.isEmpty(titleString) && TextUtils.isEmpty(contentString)
                && mPerson == null)
        {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(SecretContract.SecretEntry.COLUMN_SECRET_TITLE,titleString);
        values.put(SecretContract.SecretEntry.COLUMN_SECRET_CONTENT,contentString);
        values.put(SecretContract.SecretEntry.COLUMN_SECRET_WTIH,mPerson);
        if (mCurrentSecretUri == null) {
            // This is a NEW secret, so insert a new secret into the provider,
            // returning the content URI for the new secret.
            Uri newUri = getContentResolver().insert(SecretContract.SecretEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_secret_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_secret_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING secret, so update the secret with content URI: mCurrentsecretUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentsecretUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentSecretUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_secret_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_secret_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void deleteSecret() {
        // Only perform the delete if this is an existing secret.
        if (mCurrentSecretUri != null) {
            // Call the ContentResolver to delete the secret at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentsecretUri
            // content URI already identifies the secret that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentSecretUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_secret_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_secret_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the secret.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the secret.
                deleteSecret();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the secret.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        // If the secret hasn't changed, continue with handling back button press
        if (!mSecretHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                SecretContract.SecretEntry._ID,
                SecretContract.SecretEntry.COLUMN_SECRET_TITLE,
                SecretContract.SecretEntry.COLUMN_SECRET_CONTENT,
                SecretContract.SecretEntry.COLUMN_SECRET_WTIH};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentSecretUri,         // Query the content URI for the current secret
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(SecretContract.SecretEntry.COLUMN_SECRET_TITLE);
            int contentColumnIndex = cursor.getColumnIndex(SecretContract.SecretEntry.COLUMN_SECRET_CONTENT);
            int personColumnIndex = cursor.getColumnIndex(SecretContract.SecretEntry.COLUMN_SECRET_WTIH);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String content = cursor.getString(contentColumnIndex);
            String person = cursor.getString(personColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleEditText.setText(title);
            mContentEditText.setText(content);
            mAutoComplete.setText(person);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleEditText.setText("");
        mContentEditText.setText("");
        mAutoComplete.setText("");
    }
}
