package com.example.pe.helpers;

import com.example.pe.ContactManager;
import com.example.pe.entity.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private DatabaseReference databaseReference;
    private ContactManager mContactManager;

    public FirebaseDatabaseHelper() {
        // Connect Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addContact(Contact contact) {
        String contactId = String.valueOf(contact.id);
        databaseReference.child("contacts").child(contactId).setValue(contact);
    }

    public void updateContact(Contact contact) {
        String contactId = String.valueOf(contact.id);
        databaseReference.child("contacts").child(contactId).setValue(contact);
    }

    public void deleteContact(int contactId) {
        databaseReference.child("contacts").child(String.valueOf(contactId)).removeValue();
    }

    public void getContactById(int contactId, final OnContactByIdFetchedListener listener) {
        String contactIdString = String.valueOf(contactId);
        DatabaseReference contactRef = databaseReference.child("contacts").child(contactIdString);
        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                if (contact != null) {
                    listener.onContactByIdFetched(contact);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onContactByIdFetchError(databaseError.getMessage());
            }
        });
    }

    public interface OnContactByIdFetchedListener {
        void onContactByIdFetched(Contact contact);
        void onContactByIdFetchError(String errorMessage);
    }

    public void getContactsBySearch(final String searchText, final OnContactsBySearchListener listener) {
        databaseReference.child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Contact> matchedContacts = new ArrayList<>();

                for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    Contact contact = contactSnapshot.getValue(Contact.class);
                    if (contact != null) {
                        String firstName = contact.getFirstName();
                        String lastName = contact.getLastName();
                        String email = contact.getEmail();

                        if ((firstName != null && firstName.toLowerCase().contains(searchText.toLowerCase())) ||
                                (lastName != null && lastName.toLowerCase().contains(searchText.toLowerCase())) ||
                                (email != null && email.toLowerCase().contains(searchText.toLowerCase()))) {
                            matchedContacts.add(contact);
                        }
                    }
                }

                listener.onContactsBySearchFetched(matchedContacts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onContactsBySearchFetchError(databaseError.getMessage());
            }
        });
    }

    public interface OnContactsBySearchListener {
        void onContactsBySearchFetched(List<Contact> contacts);
        void onContactsBySearchFetchError(String errorMessage);
    }


    public void addAllContacts(List<Contact> contacts, final OnContactsAddedListener listener) {
        for (Contact contact : contacts) {
            addContact(contact);
        }
        listener.onContactsAdded();
    }

    public interface OnContactsAddedListener {
        void onContactsAdded();
    }

    public void uploadAllContacts(final List<Contact> contacts, final OnContactsUploadedListener listener) {
        deleteAllContacts(new OnContactsDeletedListener() {
            @Override
            public void onContactsDeleted() {
                for (Contact contact : contacts) {
                    addContact(contact);
                }
                listener.onContactsUploaded();
            }

            @Override
            public void onDeleteError(String errorMessage) {
                listener.onUploadError(errorMessage);
            }
        });
    }

    public interface OnContactsUploadedListener {
        void onContactsUploaded();
        void onUploadError(String errorMessage);
    }

    public void deleteAllContacts(final OnContactsDeletedListener listener) {
        databaseReference.child("contacts").removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    listener.onContactsDeleted();
                } else {
                    listener.onDeleteError(databaseError.getMessage());
                }
            }
        });
    }

    public interface OnContactsDeletedListener {
        void onContactsDeleted();
        void onDeleteError(String errorMessage);
    }

    public void getAllContacts(final OnAllContactsFetchedListener listener) {
        DatabaseReference contactsRef = databaseReference.child("contacts");
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Contact> allContacts = new ArrayList<>();

                for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    Contact contact = contactSnapshot.getValue(Contact.class);
                    if (contact != null) {
                        allContacts.add(contact);
                    }
                }

                listener.onAllContactsFetched(allContacts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onAllContactsFetchError(databaseError.getMessage());
            }
        });
    }

    public interface OnAllContactsFetchedListener {
        void onAllContactsFetched(List<Contact> contacts);
        void onAllContactsFetchError(String errorMessage);
    }



}