package com.example.blackmail_alarm;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.blackmail_alarm.data.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactManager {
    private Context mContext;
    private List<Contact> mListContact;

    public ContactManager(Context context)
    {
        mContext = context;
        getContactData();
        //Collections.sort(mListContact);
    }

    private void getContactData()
    {
        mListContact = new ArrayList<>();

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };
        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,projection,null,null,null);
        phones.moveToFirst();
        int nameColumnIndex =phones.getColumnIndex(projection[0]);
        int numberColumnIndex = phones.getColumnIndex(projection[1]);
        while (phones.moveToNext())
        {
            String name = phones.getString(nameColumnIndex);
            String number = phones.getString(numberColumnIndex);
            mListContact.add(new Contact(name,number));
        }
        phones.close();
    }
    public List<Contact> getListContact() {
        return mListContact;
    }
}
