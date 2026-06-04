package com.example.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.k234112e_hqv.R;
import com.example.models.Product;

public class
ProductAdapter extends ArrayAdapter<Product> {
    Activity context;
    int resource;

    public ProductAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View custom = convertView;
        if (custom == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            custom = inflater.inflate(resource, parent, false);
        }

        Product product = getItem(position);
        if (product != null) {
            TextView txtProductId = custom.findViewById(R.id.txtProductId);
            TextView txtProductName = custom.findViewById(R.id.txtProductName);
            TextView txtQuantity = custom.findViewById(R.id.txtQuantity);
            TextView txtPrices = custom.findViewById(R.id.txtPrices);
            TextView txtCoupon = custom.findViewById(R.id.txtCoupon);
            TextView txtVAT = custom.findViewById(R.id.txtVAT);
            TextView txtCategoryId = custom.findViewById(R.id.txtCategoryId);

            txtProductId.setText(product.getProductId());
            txtProductName.setText(product.getProductName());
            txtQuantity.setText(String.valueOf(product.getQuantity()));
            txtPrices.setText(String.valueOf(product.getPrices()));
            txtCoupon.setText(String.valueOf(product.getCoupon()));
            txtVAT.setText(String.valueOf(product.getVAT()));
            txtCategoryId.setText(product.getCategoryId());
        }

        return custom;
    }
}

