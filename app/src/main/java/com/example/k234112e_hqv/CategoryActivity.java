package com.example.k234112e_hqv;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    CategoryAdapter categoryAdapter;
    Toolbar toolbarCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = categories.get(position);
                Intent intent = new Intent(CategoryActivity.this, ProductActivity.class);
                intent.putExtra("CATEGORY", selectedCategory);
                startActivity(intent);
            }
        });
        lvCategory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showActionDialog(position);
                return true;
            }
        });
    }

    private void showActionDialog(int position) {
        Category category = categories.get(position);
        String[] options = {"Sửa (Edit)", "Xóa (Delete)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn thao tác");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    processEditCategory(category);
                } else if (which == 1) {
                    processRemoveCategory(position);
                }
            }
        });
        builder.show();
    }

    private void processEditCategory(Category category) {
        Intent intent = new Intent(this, CategoryNewActivity.class);
        intent.putExtra("EDIT_CATEGORY", category);
        startActivityForResult(intent, 1);
    }

    private void processRemoveCategory(int position) {
        Category category = categories.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete category: " + category.getCateName() + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = CategoryDAO.deleteCategory(CategoryActivity.this, category.getCateId());
                if (result > 0) {
                    Toast.makeText(CategoryActivity.this, "Delete success!", Toast.LENGTH_SHORT).show();
                    refreshData();
                } else {
                    Toast.makeText(CategoryActivity.this, "Delete fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void refreshData() {
        categories = CategoryDAO.getCategories(this);
        categoryAdapter.clear();
        categoryAdapter.addAll(categories);
        categoryAdapter.notifyDataSetChanged();
    }

    private void addViews() {
       toolbarCategory = findViewById(R.id.toolbarCategory);
       setSupportActionBar(toolbarCategory);

       lvCategory=findViewById(R.id.lvCategory);
       categories= CategoryDAO.getCategories(this);
       categoryAdapter=new CategoryAdapter(this,R.layout.categoty_custom_item);
       categoryAdapter.addAll(categories);
       lvCategory.setAdapter(categoryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.mnuCategoryNew)
        {
            //open Category new Activity
            Intent intent=new Intent(this,CategoryNewActivity.class);
            startActivityForResult(intent, 1);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==2)
        {
            //process cancel ... nothing to do
        }
        else if(requestCode==1 && resultCode==3)
        {
            refreshData();
        }
    }
}
