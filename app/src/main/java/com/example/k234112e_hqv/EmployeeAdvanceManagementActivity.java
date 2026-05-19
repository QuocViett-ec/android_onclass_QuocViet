package com.example.k234112e_hqv;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adapters.EmployeeAdapter;
import com.example.models.Department;
import com.example.models.Employee;

import java.util.ArrayList;

public class EmployeeAdvanceManagementActivity extends AppCompatActivity {
    ListView lvEmployee;
    ArrayList<Employee>listOfEmployee;
    EmployeeAdapter adapterEmployee;
    Spinner spDepartment;
    ArrayList<Department> lisOfDepartment;
    ArrayAdapter<Department> adapterDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_advance_management);
        addViews();
        sampleData();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvent(){
        spDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Department selectedDepartment = lisOfDepartment.get(i);
                adapterEmployee.clear();
                adapterEmployee.addAll(selectedDepartment.getListOfEmployee());
                adapterEmployee.notifyDataSetChanged();
            }
        });
    }

    private void sampleData() {

        Department d0=new Department("D000","-----All-----");
        Department d1=new Department("D001","HR");
        Department d2=new Department("D002","IT");
        Department d3=new Department("D003","Finance");
        Department d4=new Department("D004","SCM");

        lisOfDepartment.add(d0);
        lisOfDepartment.add(d1);
        lisOfDepartment.add(d2);
        lisOfDepartment.add(d3);
        lisOfDepartment.add(d4);
        adapterDepartment.notifyDataSetChanged();

        d1.addEmployee(new Employee("E001","John Doe","123456789"));
        d1.addEmployee(new Employee("E002","Jane Smith","987654321"));
        d2.addEmployee(new Employee("E003","Alice Johnson","555555555"));
        d2.addEmployee(new Employee("E004","Quoc Viet","0123456789"));
        d3.addEmployee(new Employee("E005","Quoc Sang","0987654321"));
        d3.addEmployee(new Employee("E005","Quoc Sang","0987654321"));
        d3.addEmployee(new Employee("E005","Quoc Sang","0987654321"));
        d3.addEmployee(new Employee("E005","Quoc Sang","0987654321"));

        ArrayList<Employee> lisOfEmp4=new ArrayList<>();
        lisOfEmp4.add(new Employee("E001","John Doe","123456789"));
        lisOfEmp4.add(new Employee("E002","Jane Smith","987654321"));
        lisOfEmp4.add(new Employee("E003","Alice Johnson","555555555"));
        lisOfEmp4.add(new Employee("E004","Quoc Viet","012345678"));
        d4.addListEmployee(lisOfEmp4);


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

        spDepartment=findViewById(R.id.spDepartment);
        lisOfDepartment=new ArrayList<>();
        adapterDepartment=new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,lisOfDepartment);
        adapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartment.setAdapter(adapterDepartment);
    }
}