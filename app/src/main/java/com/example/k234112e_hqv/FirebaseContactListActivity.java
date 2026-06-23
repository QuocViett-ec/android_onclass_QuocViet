package com.example.k234112e_hqv;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.models.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dals.ContactDbHelper;

public class FirebaseContactListActivity extends AppCompatActivity {
    public static final String EXTRA_CONTACT_ID = "contact_id";

    private TextView txtStatus;
    private ListView lvContacts;
    private Button btnAddContact;
    private Button btnRefresh;
    private Button btnSyncNow;

    private DatabaseReference contactsRef;
    private DatabaseReference connectedRef;
    private ValueEventListener connectionListener;
    private ContactDbHelper dbHelper;
    private ArrayList<Contact> contacts;
    private ArrayAdapter<String> adapter;
    private boolean firebaseConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_contact_list);

        contactsRef = FirebaseContactDatabase.contactsReference();
        connectedRef = FirebaseContactDatabase.connectedReference();
        dbHelper = new ContactDbHelper(this);
        contacts = new ArrayList<>();

        initViews();
        setupEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenFirebaseConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectedRef != null && connectionListener != null) {
            connectedRef.removeEventListener(connectionListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOnline()) {
            txtStatus.setText(getString(R.string.connecting_firebase));
        } else {
            loadLocalContacts();
        }
    }

    private void listenFirebaseConnection() {
        connectionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                firebaseConnected = Boolean.TRUE.equals(connected);
                if (firebaseConnected) {
                    syncPendingContacts(false);
                } else if (isOnline()) {
                    txtStatus.setText(getString(R.string.connecting_firebase));
                } else {
                    loadLocalContacts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                firebaseConnected = false;
                showFirebaseError(error.toException());
                loadLocalContacts();
            }
        };
        connectedRef.addValueEventListener(connectionListener);
    }

    private void initViews() {
        txtStatus = findViewById(R.id.txtStatus);
        lvContacts = findViewById(R.id.lvContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnSyncNow = findViewById(R.id.btnSyncNow);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvContacts.setAdapter(adapter);
    }

    private void setupEvents() {
        lvContacts.setOnItemClickListener((parent, view, position, id) -> {
            Contact selected = contacts.get(position);
            Intent intent = new Intent(this, FirebaseContactDetailActivity.class);
            intent.putExtra(EXTRA_CONTACT_ID, selected.getId());
            startActivity(intent);
        });

        btnAddContact.setOnClickListener(v -> startActivity(new Intent(this, InsertContactActivity.class)));
        btnRefresh.setOnClickListener(v -> reloadContacts());
        btnSyncNow.setOnClickListener(v -> syncPendingContacts(true));
    }

    private void reloadContacts() {
        if (isFirebaseReady()) {
            syncPendingContacts(false);
        } else if (isOnline()) {
            txtStatus.setText(getString(R.string.connecting_firebase));
            Toast.makeText(this, getString(R.string.firebase_disconnected), Toast.LENGTH_SHORT).show();
        } else {
            loadLocalContacts();
        }
    }

    private void loadFirebaseContacts() {
        txtStatus.setText(getString(R.string.online_firebase_mode));
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Contact> firebaseContacts = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Contact contact = contactFromSnapshot(child);
                    if (contact != null) {
                        firebaseContacts.add(contact);
                        dbHelper.insertOrUpdateContact(contact);
                    }
                }
                displayContacts(firebaseContacts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FirebaseContactListActivity.this, getString(R.string.firebase_load_failed), Toast.LENGTH_SHORT).show();
                showFirebaseError(error.toException());
                loadLocalContacts();
            }
        });
    }

    private void loadLocalContacts() {
        txtStatus.setText(getString(R.string.offline_local_mode));
        try {
            displayContacts(dbHelper.getVisibleContacts());
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.local_load_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayContacts(ArrayList<Contact> newContacts) {
        contacts.clear();
        contacts.addAll(newContacts);
        adapter.clear();
        for (Contact contact : contacts) {
            adapter.add(getString(
                    R.string.contact_list_row_format,
                    contact.getId(),
                    contact.getPhone(),
                    contact.getName(),
                    contact.getEmail()
            ));
        }
        adapter.notifyDataSetChanged();
    }

    public void syncPendingContacts() {
        syncPendingContacts(true);
    }

    private void syncPendingContacts(boolean showToast) {
        if (!isFirebaseReady()) {
            loadLocalContacts();
            if (showToast) {
                Toast.makeText(this, getString(isOnline() ? R.string.firebase_disconnected : R.string.offline_local_mode), Toast.LENGTH_SHORT).show();
            }
            return;
        }

        ArrayList<Contact> pendingContacts = dbHelper.getPendingContacts();
        if (pendingContacts.isEmpty()) {
            loadFirebaseContacts();
            return;
        }

        txtStatus.setText(getString(R.string.syncing_pending_changes));
        final int total = pendingContacts.size();
        final int[] completed = {0};
        final boolean[] failed = {false};

        for (Contact contact : pendingContacts) {
            if (Contact.PENDING_DELETE.equals(contact.getSyncStatus())) {
                contactsRef.child(contact.getId()).removeValue()
                        .addOnSuccessListener(unused -> {
                            dbHelper.deleteContact(contact.getId());
                            finishOneSync(total, completed, failed, showToast);
                        })
                        .addOnFailureListener(e -> {
                            failed[0] = true;
                            showFirebaseError(e);
                            finishOneSync(total, completed, failed, showToast);
                        });
            } else {
                contactsRef.child(contact.getId()).setValue(contact.toFirebaseMap())
                        .addOnSuccessListener(unused -> {
                            dbHelper.markSynced(contact.getId());
                            finishOneSync(total, completed, failed, showToast);
                        })
                        .addOnFailureListener(e -> {
                            failed[0] = true;
                            showFirebaseError(e);
                            finishOneSync(total, completed, failed, showToast);
                        });
            }
        }
    }

    private void finishOneSync(int total, int[] completed, boolean[] failed, boolean showToast) {
        completed[0]++;
        if (completed[0] < total) {
            return;
        }
        if (showToast) {
            Toast.makeText(this, getString(failed[0] ? R.string.sync_failed : R.string.sync_success), Toast.LENGTH_SHORT).show();
        }
        loadFirebaseContacts();
    }

    private boolean isFirebaseReady() {
        return isOnline() && firebaseConnected;
    }

    private void showFirebaseError(Exception e) {
        String message = e == null || e.getMessage() == null ? getString(R.string.sync_failed) : e.getMessage();
        Toast.makeText(this, getString(R.string.firebase_error_format, message), Toast.LENGTH_LONG).show();
    }

    private Contact contactFromSnapshot(DataSnapshot snapshot) {
        String id = snapshot.getKey();
        if (id == null) {
            return null;
        }
        String name = snapshotValueAsString(snapshot.child("name"));
        String phone = snapshotValueAsString(snapshot.child("phone"));
        String email = snapshotValueAsString(snapshot.child("email"));
        return new Contact(id, name, phone, email, System.currentTimeMillis(), Contact.SYNCED);
    }

    private String snapshotValueAsString(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        return value == null ? "" : String.valueOf(value);
    }

    private boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = manager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
        return false;
    }
}
