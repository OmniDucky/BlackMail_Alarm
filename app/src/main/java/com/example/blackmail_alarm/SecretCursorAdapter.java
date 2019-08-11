package com.example.blackmail_alarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.blackmail_alarm.data.SecretContract;

public class SecretCursorAdapter extends CursorAdapter {
    public SecretCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_secret, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleTextView = (TextView) view.findViewById(R.id.title_secret);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary_secret);

        int titleColumnIndex = cursor.getColumnIndex(SecretContract.SecretEntry.COLUMN_SECRET_TITLE);
        int contentColumnIndex = cursor.getColumnIndex(SecretContract.SecretEntry.COLUMN_SECRET_CONTENT);

        String title = cursor.getString(titleColumnIndex);
        String content = cursor.getString(contentColumnIndex);

        titleTextView.setText(title);
        summaryTextView.setText(content);
    }
}
