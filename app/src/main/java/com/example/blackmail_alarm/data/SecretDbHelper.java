package com.example.blackmail_alarm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SecretDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = SecretDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "secret_repository.db";
    private static final int DATABASE_VERSION = 1;
    public SecretDbHelper(Context context) {super(context,DATABASE_NAME,null,DATABASE_VERSION);}
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + SecretContract.SecretEntry.TABLE_NAME + " ("
                + SecretContract.SecretEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SecretContract.SecretEntry.COLUMN_SECRET_TITLE + " TEXT NOT NULL, "
                + SecretContract.SecretEntry.COLUMN_SECRET_CONTENT + " TEXT NOT NULL, "
                + SecretContract.SecretEntry.COLUMN_SECRET_WTIH  + " TEXT );";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
