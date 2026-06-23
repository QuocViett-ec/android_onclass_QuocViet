package com.example.k234112e_hqv;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.models.FirebaseItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dals.FirebaseItemDAO;

public class FirebaseCrudActivity extends AppCompatActivity {

    private TextView txtConnectionStatus;
    private View viewStatusIndicator;
    private EditText edtItemId, edtItemName, edtItemPrice, edtItemQuantity;
    private Button btnAdd, btnUpdate, btnDelete, btnClear;
    private ListView lvItems;

    private DatabaseReference mDatabase;
    private DatabaseReference connectedRef;
    private ValueEventListener firebaseListener;
    private ValueEventListener connectivityListener;

    private ArrayList<FirebaseItem> itemList;
    private ArrayAdapter<FirebaseItem> adapter;

    private boolean isCurrentlyOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_crud);

        initViews();
        initData();
        setupFirebase();
        setupEvents();
    }

    private void initViews() {
        txtConnectionStatus = findViewById(R.id.txtConnectionStatus);
        viewStatusIndicator = findViewById(R.id.viewStatusIndicator);
        edtItemId = findViewById(R.id.edtItemId);
        edtItemName = findViewById(R.id.edtItemName);
        edtItemPrice = findViewById(R.id.edtItemPrice);
        edtItemQuantity = findViewById(R.id.edtItemQuantity);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnClear = findViewById(R.id.btnClear);
        lvItems = findViewById(R.id.lvItems);
    }

    private void initData() {
        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        lvItems.setAdapter(adapter);
    }

    private void setupFirebase() {
        // Initialize Firebase Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference("items");
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    }

    private void setupEvents() {
        // Form click selection
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FirebaseItem selected = itemList.get(position);
                edtItemId.setText(selected.getId());
                edtItemName.setText(selected.getName());
                edtItemPrice.setText(String.valueOf(selected.getPrice()));
                edtItemQuantity.setText(String.valueOf(selected.getQuantity()));
            }
        });

        // CRUD Button clicks
        btnAdd.setOnClickListener(v -> addItem());
        btnUpdate.setOnClickListener(v -> updateItem());
        btnDelete.setOnClickListener(v -> deleteItem());
        btnClear.setOnClickListener(v -> clearForm());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check system connectivity manager first
        updateStatus(isOnline());

        // Listen for actual Firebase connection state changes
        connectivityListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class) != null && snapshot.getValue(Boolean.class);
                updateStatus(connected);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        connectedRef.addValueEventListener(connectivityListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectedRef != null && connectivityListener != null) {
            connectedRef.removeEventListener(connectivityListener);
        }
        detachFirebaseListener();
    }

    private void updateStatus(boolean online) {
        isCurrentlyOnline = online;
        runOnUiThread(() -> {
            if (online) {
                txtConnectionStatus.setText("Status: ONLINE (Firebase Mode)");
                viewStatusIndicator.setBackgroundColor(Color.parseColor("#28A745")); // Green color
                attachFirebaseListener();
            } else {
                txtConnectionStatus.setText("Status: OFFLINE (Local DB Mode)");
                viewStatusIndicator.setBackgroundColor(Color.parseColor("#DC3545")); // Red color
                detachFirebaseListener();
                loadLocalData();
            }
        });
    }

    private void attachFirebaseListener() {
        if (firebaseListener == null) {
            firebaseListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!isCurrentlyOnline) return;

                    ArrayList<FirebaseItem> firebaseList = new ArrayList<>();
                    // Sync: clear local SQLite and insert fresh items from Firebase
                    FirebaseItemDAO.clearAll(FirebaseCrudActivity.this);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FirebaseItem item = snapshot.getValue(FirebaseItem.class);
                        if (item != null) {
                            if (item.getId() == null) {
                                item.setId(snapshot.getKey());
                            }
                            firebaseList.add(item);
                            FirebaseItemDAO.saveItem(FirebaseCrudActivity.this, item);
                        }
                    }

                    itemList.clear();
                    itemList.addAll(firebaseList);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(FirebaseCrudActivity.this, "Firebase Load Cancelled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            mDatabase.addValueEventListener(firebaseListener);
        }
    }

    private void detachFirebaseListener() {
        if (mDatabase != null && firebaseListener != null) {
            mDatabase.removeEventListener(firebaseListener);
            firebaseListener = null;
        }
    }

    private void loadLocalData() {
        ArrayList<FirebaseItem> localList = FirebaseItemDAO.getItems(FirebaseCrudActivity.this);
        itemList.clear();
        itemList.addAll(localList);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Loaded " + localList.size() + " items from local SQLite DB Cache", Toast.LENGTH_SHORT).show();
    }

    private void addItem() {
        if (!isCurrentlyOnline) {
            Toast.makeText(this, "CRUD Actions disabled: Device is OFFLINE", Toast.LENGTH_LONG).show();
            return;
        }

        String name = edtItemName.getText().toString().trim();
        String priceStr = edtItemPrice.getText().toString().trim();
        String quantityStr = edtItemQuantity.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill in Name, Price, and Quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            String id = mDatabase.push().getKey();
            if (id != null) {
                FirebaseItem item = new FirebaseItem(id, name, price, quantity);
                mDatabase.child(id).setValue(item)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(FirebaseCrudActivity.this, "Item added to Firebase", Toast.LENGTH_SHORT).show();
                            clearForm();
                        })
                        .addOnFailureListener(e -> Toast.makeText(FirebaseCrudActivity.this, "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number inputs", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateItem() {
        if (!isCurrentlyOnline) {
            Toast.makeText(this, "CRUD Actions disabled: Device is OFFLINE", Toast.LENGTH_LONG).show();
            return;
        }

        String id = edtItemId.getText().toString().trim();
        String name = edtItemName.getText().toString().trim();
        String priceStr = edtItemPrice.getText().toString().trim();
        String quantityStr = edtItemQuantity.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "Please select an item from the list to update", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill in Name, Price, and Quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            FirebaseItem item = new FirebaseItem(id, name, price, quantity);
            mDatabase.child(id).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(FirebaseCrudActivity.this, "Item updated in Firebase", Toast.LENGTH_SHORT).show();
                        clearForm();
                    })
                    .addOnFailureListener(e -> Toast.makeText(FirebaseCrudActivity.this, "Failed to update item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number inputs", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem() {
        if (!isCurrentlyOnline) {
            Toast.makeText(this, "CRUD Actions disabled: Device is OFFLINE", Toast.LENGTH_LONG).show();
            return;
        }

        String id = edtItemId.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Please select an item from the list to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child(id).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(FirebaseCrudActivity.this, "Item deleted from Firebase", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> Toast.makeText(FirebaseCrudActivity.this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearForm() {
        edtItemId.setText("");
        edtItemName.setText("");
        edtItemPrice.setText("");
        edtItemQuantity.setText("");
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
