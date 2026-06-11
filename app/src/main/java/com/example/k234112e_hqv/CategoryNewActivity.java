package com.example.k234112e_hqv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.Category;

import dals.CategoryDAO;

public class CategoryNewActivity extends AppCompatActivity {

    EditText edtCategoryID,edtCategoryName,edtCategoryDescription;
    Category editCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_new);
        addViews();
        
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("EDIT_CATEGORY")) {
            editCategory = (Category) intent.getSerializableExtra("EDIT_CATEGORY");
            if (editCategory != null) {
                edtCategoryID.setText(editCategory.getCateId());
                edtCategoryID.setEnabled(false); // Do not allow primary key editing
                edtCategoryName.setText(editCategory.getCateName());
                edtCategoryDescription.setText(editCategory.getCateDescription());
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        edtCategoryID=findViewById(R.id.edtCategoryID);
        edtCategoryName=findViewById(R.id.edtCategoryName);
        edtCategoryDescription=findViewById(R.id.edtCategoryDescription);
    }

    public void process_save(View view) {
        String cateId=edtCategoryID.getText().toString();
        String cateName=edtCategoryName.getText().toString();
        String cateDescription=edtCategoryDescription.getText().toString();

        Category category=new Category(cateId,cateName,cateDescription);
        long result;
        if (editCategory != null) {
            result = CategoryDAO.updateCategory(this, category);
        } else {
            result = CategoryDAO.saveCategory(this, category);
        }

        if (result>0)
        {
            Intent intent=getIntent();
            //assume 1 is success
            setResult(3,intent);
            finish();
        }
        else
        {
            Toast.makeText(this, "Save fail!", Toast.LENGTH_SHORT).show();
        }
    }

    public void process_cancel(View view) {
        Intent intent=getIntent();
        //assume 2 is cancel
        setResult(2,intent);
        finish();
    }
}