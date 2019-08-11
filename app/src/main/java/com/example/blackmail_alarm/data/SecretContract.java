package com.example.blackmail_alarm.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class SecretContract {
    private SecretContract(){}
    public static final String CONTENT_AUTHORITY = "com.example.blackmail_alarm";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SECRETS = "secrets";

    public static final class SecretEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SECRETS);
        //a lists of secrets
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SECRETS;
        // 1 secret
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SECRETS;
        public final static String TABLE_NAME = "secrets";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SECRET_TITLE ="title";
        public final static String COLUMN_SECRET_CONTENT ="content";
        public final static String COLUMN_SECRET_WTIH ="person";
    }
}
