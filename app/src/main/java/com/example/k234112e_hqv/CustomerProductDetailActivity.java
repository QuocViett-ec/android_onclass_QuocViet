package com.example.k234112e_hqv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.models.CartManager;
import com.example.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class CustomerProductDetailActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgDetailProduct;
    private TextView txtDetailName;
    private TextView txtDetailPrice;
    private TextView txtDetailStock;
    private TextView txtDetailCategory;
    private Button btnDetailMinus;
    private TextView txtDetailQuantity;
    private Button btnDetailPlus;
    private Button btnAddToCart;

    private String productId;
    private Product currentProduct;
    private int selectedQty = 1;

    private FirebaseDatabase database;
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_detail);

        productId = getIntent().getStringExtra("PRODUCT_ID");
        if (productId == null) {
            Toast.makeText(this, "Product not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupFirebase();
        loadProductDetails();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgDetailProduct = findViewById(R.id.imgDetailProduct);
        txtDetailName = findViewById(R.id.txtDetailName);
        txtDetailPrice = findViewById(R.id.txtDetailPrice);
        txtDetailStock = findViewById(R.id.txtDetailStock);
        txtDetailCategory = findViewById(R.id.txtDetailCategory);
        btnDetailMinus = findViewById(R.id.btnDetailMinus);
        txtDetailQuantity = findViewById(R.id.txtDetailQuantity);
        btnDetailPlus = findViewById(R.id.btnDetailPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        btnBack.setOnClickListener(v -> finish());

        btnDetailMinus.setOnClickListener(v -> {
            if (selectedQty > 1) {
                selectedQty--;
                txtDetailQuantity.setText(String.valueOf(selectedQty));
            }
        });

        btnDetailPlus.setOnClickListener(v -> {
            if (currentProduct != null && selectedQty < currentProduct.getStock()) {
                selectedQty++;
                txtDetailQuantity.setText(String.valueOf(selectedQty));
            } else {
                Toast.makeText(this, "Cannot select more than available stock", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null) {
                CartManager.getInstance().addToCart(currentProduct, selectedQty);
                Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupFirebase() {
        database = FirebaseDatabase.getInstance("https://k234112e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        productRef = database.getReference("products").child(productId);
    }

    private void loadProductDetails() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentProduct = snapshot.getValue(Product.class);
                if (currentProduct != null) {
                    currentProduct.setProductId(snapshot.getKey());
                    populateUI();
                } else {
                    Toast.makeText(CustomerProductDetailActivity.this, "Product unavailable", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerProductDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI() {
        txtDetailName.setText(currentProduct.getProductName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        txtDetailPrice.setText(formatter.format(currentProduct.getPrice()));
        txtDetailStock.setText("Available Stock: " + currentProduct.getStock());
        txtDetailCategory.setText("Category ID: " + currentProduct.getCategoryId());

        // Image
        if (currentProduct.getImageUrl() != null && !currentProduct.getImageUrl().trim().isEmpty()) {
            Glide.with(this)
                    .load(currentProduct.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(imgDetailProduct);
        } else {
            imgDetailProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Disable add to cart if out of stock
        if (currentProduct.getStock() <= 0) {
            btnAddToCart.setEnabled(false);
            btnAddToCart.setText("OUT OF STOCK");
            btnDetailPlus.setEnabled(false);
            btnDetailMinus.setEnabled(false);
        } else {
            btnAddToCart.setEnabled(true);
            btnAddToCart.setText("ADD TO CART");
            btnDetailPlus.setEnabled(true);
            btnDetailMinus.setEnabled(true);
        }
    }
}
