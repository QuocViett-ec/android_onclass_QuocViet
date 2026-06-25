package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.k234112e_hqv.R;
import com.example.models.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onQuantityChanged(CartItem item, int newQty);
        void onItemRemoved(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.txtCartItemName.setText(item.getProduct().getProductName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.txtCartItemPrice.setText("Price: " + formatter.format(item.getProduct().getPrice()));
        holder.txtCartItemSubtotal.setText("Subtotal: " + formatter.format(item.getSubtotal()));
        holder.txtCartItemQuantity.setText(String.valueOf(item.getQuantity()));

        // Load image
        if (item.getProduct().getImageUrl() != null && !item.getProduct().getImageUrl().trim().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getProduct().getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgCartItem);
        } else {
            holder.imgCartItem.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = item.getQuantity();
            if (currentQty > 1) {
                listener.onQuantityChanged(item, currentQty - 1);
            } else {
                Toast.makeText(holder.itemView.getContext(), "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int currentQty = item.getQuantity();
            if (currentQty < item.getProduct().getStock()) {
                listener.onQuantityChanged(item, currentQty + 1);
            } else {
                Toast.makeText(holder.itemView.getContext(), "Max stock limit reached (" + item.getProduct().getStock() + ")", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRemove.setOnClickListener(v -> listener.onItemRemoved(item));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCartItem;
        TextView txtCartItemName;
        TextView txtCartItemPrice;
        TextView txtCartItemSubtotal;
        TextView txtCartItemQuantity;
        Button btnMinus;
        Button btnPlus;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCartItem = itemView.findViewById(R.id.imgCartItem);
            txtCartItemName = itemView.findViewById(R.id.txtCartItemName);
            txtCartItemPrice = itemView.findViewById(R.id.txtCartItemPrice);
            txtCartItemSubtotal = itemView.findViewById(R.id.txtCartItemSubtotal);
            txtCartItemQuantity = itemView.findViewById(R.id.txtCartItemQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
