package com.example.pe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pe.entity.Contact;
import com.example.pe.helpers.FirebaseDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ContactManager mContactManager;

    List<Contact> contacts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mContactManager = new ContactManager(this);
//        contacts = mContactManager.getListContact();
//        for (Contact c:contacts) {
//            Log.d("contactCheck", "onCreate: "+c.toString());
//        }
        RecyclerView rec = findViewById(R.id.rec_list);
        rec.setLayoutManager(new LinearLayoutManager(this));
        final FirebaseDatabaseHelper dbHelper = new FirebaseDatabaseHelper();
        dbHelper.getAllContacts(new FirebaseDatabaseHelper.OnAllContactsFetchedListener() {
            @Override
            public void onAllContactsFetched(List<Contact> contact) {
//                rec.setAdapter(new ContactCardAdapter(contacts));
                contacts.clear();
                contacts.addAll(contact);
                for (Contact c:contact) {
                    Log.d("contactCheck2", "onCreate: "+c.toString());
                }
                rec.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onAllContactsFetchError(String errorMessage) {
                Log.e("FirebaseFetchError", errorMessage);
            }
        });


        rec.setAdapter(new ContactCardAdapter(contacts, new ContactCardAdapter.IclickItem() {
            @Override
            public void getContact(int id) {
                Log.d("hehe","Id : "+id);
                (new FirebaseDatabaseHelper()).deleteContact(id);
                //mContactManager.deleteContact(id);
                dbHelper.getAllContacts(new FirebaseDatabaseHelper.OnAllContactsFetchedListener() {
                    @Override
                    public void onAllContactsFetched(List<Contact> contact) {
//                rec.setAdapter(new ContactCardAdapter(contacts));
                        contacts.clear();
                        contacts.addAll(contact);
                        for (Contact c:contact) {
                            Log.d("contactCheck2", "onCreate: "+c.toString());
                        }
                        rec.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onAllContactsFetchError(String errorMessage) {
                        Log.e("FirebaseFetchError", errorMessage);
                    }
                });
            }
        }));


        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddContactActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacts = mContactManager.getListContact();
                if (contacts != null) {
                    dbHelper.uploadAllContacts(contacts, new FirebaseDatabaseHelper.OnContactsUploadedListener() {
                        @Override
                        public void onContactsUploaded() {
                            Toast.makeText(MainActivity.this, "Contacts loaded successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onUploadError(String errorMessage) {
                            Toast.makeText(MainActivity.this, "Contacts loaded error", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "No contacts to sync", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchValue = findViewById(R.id.editTextText);
                String sValue = searchValue.getText().toString();
                Log.d("searchValue", "onClick: " + sValue);
                dbHelper.getContactsBySearch(sValue, new FirebaseDatabaseHelper.OnContactsBySearchListener() {
                    @Override
                    public void onContactsBySearchFetched(List<Contact> contact) {
                        contacts.clear();
                        contacts.addAll(contact);
                        for (Contact c:contact) {
                            Log.d("contactCheck2", "onCreate: "+c.toString());
                        }
                        rec.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onContactsBySearchFetchError(String errorMessage) {

                    }
                });
            }
        });
    }
    public List<Contact> getFromLocal(){
        return(new ContactManager(this)).getListContact();
    }
}