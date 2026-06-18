package com.example.k234112e_hqv;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class FontAndMusicActivity extends AppCompatActivity {
    private static final String LOG_TAG = "FontAndMusicActivity";

    Button btnPlayAudio1;
    Button btnPlayAudio2;
    ListView lvFont;
    TextView txtTitle;
    ArrayList<String> fonts;
    ArrayAdapter<String> fontAdapter;
    MediaPlayer mediaPlayer;
            
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_font_and_music);
        addViews();
        addEvents();

        View main = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadFonts(){
        try {
            AssetManager assetManager = getAssets();
            String[] arrFonts = assetManager.list("font");
            fonts.clear();
            if (arrFonts != null) {
                for (String font : arrFonts) {
                    fonts.add(font);
                }
            }
            fontAdapter.notifyDataSetChanged();
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }
    }

    private void addEvents() {
        btnPlayAudio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio("music/audio1.mp3");
            }
        });
        btnPlayAudio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio("music/audio2.mp3");

            }
        });
        lvFont.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeFont(position);
            }
        });
    }

    private void playAudio(String audioPath) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(audioPath);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(
                    assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            assetFileDescriptor.close();
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
        }
    }

    private void changeFont(int position) {
        String fontFileName = fonts.get(position);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/" + fontFileName);
        txtTitle.setTypeface(typeface);
    }

    private void addViews() {
        btnPlayAudio1=findViewById(R.id.btnPlayAudio1);
        btnPlayAudio2=findViewById(R.id.btnPlayAudio2);
        txtTitle=findViewById(R.id.txtTitle);
        lvFont=findViewById(R.id.lvFont);
        fonts=new ArrayList<>();
        fontAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,fonts);
        lvFont.setAdapter(fontAdapter);
        loadFonts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
