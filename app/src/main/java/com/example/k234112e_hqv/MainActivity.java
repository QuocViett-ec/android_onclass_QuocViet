package com.example.k234112e_hqv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.UserAccount;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

public class MainActivity extends AppCompatActivity {
    public static final String DATABASE_NAME = "k234112eSales.db";
    public static final String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        processCopy();
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        Button btnFirebaseContactCrud = findViewById(R.id.btnFirebaseContactCrud);
        btnFirebaseContactCrud.setOnClickListener(v -> {
            Intent intent = new Intent(this, FirebaseContactListActivity.class);
            startActivity(intent);
        });

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

    private void processCopy() {
        try {
            File dbFile = getDatabasePath(DATABASE_NAME);
            if (!dbFile.exists()) {
                if (CopyDBFromAsset()) {
                    Toast.makeText(MainActivity.this, "Copy database successful!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Copy database fail!", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Log.e("Error: ", e.toString());
        }
    }

    private boolean CopyDBFromAsset() {
        String dbPath = getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
        try {
            InputStream inputStream = getAssets().open(DATABASE_NAME);
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists()) {
                f.mkdirs();
            }
            OutputStream outputStream = new FileOutputStream(dbPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void open_smsspyware(View view) {
        Intent intent = new Intent(this, SMSSypewareActivity.class);
        startActivity(intent);
    }

    public void multi_threading_activity(View view) {
        Intent intent = new Intent(this, MultiThreadingActivity.class);
        startActivity(intent);
    }

    public void openMultiThreadingObjectActivity(View view) {
        Intent intent = new Intent(this, MultiThreadingObjectActivity.class);
        startActivity(intent);
    }

    public void rauvat(View view) {
        Intent intent = new Intent(this, RauVatActivity.class);
        startActivity(intent);
    }

    public void openFontAndMusic(View view) {
        Intent intent = new Intent(this, FontAndMusicActivity.class);
        startActivity(intent);
    }

    public void openMyUelQuery(View view) {
        Intent intent = new Intent(this, MyUelQueryActivity.class);
        startActivity(intent);
    }

    public void openFirebaseCrud(View view) {
        Intent intent = new Intent(this, FirebaseCrudActivity.class);
        startActivity(intent);
    }
}
