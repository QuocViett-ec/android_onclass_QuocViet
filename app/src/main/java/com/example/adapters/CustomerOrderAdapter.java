package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k234112e_hqv.R;
import com.example.models.Order;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderAdapter.ViewHolder> {

    private List<Order> orders;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public CustomerOrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.txtOrderId.setText("Order ID: " + order.getOrderId());
        holder.txtOrderStatus.setText(order.getStatus() != null ? order.getStatus() : "Processing");
        holder.txtOrderDate.setText("Date: " + (order.getOrderDateString() != null ? order.getOrderDateString() : "-"));

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.txtOrderTotal.setText(formatter.format(order.getTotalAmount()));

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId;
        TextView txtOrderStatus;
        TextView txtOrderDate;
        TextView txtOrderTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtOrderTotal = itemView.findViewById(R.id.txtOrderTotal);
        }
    }
}
