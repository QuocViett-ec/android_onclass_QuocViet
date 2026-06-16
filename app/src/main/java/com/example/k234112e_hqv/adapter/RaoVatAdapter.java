package com.example.k234112e_hqv.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k234112e_hqv.R;
import com.example.k234112e_hqv.model.RaoVatItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RaoVatAdapter extends RecyclerView.Adapter<RaoVatAdapter.ViewHolder> {

    private final List<RaoVatItem> items;
    private final Context context;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public RaoVatAdapter(Context context, List<RaoVatItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_raovat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RaoVatItem item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvPrice.setText(item.getPrice());
        holder.tvLocation.setText(item.getLocation());

        // Reset image
        holder.imgThumb.setImageResource(R.drawable.ic_placeholder);
        holder.imgThumb.setTag(item.getThumb());

        // Load image async
        String imageUrl = item.getThumb();
        executor.execute(() -> {
            Bitmap bitmap = loadBitmap(imageUrl);
            mainHandler.post(() -> {
                // Make sure the view hasn't been recycled
                if (holder.imgThumb.getTag() != null &&
                        holder.imgThumb.getTag().equals(imageUrl)) {
                    if (bitmap != null) {
                        holder.imgThumb.setImageBitmap(bitmap);
                    } else {
                        holder.imgThumb.setImageResource(R.drawable.ic_placeholder);
                    }
                }
            });
        });

        // Click to open URL
        holder.itemView.setOnClickListener(v -> {
            if (item.getUrl() != null && !item.getUrl().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
                context.startActivity(intent);
            }
        });
    }

    private Bitmap loadBitmap(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            connection.disconnect();
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle, tvPrice, tvLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}
