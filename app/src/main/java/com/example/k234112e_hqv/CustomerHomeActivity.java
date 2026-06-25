package com.example.k234112e_hqv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.CustomerCategoryAdapter;
import com.example.adapters.CustomerProductAdapter;
import com.example.models.CartManager;
import com.example.models.Category;
import com.example.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private RecyclerView rvProducts;
    private TextView txtCartBadge;
    private ImageButton btnGoToCart;
    private ImageButton btnGoToOrders;

    private List<Category> categoryList = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> displayedProducts = new ArrayList<>();

    private CustomerCategoryAdapter categoryAdapter;
    private CustomerProductAdapter productAdapter;

    private FirebaseDatabase database;
    private DatabaseReference productsRef;
    private DatabaseReference categoriesRef;

    private Category selectedCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        initViews();
        setupFirebase();
        setupRecyclerViews();
        loadData();
    }

    private void initViews() {
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        txtCartBadge = findViewById(R.id.txtCartBadge);
        btnGoToCart = findViewById(R.id.btnGoToCart);
        btnGoToOrders = findViewById(R.id.btnGoToOrders);
        View layoutCartContainer = findViewById(R.id.layoutCartContainer);

        View.OnClickListener goToCartListener = v -> {
            Intent intent = new Intent(this, CustomerCartActivity.class);
            startActivity(intent);
        };
        btnGoToCart.setOnClickListener(goToCartListener);
        if (layoutCartContainer != null) {
            layoutCartContainer.setOnClickListener(goToCartListener);
        }

        btnGoToOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerOrdersActivity.class);
            startActivity(intent);
        });
    }

    private void setupFirebase() {
        database = FirebaseDatabase.getInstance("https://k234112e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        productsRef = database.getReference("products");
        categoriesRef = database.getReference("categories");
    }

    private void setupRecyclerViews() {
        // Categories horizontal list
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CustomerCategoryAdapter(categoryList, category -> {
            selectedCategory = category;
            filterProducts();
        });
        rvCategories.setAdapter(categoryAdapter);

        // Products grid list
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new CustomerProductAdapter(displayedProducts, product -> {
            Intent intent = new Intent(CustomerHomeActivity.this, CustomerProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getProductId());
            startActivity(intent);
        });
        rvProducts.setAdapter(productAdapter);
    }

    private void loadData() {
        // Load Categories
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null) {
                        category.setCateId(snapshot.getKey());
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerHomeActivity.this, "Failed to load categories: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load Products
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allProducts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(snapshot.getKey());
                        // Only add active products
                        if (product.getIsActive()) {
                            allProducts.add(product);
                        }
                    }
                }
                filterProducts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerHomeActivity.this, "Failed to load products: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts() {
        displayedProducts.clear();
        for (Product product : allProducts) {
            if (selectedCategory == null || product.getCategoryId().equals(selectedCategory.getCateId())) {
                displayedProducts.add(product);
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void updateCartBadge() {
        int count = 0;
        for (com.example.models.CartItem item : CartManager.getInstance().getCartItems()) {
            count += item.getQuantity();
        }

        if (count > 0) {
            txtCartBadge.setText(String.valueOf(count));
            txtCartBadge.setVisibility(View.VISIBLE);
        } else {
            txtCartBadge.setVisibility(View.GONE);
        }
    }
}
