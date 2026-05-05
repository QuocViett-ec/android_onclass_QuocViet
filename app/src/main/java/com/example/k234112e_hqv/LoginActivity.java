package com.example.k234112e_hqv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    /*
    declare all variable for interactive view
     */
    EditText editTextUsername;
    EditText editTextTextPassword;
    TextView txtMassage;
    Button btn_login;
    Button btn_exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        addViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void addViews(){
        editTextUsername=findViewById(R.id.editTextUsername);
        editTextTextPassword=findViewById(R.id.editTextTextPassword);
        txtMassage=findViewById(R.id.txtMassage);
        btn_login=findViewById(R.id.btn_login);
        btn_exit=findViewById(R.id.btn_exit);
    }
    public void loginSystem(View view) {
        String username = editTextUsername.getText().toString();
        String password = editTextTextPassword.getText().toString();
        if (username.equalsIgnoreCase("admin") && password.equals("123")) {
            txtMassage.setText(getString(R.string.str_login_success));
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
        {
            txtMassage.setText(getString(R.string.str_login_fail));
        }
    }

    public void ExitSystem(View view) {
        finish();
    }
}