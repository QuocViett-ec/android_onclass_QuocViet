package com.example.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k234112e_hqv.R;
import com.example.models.Category;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CustomerCategoryAdapter extends RecyclerView.Adapter<CustomerCategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = -1;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CustomerCategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.txtCategoryName.setText(category.getCategoryName());

        // Highlight selected category
        if (selectedPosition == position) {
            holder.cardCategory.setCardBackgroundColor(Color.parseColor("#3F51B5"));
            holder.txtCategoryName.setTextColor(Color.WHITE);
            holder.cardCategory.setStrokeWidth(0);
        } else {
            holder.cardCategory.setCardBackgroundColor(Color.WHITE);
            holder.txtCategoryName.setTextColor(Color.parseColor("#333333"));
            holder.cardCategory.setStrokeWidth(1);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            if (selectedPosition == position) {
                // Deselect
                selectedPosition = -1;
                listener.onCategoryClick(null);
            } else {
                selectedPosition = position;
                listener.onCategoryClick(category);
            }
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardCategory;
        TextView txtCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.cardCategory);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
        }
    }
}
