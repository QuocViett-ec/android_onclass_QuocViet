package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.k234112e_hqv.R;
import com.example.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.ViewHolder> {

    private List<Product> products;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public CustomerProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.txtProductName.setText(product.getProductName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.txtProductPrice.setText(formatter.format(product.getPrice()));

        holder.txtProductStock.setText("Stock: " + product.getStock());

        if (product.getStock() <= 0) {
            holder.txtProductStatus.setText("Out of stock");
            holder.txtProductStatus.setTextColor(0xFFD32F2F); // Red
        } else {
            holder.txtProductStatus.setText("Available");
            holder.txtProductStatus.setTextColor(0xFF2E7D32); // Green
        }

        // Load image using Glide
        if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName;
        TextView txtProductPrice;
        TextView txtProductStock;
        TextView txtProductStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtProductStock = itemView.findViewById(R.id.txtProductStock);
            txtProductStatus = itemView.findViewById(R.id.txtProductStatus);
        }
    }
}
