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
import com.example.models.OrderDetail;
import com.example.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class CustomerOrderDetailAdapter extends RecyclerView.Adapter<CustomerOrderDetailAdapter.ViewHolder> {

    private List<OrderDetail> details;
    private Map<String, Product> productMap;

    public CustomerOrderDetailAdapter(List<OrderDetail> details, Map<String, Product> productMap) {
        this.details = details;
        this.productMap = productMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail detail = details.get(position);
        Product product = productMap.get(detail.getProductId());

        if (product != null) {
            holder.txtProductName.setText(product.getProductName());
            if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .into(holder.imgProduct);
            } else {
                holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.txtProductName.setText("Product ID: " + detail.getProductId());
            holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        double price = detail.getUnitPrice() > 0 ? detail.getUnitPrice() : detail.getPrice();
        holder.txtPriceQty.setText(formatter.format(price) + " x " + detail.getQuantity());

        double subtotal = price * detail.getQuantity();
        holder.txtSubtotal.setText(formatter.format(subtotal));
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName;
        TextView txtPriceQty;
        TextView txtSubtotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgOrderDetailProduct);
            txtProductName = itemView.findViewById(R.id.txtOrderDetailProductName);
            txtPriceQty = itemView.findViewById(R.id.txtOrderDetailPriceQty);
            txtSubtotal = itemView.findViewById(R.id.txtOrderDetailSubtotal);
        }
    }
}
