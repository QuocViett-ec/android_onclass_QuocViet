package com.example.k234112e_hqv;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.CustomerOrderDetailAdapter;
import com.example.models.Order;
import com.example.models.OrderDetail;
import com.example.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class CustomerOrderDetailActivity extends AppCompatActivity {

    private ImageButton btnOrderDetailBack;
    private TextView txtDetOrderId;
    private TextView txtDetOrderDate;
    private TextView txtDetOrderStatus;
    private TextView txtDetCustomer;
    private RecyclerView rvOrderDetailItems;
    private TextView txtDetOrderTotal;

    private String orderId;
    private List<OrderDetail> detailsList = new ArrayList<>();
    private Map<String, Product> productMap = new HashMap<>();
    private CustomerOrderDetailAdapter detailAdapter;

    private FirebaseDatabase database;
    private DatabaseReference orderRef;
    private DatabaseReference orderDetailsRef;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_detail);

        orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId == null) {
            Toast.makeText(this, "Order ID not specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupFirebase();
        loadOrderHeader();
        loadOrderDetails();
    }

    private void initViews() {
        btnOrderDetailBack = findViewById(R.id.btnOrderDetailBack);
        txtDetOrderId = findViewById(R.id.txtDetOrderId);
        txtDetOrderDate = findViewById(R.id.txtDetOrderDate);
        txtDetOrderStatus = findViewById(R.id.txtDetOrderStatus);
        txtDetCustomer = findViewById(R.id.txtDetCustomer);
        rvOrderDetailItems = findViewById(R.id.rvOrderDetailItems);
        txtDetOrderTotal = findViewById(R.id.txtDetOrderTotal);

        btnOrderDetailBack.setOnClickListener(v -> finish());
    }

    private void setupFirebase() {
        database = FirebaseDatabase.getInstance("https://k234112e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        orderRef = database.getReference("orders").child(orderId);
        orderDetailsRef = database.getReference("orderDetails");
        productsRef = database.getReference("products");
    }

    private void loadOrderHeader() {
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                if (order != null) {
                    txtDetOrderId.setText("Order ID: " + snapshot.getKey());
                    txtDetOrderDate.setText("Date: " + (order.getOrderDateString() != null ? order.getOrderDateString() : "-"));
                    txtDetOrderStatus.setText("Status: " + (order.getStatus() != null ? order.getStatus() : "Processing"));
                    txtDetCustomer.setText("Customer ID: " + (order.getCustomerId() != null ? order.getCustomerId() : "-"));

                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    txtDetOrderTotal.setText(formatter.format(order.getTotalAmount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerOrderDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrderDetails() {
        orderDetailsRef.orderByChild("orderId").equalTo(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        detailsList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            OrderDetail detail = snapshot.getValue(OrderDetail.class);
                            if (detail != null) {
                                detail.setOrderDetailId(snapshot.getKey());
                                detailsList.add(detail);
                            }
                        }
                        if (detailsList.isEmpty()) {
                            setupAdapter();
                        } else {
                            loadProductsForDetails();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(CustomerOrderDetailActivity.this, "Error loading details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadProductsForDetails() {
        final int totalProducts = detailsList.size();
        final int[] loadedCount = {0};

        for (OrderDetail detail : detailsList) {
            String productId = detail.getProductId();
            productsRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(snapshot.getKey());
                        productMap.put(productId, product);
                    }
                    loadedCount[0]++;
                    if (loadedCount[0] == totalProducts) {
                        setupAdapter();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadedCount[0]++;
                    if (loadedCount[0] == totalProducts) {
                        setupAdapter();
                    }
                }
            });
        }
    }

    private void setupAdapter() {
        detailAdapter = new CustomerOrderDetailAdapter(detailsList, productMap);
        rvOrderDetailItems.setAdapter(detailAdapter);
    }
}
