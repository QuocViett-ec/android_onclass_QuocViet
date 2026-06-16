package com.example.k234112e_hqv;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k234112e_hqv.adapter.RaoVatAdapter;
import com.example.k234112e_hqv.model.RaoVatItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RauVatActivity extends AppCompatActivity {

    private static final String API_URL = "https://raovat.tuoitre.vn/api/list/list-top-ttorv";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private View layoutError;
    private TextView tvError;
    private TextView tvTotalItems;
    private TextView tvStatusText;
    private TextView tvPageInfo;
    private View btnRetry;
    private View btnBack;

    private RaoVatAdapter adapter;
    private List<RaoVatItem> itemList = new ArrayList<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rau_vat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        layoutError = findViewById(R.id.layoutError);
        tvError = findViewById(R.id.tvError);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvStatusText = findViewById(R.id.tvStatusText);
        tvPageInfo = findViewById(R.id.tvPageInfo);
        btnRetry = findViewById(R.id.btnRetry);
        btnBack = findViewById(R.id.btnBack);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RaoVatAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        // Smooth scroll animation
        recyclerView.setHasFixedSize(false);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Retry button
        btnRetry.setOnClickListener(v -> loadData());
    }

    private void loadData() {
        showLoading(true);

        executor.execute(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    connection.disconnect();

                    String jsonResponse = sb.toString();
                    parseAndDisplay(jsonResponse);
                } else {
                    connection.disconnect();
                    mainHandler.post(() -> showError("Lỗi server: " + responseCode));
                }

            } catch (Exception e) {
                mainHandler.post(() -> showError("Không có kết nối mạng hoặc API lỗi:\n" + e.getMessage()));
            }
        });
    }

    private void parseAndDisplay(String json) {
        try {
            JSONObject root = new JSONObject(json);
            boolean success = root.optBoolean("success", false);
            int totalItems = root.optInt("total_items", 0);
            int page = root.optInt("page", 1);
            String message = root.optString("message", "");

            JSONArray items = root.optJSONArray("items");
            List<RaoVatItem> newList = new ArrayList<>();

            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject obj = items.getJSONObject(i);
                    RaoVatItem item = new RaoVatItem(
                            obj.optString("title", ""),
                            obj.optString("thumb", ""),
                            obj.optString("url", ""),
                            obj.optString("price", ""),
                            obj.optString("location", "")
                    );
                    newList.add(item);
                }
            }

            mainHandler.post(() -> {
                itemList.clear();
                itemList.addAll(newList);
                adapter.notifyDataSetChanged();

                // Update stats
                tvTotalItems.setText(String.valueOf(totalItems));
                tvStatusText.setText(success ? "Thành công ✓" : "Lỗi");
                tvPageInfo.setText(String.valueOf(page));

                showLoading(false);

                if (newList.isEmpty()) {
                    showError("Không có dữ liệu");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    layoutError.setVisibility(View.GONE);
                    // Fade-in animation
                    recyclerView.startAnimation(
                            AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                }
            });

        } catch (Exception e) {
            mainHandler.post(() -> showError("Lỗi phân tích dữ liệu: " + e.getMessage()));
        }
    }

    private void showLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            tvStatusText.setText("Đang tải...");
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvError.setText(message);
        tvStatusText.setText("Lỗi ✗");
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}