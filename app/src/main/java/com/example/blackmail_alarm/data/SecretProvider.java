package com.example.blackmail_alarm.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Switch;

public class SecretProvider extends ContentProvider {
    public static final String LOG_TAG = SecretProvider.class.getSimpleName();
    SecretDbHelper mDbHelper;
    /** URI matcher code for the content URI for the secrets table */
    private static final int SECRETS = 1;
    /** URI matcher code for the content URI for a single secret in the secrets table */
    private static final int SECRET_ID = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(SecretContract.CONTENT_AUTHORITY, SecretContract.PATH_SECRETS,SECRETS );
        sUriMatcher.addURI(SecretContract.CONTENT_AUTHORITY, SecretContract.PATH_SECRETS + "/#", SECRET_ID);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new SecretDbHelper(getContext());
        return true;
    }
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder)
    {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SECRETS:
                cursor = database.query(SecretContract.SecretEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SECRET_ID:
                selection = SecretContract.SecretEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(SecretContract.SecretEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SECRETS:
                return insertSecret(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertSecret(Uri uri,ContentValues values)
    {
        String title = values.getAsString(SecretContract.SecretEntry.COLUMN_SECRET_TITLE);
        if (title == null)
        {
            throw new IllegalArgumentException("Secrete requires a title");
        }
        String content = values.getAsString(SecretContract.SecretEntry.COLUMN_SECRET_CONTENT);
        if (content == null)
        {
            throw new IllegalArgumentException("Secrete requires a content");
        }
        String person = values.getAsString(SecretContract.SecretEntry.COLUMN_SECRET_WTIH);
        if (person == null)
        {
            throw new IllegalArgumentException("Secrete requires a content");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(SecretContract.SecretEntry.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection,  String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
