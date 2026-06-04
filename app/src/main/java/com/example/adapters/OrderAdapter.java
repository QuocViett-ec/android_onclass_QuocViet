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
import com.example.models.DataWareHouse;
import com.example.models.Order;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderAdapter extends ArrayAdapter<Order> {
    Activity context;
    int resource;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public OrderAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View customView = convertView;
        if (customView == null) {
            LayoutInflater inflater = this.context.getLayoutInflater();
            customView = inflater.inflate(this.resource, parent, false);
        }

        TextView txtOrderId = customView.findViewById(R.id.txtOrderId);
        TextView txtOrderDate = customView.findViewById(R.id.txtOrderDate);
        TextView txtStatus = customView.findViewById(R.id.txtStatus);
        TextView txtOrderTotal = customView.findViewById(R.id.txtOrderTotal);

        Order order = getItem(position);
        if (order != null) {
            txtOrderId.setText(order.getOrderId());
            if (order.getOrderDate() != null) {
                txtOrderDate.setText(dateFormat.format(order.getOrderDate()));
            }
            if (order.getOrderStatus() != null) {
                switch (order.getOrderStatus()) {
                    case COMPLETED:
                        txtStatus.setText(getContext().getString(R.string.str_order_status_completed));
                        break;
                    case NOT_PAYMENT:
                        txtStatus.setText(getContext().getString(R.string.str_order_status_notpayment));
                        break;
                    case ON_LOGISTIC:
                        txtStatus.setText(getContext().getString(R.string.str_order_status_onlogistic));
                        break;
                    case CUSTOMER_COMPLAINT:
                        txtStatus.setText(getContext().getString(R.string.str_order_status_customercomplain));
                        break;
                    default:
                        txtStatus.setText(order.getOrderStatus().name());
                        break;
                }
            }
            double total = DataWareHouse.sumOfMoney(order);
            txtOrderTotal.setText(String.valueOf(total));
        }

        return customView;
    }
}
