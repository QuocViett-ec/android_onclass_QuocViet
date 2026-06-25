package com.example.k234112e_hqv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.CartAdapter;
import com.example.models.CartItem;
import com.example.models.CartManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CustomerCartActivity extends AppCompatActivity {

    private ImageButton btnCartBack;
    private RecyclerView rvCartItems;
    private TextView txtEmptyCart;
    private TextView txtCartTotal;
    private Button btnCheckout;

    private List<CartItem> cartItemsList = new ArrayList<>();
    private CartAdapter cartAdapter;

    private FirebaseDatabase database;
    private DatabaseReference ordersRef;
    private DatabaseReference orderDetailsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

        initViews();
        setupFirebase();
        setupRecyclerView();
    }

    private void initViews() {
        btnCartBack = findViewById(R.id.btnCartBack);
        rvCartItems = findViewById(R.id.rvCartItems);
        txtEmptyCart = findViewById(R.id.txtEmptyCart);
        txtCartTotal = findViewById(R.id.txtCartTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        btnCartBack.setOnClickListener(v -> finish());

        btnCheckout.setOnClickListener(v -> performCheckout());
    }

    private void setupFirebase() {
        database = FirebaseDatabase.getInstance("https://k234112e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        ordersRef = database.getReference("orders");
        orderDetailsRef = database.getReference("orderDetails");
    }

    private void setupRecyclerView() {
        cartItemsList.addAll(CartManager.getInstance().getCartItems());
        cartAdapter = new CartAdapter(cartItemsList, new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQty) {
                CartManager.getInstance().updateQuantity(item.getProduct().getProductId(), newQty);
                refreshCart();
            }

            @Override
            public void onItemRemoved(CartItem item) {
                CartManager.getInstance().removeFromCart(item.getProduct().getProductId());
                refreshCart();
            }
        });
        rvCartItems.setAdapter(cartAdapter);

        updateTotalAndEmptyStates();
    }

    private void refreshCart() {
        cartItemsList.clear();
        cartItemsList.addAll(CartManager.getInstance().getCartItems());
        cartAdapter.notifyDataSetChanged();
        updateTotalAndEmptyStates();
    }

    private void updateTotalAndEmptyStates() {
        double total = CartManager.getInstance().getTotalAmount();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        txtCartTotal.setText(formatter.format(total));

        if (cartItemsList.isEmpty()) {
            txtEmptyCart.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
        } else {
            txtEmptyCart.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);
        }
    }

    private void performCheckout() {
        if (cartItemsList.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCheckout.setEnabled(false);

        // Prepare order ID
        String orderId = ordersRef.push().getKey();
        if (orderId == null) {
            Toast.makeText(this, "Failed to generate Order ID", Toast.LENGTH_SHORT).show();
            btnCheckout.setEnabled(true);
            return;
        }

        // Format Date to ISO string: "2026-06-15T08:30:00Z"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String orderDateStr = sdf.format(new Date());

        // Order mapping
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("customerId", "CUST001"); // Demohardcoded customer id
        orderMap.put("employeeId", "");
        orderMap.put("orderDate", orderDateStr);
        orderMap.put("status", "Processing");
        orderMap.put("totalAmount", CartManager.getInstance().getTotalAmount());

        // Insert Order first
        ordersRef.child(orderId).setValue(orderMap)
                .addOnSuccessListener(aVoid -> {
                    // Order header successfully written, now write details
                    writeOrderDetails(orderId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CustomerCartActivity.this, "Failed to create order: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnCheckout.setEnabled(true);
                });
    }

    private void writeOrderDetails(String orderId) {
        final int totalItems = cartItemsList.size();
        final int[] successCounter = {0};
        final boolean[] failureOccurred = {false};

        for (int i = 0; i < totalItems; i++) {
            CartItem item = cartItemsList.get(i);
            String detailId = orderDetailsRef.push().getKey();
            if (detailId == null) {
                failureOccurred[0] = true;
                continue;
            }

            Map<String, Object> detailMap = new HashMap<>();
            detailMap.put("orderId", orderId);
            detailMap.put("productId", item.getProduct().getProductId());
            detailMap.put("quantity", item.getQuantity());
            detailMap.put("unitPrice", item.getProduct().getPrice());

            orderDetailsRef.child(detailId).setValue(detailMap)
                    .addOnSuccessListener(aVoid -> {
                        successCounter[0]++;
                        checkCheckoutCompletion(successCounter[0], totalItems, failureOccurred[0]);
                    })
                    .addOnFailureListener(e -> {
                        failureOccurred[0] = true;
                        successCounter[0]++;
                        checkCheckoutCompletion(successCounter[0], totalItems, true);
                    });
        }
    }

    private void checkCheckoutCompletion(int currentCount, int totalCount, boolean failed) {
        if (currentCount == totalCount) {
            if (failed) {
                Toast.makeText(this, "Order created but some details failed to save. Please contact support.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Checkout successful! Order created.", Toast.LENGTH_LONG).show();
                CartManager.getInstance().clearCart();
                Intent intent = new Intent(this, CustomerOrdersActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
