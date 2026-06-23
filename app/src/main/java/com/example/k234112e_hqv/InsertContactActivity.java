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

import androidx.appcompat.app.AppCompatActivity;

import com.example.models.Contact;
import com.google.firebase.database.DatabaseReference;

import dals.ContactDbHelper;

public class InsertContactActivity extends AppCompatActivity {
    private EditText edtContactId;
    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPhone;
    private Button btnInsertContact;

    private DatabaseReference contactsRef;
    private ContactDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_contact);

        contactsRef = FirebaseContactDatabase.contactsReference();
        dbHelper = new ContactDbHelper(this);

        edtContactId = findViewById(R.id.edtContactId);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnInsertContact = findViewById(R.id.btnInsertContact);

        btnInsertContact.setOnClickListener(v -> insertContact());
    }

    private void insertContact() {
        Contact contact = readContactFromForm(Contact.SYNCED);
        if (contact == null) {
            return;
        }

        if (isOnline()) {
            contactsRef.child(contact.getId()).setValue(contact.toFirebaseMap())
                    .addOnSuccessListener(unused -> {
                        dbHelper.insertOrUpdateContact(contact);
                        Toast.makeText(this, getString(R.string.contact_inserted), Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> showFirebaseError(e));
        } else {
            contact.setSyncStatus(Contact.PENDING_CREATE);
            dbHelper.insertOrUpdateContact(contact);
            Toast.makeText(this, getString(R.string.contact_saved_offline), Toast.LENGTH_SHORT).show();
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
