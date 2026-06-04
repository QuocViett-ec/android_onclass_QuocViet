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
import com.example.models.Category;

public class CategoryAdapter extends ArrayAdapter<Category>
{
    Activity context;
    int resource;
    public CategoryAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View custom = convertView;
        if (custom == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            custom = inflater.inflate(resource, parent, false);
        }
        
        Category c = getItem(position);
        if (c != null) {
            TextView txtCategoryId = custom.findViewById(R.id.txtCategoryId);
            TextView txtCategoryName = custom.findViewById(R.id.txtCategoryName);
            TextView txtCategoryDescription = custom.findViewById(R.id.txtCategoryDescription);
            
            txtCategoryId.setText(c.getCateId());
            txtCategoryName.setText(c.getCateName());
            txtCategoryDescription.setText(c.getCateDescription());
        }

        return custom;
    }
}


