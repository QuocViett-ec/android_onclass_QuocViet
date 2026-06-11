package com.example.k234112e_hqv;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.MyContact;

import java.util.ArrayList;

import dals.MyContactDAO;

public class MyContactActivity extends AppCompatActivity {
    ListView lvMyContact;
    ArrayList<MyContact> MyContacts;
    ArrayAdapter<MyContact> AdapterMyContacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_contact);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        lvMyContact=findViewById(R.id.lvMyContact);
        MyContacts = MyContactDAO.getContacts(this);
        AdapterMyContacts = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MyContacts);
        lvMyContact.setAdapter(AdapterMyContacts);
    }
}