package com.example.k234112e_hqv;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adapters.CategoryAdapter;
import com.example.models.Category;

import java.util.ArrayList;

import dals.CategoryDAO;

public class CategoryActivity extends AppCompatActivity {

    ListView lvCategory;
    ArrayList<Category>categories;
    //ArrayAdapter<Category>categoryAdapter;
    CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
       lvCategory=findViewById(R.id.lvCategory);
       categories= CategoryDAO.getCategories(this);
       categoryAdapter=new CategoryAdapter(this,R.layout.categoty_custom_item);
       categoryAdapter.addAll(categories);
       lvCategory.setAdapter(categoryAdapter);
    }
}