package com.example.k234112e_hqv;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.models.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import dals.ContactDbHelper;

public class FirebaseContactDetailActivity extends AppCompatActivity {
    private EditText edtContactId;
    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPhone;
    private Button btnBack;
    private Button btnUpdate;
    private Button btnDelete;

    private DatabaseReference contactsRef;
    private ContactDbHelper dbHelper;
    private String contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_contact_detail);

        contactsRef = FirebaseContactDatabase.contactsReference();
        dbHelper = new ContactDbHelper(this);
        contactId = getIntent().getStringExtra(FirebaseContactListActivity.EXTRA_CONTACT_ID);

        initViews();
        setupEvents();
        loadContact();
    }

    private void initViews() {
        edtContactId = findViewById(R.id.edtContactId);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnBack = findViewById(R.id.btnBack);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updateContact());
        btnDelete.setOnClickListener(v -> deleteContact());
    }

    private void loadContact() {
        if (contactId == null || contactId.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.no_contact_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (isOnline()) {
            contactsRef.child(contactId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(FirebaseContactDetailActivity.this, getString(R.string.no_contact_found), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    Contact contact = new Contact(
                            contactId,
                            snapshotValueAsString(snapshot.child("name")),
                            snapshotValueAsString(snapshot.child("phone")),
                            snapshotValueAsString(snapshot.child("email")),
                            System.currentTimeMillis(),
                            Contact.SYNCED
                    );
                    dbHelper.insertOrUpdateContact(contact);
                    fillForm(contact);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(FirebaseContactDetailActivity.this, getString(R.string.firebase_load_failed), Toast.LENGTH_SHORT).show();
                    showFirebaseError(error.toException());
                    loadLocalContact();
                }
            });
        } else {
            loadLocalContact();
        }
    }

    private void loadLocalContact() {
        Contact contact = dbHelper.getContactById(contactId);
        if (contact == null || Contact.PENDING_DELETE.equals(contact.getSyncStatus())) {
            Toast.makeText(this, getString(R.string.no_contact_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        fillForm(contact);
    }

    private void fillForm(Contact contact) {
        edtContactId.setText(contact.getId());
        edtName.setText(contact.getName());
        edtEmail.setText(contact.getEmail());
        edtPhone.setText(contact.getPhone());
    }

    private void updateContact() {
        Contact contact = readContactFromForm(Contact.SYNCED);
        if (contact == null) {
            return;
        }

        if (isOnline()) {
            contactsRef.child(contact.getId()).setValue(contact.toFirebaseMap())
                    .addOnSuccessListener(unused -> {
                        dbHelper.insertOrUpdateContact(contact);
                        Toast.makeText(this, getString(R.string.contact_updated), Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> showFirebaseError(e));
        } else {
            Contact current = dbHelper.getContactById(contact.getId());
            if (current != null && Contact.PENDING_CREATE.equals(current.getSyncStatus())) {
                contact.setSyncStatus(Contact.PENDING_CREATE);
            } else {
                contact.setSyncStatus(Contact.PENDING_UPDATE);
            }
            dbHelper.insertOrUpdateContact(contact);
            Toast.makeText(this, getString(R.string.contact_update_saved_offline), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void deleteContact() {
        String id = edtContactId.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_contact_found), Toast.LENGTH_SHORT).show();
            return;
        }

        if (isOnline()) {
            contactsRef.child(id).removeValue()
                    .addOnSuccessListener(unused -> {
                        dbHelper.deleteContact(id);
                        Toast.makeText(this, getString(R.string.contact_deleted), Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> showFirebaseError(e));
        } else {
            Contact current = dbHelper.getContactById(id);
            if (current != null && Contact.PENDING_CREATE.equals(current.getSyncStatus())) {
                dbHelper.deleteContact(id);
            } else {
                dbHelper.markPendingDelete(id);
            }
            Toast.makeText(this, getString(R.string.contact_delete_saved_offline), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private Contact readContactFromForm(String syncStatus) {
        String id = edtContactId.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, getString(R.string.validation_required_fields), Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!isValidFirebaseKey(id)) {
            Toast.makeText(this, getString(R.string.invalid_contact_id), Toast.LENGTH_SHORT).show();
            return null;
        }
        return new Contact(id, name, phone, email, System.currentTimeMillis(), syncStatus);
    }

    private boolean isValidFirebaseKey(String id) {
        return !id.contains(".") && !id.contains("#") && !id.contains("$") &&
                !id.contains("[") && !id.contains("]") && !id.contains("/");
    }

    private void showFirebaseError(Exception e) {
        String message = e == null || e.getMessage() == null ? getString(R.string.sync_failed) : e.getMessage();
        Toast.makeText(this, getString(R.string.firebase_error_format, message), Toast.LENGTH_LONG).show();
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
