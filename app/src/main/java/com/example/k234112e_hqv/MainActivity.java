package com.example.k234112e_hqv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.UserAccount;

import java.time.Instant;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        //get intent
        Intent intent=getIntent();
        //get data
        UserAccount uc=(UserAccount) intent.getSerializableExtra("User_Login");
        if(uc!=null)        {
            String welcome = getResources().getString(R.string.str_welcome);
            Toast.makeText(this, welcome + " " + uc.getDisplayName(), Toast.LENGTH_SHORT).show();
            TextView txtWelcome=findViewById(R.id.txtWelcome);
            txtWelcome.setText(welcome);
        }
    }

    public void say_hello(View view) {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }

    public void close(View view) {
        finish();
    }
    public void  click_me(View view){
        String welcome = getResources().getString(R.string.str_welcome);
        Toast.makeText(this, welcome, Toast.LENGTH_SHORT).show();
    }

    public void open_calculator(View view) {
        Intent intent = new Intent(this, CalculatorActivity.class);
        startActivity(intent);
    }
}