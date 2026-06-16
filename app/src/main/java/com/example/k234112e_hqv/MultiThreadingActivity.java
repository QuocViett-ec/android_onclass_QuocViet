package com.example.k234112e_hqv;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MultiThreadingActivity extends AppCompatActivity {
    EditText edtNumberButton;
    TextView txtPercent;
    ProgressBar progressBar;
    LinearLayout linearLayoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_multi_threading);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews()
    {
        edtNumberButton=findViewById(R.id.edtNumberButton);
        txtPercent=findViewById(R.id.txtPercent);
        progressBar=findViewById(R.id.progressBarPercent);
        linearLayoutButton=findViewById(R.id.linearLayoutButton);
    }

    //Khai báo MainThread (tiến trình chính để visualization giao diện)
    Handler mainThread=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            //message này sẽ được tiểu trình bắn về cho MainThread
            int value=message.arg1;
            int percent=message.arg2;
            txtPercent.setText(percent+"%");
            progressBar.setProgress(percent);
            Button btn=new Button(MultiThreadingActivity.this);
            btn.setWidth(300);
            btn.setHeight(50);
            btn.setText(value+"");
            linearLayoutButton.addView(btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button b=(Button) view;
                    b.setTextColor(Color.RED);
                }
            });
            return false;
        }
    });

    public void processMultiThreading(View view) {
        int numberButton=Integer.parseInt(edtNumberButton.getText().toString());
        //khai báo tiểu trình:
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                //xử lý long time task
                Random random=new Random();
                for(int i=1;i<=numberButton;i++)
                {
                    //tạo giá trị ngẫu nhiên:
                    int value=random.nextInt(500);
                    int percent=(int)((i*1.0/numberButton)*100);
                    //lấy thông điệp từ MainThread
                    Message message=mainThread.obtainMessage();
                    message.arg1=value;
                    message.arg2=percent;
                    //gửi thông điệp về MainThread
                    mainThread.sendMessage(message);
                    try {
                        //cần có thời gian nghỉ do làm Long Time
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}