package com.example.adapters;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.k234112e_hqv.R;
import com.example.models.Employee;

public class EmployeeAdapter extends ArrayAdapter<Employee> {
    Activity context;
    int resource;

    public EmployeeAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View custom_view=inflater.inflate(resource,null);
        Employee emp=getItem(position);
        TextView txt_id=custom_view.findViewById(R.id.txt_id);
        TextView txt_name=custom_view.findViewById(R.id.txt_name);
        TextView txt_phone=custom_view.findViewById(R.id.txt_phone);
        txt_id.setText(emp.getId());
        txt_name.setText(emp.getName());
        txt_phone.setText(emp.getPhone());
        ImageView imgCall=custom_view.findViewById(R.id.img_call);
        ImageView imgSms=custom_view.findViewById(R.id.img_sms);
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                Uri uri=Uri.parse("tel:"+emp.getPhone());
                intentCall.setData(uri);
                context.startActivity(intentCall);
            }
        });
        imgSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSms = new Intent(Intent.ACTION_SENDTO);
                Uri uri = Uri.parse("smsto:" + emp.getPhone());
                intentSms.setData(uri);
                context.startActivity(intentSms);
            }
        });

        return custom_view;
    }
}
