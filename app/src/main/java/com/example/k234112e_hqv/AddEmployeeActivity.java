package com.example.k234112e_hqv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.Employee;

import java.util.ArrayList;

public class AddEmployeeActivity extends AppCompatActivity {

    EditText edt_id,edt_name,edt_phone;
    ImageView img_yes,img_no;
    AutoCompleteTextView act_birth_place;
    ArrayList<String> listBirthPlace;
    ArrayAdapter<String> adapterBirthPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_employee);
        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() 
    {
        img_yes.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View view) {
                processAddNewEmployee();
            }
        });
        img_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void processAddNewEmployee() {
        String id=edt_id.getText().toString();
        String name=edt_name.getText().toString();
        String phone=edt_phone.getText().toString();
        String birthPlace=act_birth_place.getText().toString();

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty() || birthPlace.isEmpty()) {
            String msg=getResources().getString(R.string.str_please_input_full_info);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        Employee emp=new Employee(id,name,phone);
        //create result intent
        Intent intent=new Intent();
        //put data to intent
        intent.putExtra("New_Employee",emp);
        //set result
        setResult(888,intent);
        //finish activity     
        finish();
    }

    private void addViews() {
        edt_id=findViewById(R.id.edt_id);
        edt_name=findViewById(R.id.edt_name);
        edt_phone=findViewById(R.id.edt_phone);
        img_yes=findViewById(R.id.img_yes);
        img_no=findViewById(R.id.img_no);
        act_birth_place=findViewById(R.id.act_birth_place);
        
        String []arrBirthplace=getResources().getStringArray(R.array.array_birth_place);
        adapterBirthPlace=new ArrayAdapter<>(this, 
                android.R.layout.simple_list_item_1,arrBirthplace);
        act_birth_place.setAdapter(adapterBirthPlace);
    }

}