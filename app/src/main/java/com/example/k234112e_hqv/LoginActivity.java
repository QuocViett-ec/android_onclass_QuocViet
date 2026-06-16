package com.example.k234112e_hqv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.ListUserAccount;
import com.example.models.UserAccount;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

//copy từ 48-87 sửa lại tên database
    public static final String DATABASE_NAME = "k234112eSales.db";
    public static final String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database = null;
    private void copyDataBase(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    File dbFile= getDatabasePath(DATABASE_NAME);
                    if(!dbFile.exists()){
                        if(CopyDBFromAsset()){
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                    "Copy database successful!", Toast.LENGTH_LONG).show());
                        }else{
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                    "Copy database fail!", Toast.LENGTH_LONG).show());
                        }
                    }
                }catch (Exception e){
                    Log.e("Error: ", e.toString());
                }
            }
        }).start();
    }

    private boolean CopyDBFromAsset() {
        try {
            InputStream inputStream = getAssets().open(DATABASE_NAME);
            File dbFile = getDatabasePath(DATABASE_NAME);
            File dbDir = dbFile.getParentFile();
            if (dbDir != null && !dbDir.exists()) {
                dbDir.mkdirs();
            }
            OutputStream outputStream = new FileOutputStream(dbFile);
            byte[] buffer = new byte[1024]; int length;
            while((length=inputStream.read(buffer))>0){
                outputStream.write(buffer,0, length);
            }
            outputStream.flush();  outputStream.close(); inputStream.close();
            return  true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        addViews();
        copyDataBase();
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
                // Admin: go to order management UI
                //Intent intent = new Intent(LoginActivity.this, OrderManagementActivity.class);
                //intent.putExtra("User_Login", uc);
                //startActivity(intent);
                // Intent intent = new Intent(LoginActivity.this,ProductActivity.class);
                //Intent intent = new Intent(LoginActivity.this,CategoryActivity.class);
                //Intent intent = new Intent(LoginActivity.this,MyContactActivity.class);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

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
                // Admin: go to order management UI
                //Intent intent = new Intent(LoginActivity.this, OrderManagementActivity.class);
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

    public void ExitSystem(View view) {
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
    BroadcastReceiver internetStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {

            }
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    btn_login.setEnabled(true);
                } else {
                    btn_login.setEnabled(false);
                }
            }
        }
    };


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

        IntentFilter intentFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetStateReceiver,intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internetStateReceiver);
    }
}