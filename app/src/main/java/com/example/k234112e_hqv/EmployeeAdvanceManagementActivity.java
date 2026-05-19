package com.example.k234112e_hqv;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adapters.EmployeeAdapter;
import com.example.models.Employee;

import java.util.ArrayList;

public class EmployeeAdvanceManagementActivity extends AppCompatActivity {
    ListView lvEmployee;
    ArrayList<Employee>listOfEmployee;
    EmployeeAdapter adapterEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_advance_management);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews(){
        lvEmployee=findViewById(R.id.lvEmployee);
        listOfEmployee=new ArrayList<>();
        adapterEmployee=new EmployeeAdapter(this,R.layout.item_custome_employee);
        listOfEmployee.add(new Employee("E001","John Doe","123456789"));
        listOfEmployee.add(new Employee("E002","Jane Smith","987654321"));
        listOfEmployee.add(new Employee("E003","Alice Johnson","555555555"));
        listOfEmployee.add(new Employee("E004","Quoc Viet","0123456789"));
        listOfEmployee.add(new Employee("E005","Quoc Sang","0987654321"));
        adapterEmployee.addAll(listOfEmployee);
        lvEmployee.setAdapter(adapterEmployee);
        adapterEmployee.notifyDataSetChanged();
    }
}