package com.example.k234112e_hqv;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseContactDatabase {
    private static final String DATABASE_URL = "https://k234112e-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String CONTACTS_NODE = "contacts";

    private FirebaseContactDatabase() {
    }

    public static DatabaseReference contactsReference() {
        return FirebaseDatabase.getInstance(DATABASE_URL).getReference(CONTACTS_NODE);
    }

    public static DatabaseReference connectedReference() {
        return FirebaseDatabase.getInstance(DATABASE_URL).getReference(".info/connected");
    }
}
