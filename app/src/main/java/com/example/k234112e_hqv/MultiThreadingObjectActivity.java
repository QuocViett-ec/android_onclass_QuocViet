package com.example.k234112e_hqv;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.models.DataWareHouse;
import com.example.models.Product;

import java.util.ArrayList;

public class MultiThreadingObjectActivity extends AppCompatActivity {
    EditText edtNumberOfProduct;
    Button btnDownload;
    TextView txtPercent;
    ProgressBar progressBarPercent;
    ListView lvProduct;
    ArrayList<Product>products;
    ArrayAdapter<Product>adapterProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_multi_threading_object);
        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processDownloadProductLongTime();
            }
        });
        lvProduct.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
                processRemoveProduct(i);
                return false;
            }
        });
    }

    private void processRemoveProduct(int i) {
        products.remove(i);
        adapterProduct.notifyDataSetChanged();
    }

    // khai bao main thread
    Handler mainThread = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            int percent = message.arg1;
            txtPercent.setText(percent+"%");
            progressBarPercent.setProgress(percent);
            if(message.obj!=null)
            {
                Product product=(Product) message.obj;
                products.add(product);
                adapterProduct.notifyDataSetChanged();
            }
            if(percent==100)
            {
                Toast.makeText(MultiThreadingObjectActivity.this,
                        "Download complete", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });
    private void processDownloadProductLongTime() {
        int n = Integer.parseInt(edtNumberOfProduct.getText().toString());
        //khai báo tiểu trình để chạy longtime:
        Thread thread = new Thread(new Runnable() {
            @Override //khong duoc tuong tac bien nao trong lieu trinh
            public void run() {
                for (int i=0;i<n;i++)
                {
                    // giả lập tải dữ liệu trên mạng(mà bị long time)
                    Product product = DataWareHouse.downloadProduct(i);
                    int percent = (i+1)*100/n;
                    //lấy msg từ main thread
                    Message message = mainThread.obtainMessage();
                    // gán dữ liệu cho message
                    message.arg1 = 1;
                    message.obj = product;
                    // gửi thông điệp cho main Thread
                    mainThread.sendMessage(message);
                    //sau đó cần tạm pause để tiên trình khác có thể xen vào làm
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                //nên gửi thêm thông điệp final
                Message finalMessage = mainThread.obtainMessage();
                finalMessage.arg1 = 100; // báo hiệu đã tải xong
                mainThread.sendMessage(finalMessage);
            }
        });
        thread.start();
    }

    private void addViews() {
        edtNumberOfProduct=findViewById(R.id.edtNumberOfProduct);
        btnDownload=findViewById(R.id.btnDownload);
        txtPercent=findViewById(R.id.txtPercent);
        progressBarPercent=findViewById(R.id.progressBarPercent);
        lvProduct=findViewById(R.id.lvProduct);
        products=new ArrayList<>();
        adapterProduct=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,products);
        lvProduct.setAdapter(adapterProduct);
    }
}