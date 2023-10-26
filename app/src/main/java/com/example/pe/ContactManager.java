package com.example.pe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.example.pe.entity.Contact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactManager {
    private Context mContext;
    private List<Contact> mListContact;


    public ContactManager(Context context){
        mContext= context;
        getContactData();
    }

    private void getContactData() {
        mListContact = new ArrayList<>();
        String[] projections = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        };
        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projections, null, null, null);

        if (phones != null) {
            int idIndex = phones.getColumnIndex(projections[0]);
            int phoneIndex = phones.getColumnIndex(projections[1]);
            int imageURIIndex = phones.getColumnIndex(projections[2]);

            while (phones.moveToNext()) {
                int id = phones.getInt(idIndex);
                String firstName = getFirstName(id);
                String lastName = getLastName(id);
                String email = getEmail(id);
                String address = getAddress(id);
                String phone = phones.getString(phoneIndex);
                String company = getCompany(id);
                String imageURI = phones.getString(imageURIIndex);
                Bitmap photo = getPhotoURI(imageURI);
                mListContact.add(new Contact(id, firstName, lastName, email, address, phone, company, imageURI));

                Log.d("checkgetAll", "getContactData: " + (new Contact(id, firstName, lastName, email, address, phone, company, imageURI)));
            }

            phones.close();
        }
    }
    @SuppressLint("Range")
    private String getFirstName(int contactId) {
        String firstName = null;
        Cursor cursor = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME},
                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE},
                null);

        if (cursor != null && cursor.moveToFirst()) {
            firstName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
            cursor.close();
        }
        return firstName;
    }

    @SuppressLint("Range")
    private String getLastName(int contactId) {
        String lastName = null;
        Cursor cursor = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME},
                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE},
                null);

        if (cursor != null && cursor.moveToFirst()) {
            lastName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
            cursor.close();
        }
        return lastName;
    }
    @SuppressLint("Range")
    private String getEmail(int contactId) {
        String email = null;
        Cursor cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS},
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{String.valueOf(contactId)},
                null);
        if (cursor != null && cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            cursor.close();
        }
        return email;
    }
    @SuppressLint("Range")

    private String getAddress(int contactId) {
        String address = null;
        Cursor cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS},
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?",
                new String[]{String.valueOf(contactId)},
                null);
        if (cursor != null && cursor.moveToFirst()) {
            address = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
            cursor.close();
        }
        return address;
    }
    @SuppressLint("Range")

    private String getCompany(int contactId) {
        String company = null;
        Cursor cursor = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Organization.COMPANY},
                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE},
                null);

        if (cursor != null && cursor.moveToFirst()) {
            company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
            cursor.close();
        }
        return company;
    }
    public List<Contact> getListContact() {
        return mListContact;
    }

    public void setListContact(List<Contact> mListContact) {
        this.mListContact = mListContact;
    }

    private Bitmap getPhotoURI(String imageURI) {
        if(imageURI != null){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(imageURI));
                return bitmap;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean deleteContact(int contactId) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
        int rowsDeleted = mContext.getContentResolver().delete(contactUri, null, null);
        return rowsDeleted > 0;
    }

    //Nhet cai nay vao ham delete by id tren firebase la xoa tren local luon nhe :>
    //mContactManager.deleteContact(idContact);
}
