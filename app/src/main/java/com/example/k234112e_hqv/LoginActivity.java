package com.example.k234112e_hqv;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.ListUserAccount;
import com.example.models.UserAccount;

public class LoginActivity extends AppCompatActivity {

    /*
    declare all variable for interactive view
     */
    EditText editTextUsername;
    EditText editTextTextPassword;
    TextView txtMassage;
    Button btn_login;
    Button btn_exit;
    CheckBox chk_SaveLogin;
    String name_share_pref="LoginInfor";
    RadioButton rad_admin,rad_employee;
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
        chk_SaveLogin=findViewById(R.id.chk_SaveLogin);
        rad_admin=findViewById(R.id.rad_admin);
        rad_employee=findViewById(R.id.rad_employee);
    }
    public void loginSystem(View view) {
        String username = editTextUsername.getText().toString();
        String password = editTextTextPassword.getText().toString();
        UserAccount uc= ListUserAccount.Login(username,password);
        if(uc!=null) {
            boolean saved = chk_SaveLogin.isChecked();
            SharedPreferences preferences = getSharedPreferences(name_share_pref, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("UserName", username);
            editor.putString("PassWord", password);
            editor.putBoolean("Saved", saved);
            editor.commit();
            txtMassage.setText(getString(R.string.str_login_success));

            if (rad_admin.isChecked() ){
                // Admin: go to main UI
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("User_Login", uc);
                startActivity(intent);
            } else if (rad_employee.isChecked() ) {
                // Employee: go to employee UI
                Intent intent = new Intent(LoginActivity.this, EmployeeAdvanceManagementActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            txtMassage.setText(getString(R.string.str_login_fail));
            return;
        }
    }
    public void loginSystemOld(View view) {
        String username = editTextUsername.getText().toString();
        String password = editTextTextPassword.getText().toString();
        boolean isAdmin = username.equalsIgnoreCase("admin") && password.equals("123");
        boolean isEmployee = username.equalsIgnoreCase("employee") && password.equals("123");

        if (isAdmin || isEmployee) {

            boolean saved=chk_SaveLogin.isChecked();
            SharedPreferences preferences=getSharedPreferences(name_share_pref,MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("UserName",username);
            editor.putString("PassWord",password);
            editor.putBoolean("Saved",saved);
            editor.commit();

            txtMassage.setText(getString(R.string.str_login_success));

            if (rad_admin.isChecked() && isAdmin) {
                // Admin: go to main UI
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (rad_employee.isChecked() && isEmployee) {
                // Employee: go to employee UI
                Intent intent = new Intent(LoginActivity.this, EmployeeAdvanceManagementActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            txtMassage.setText(getString(R.string.str_login_fail));
        }
    }

    public void exitSystem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Xác nhận thoát");
        builder.setMessage("Bạn có thực sự muốn thoát không?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences=getSharedPreferences(name_share_pref,MODE_PRIVATE);
        String username=preferences.getString("UserName","");
        String password=preferences.getString("PassWord","");
        boolean saved=preferences.getBoolean("Saved",false);
        if(saved)
        {
            editTextUsername.setText(username);
            editTextTextPassword.setText(password);
        }
        chk_SaveLogin.setChecked(saved);

    }
}