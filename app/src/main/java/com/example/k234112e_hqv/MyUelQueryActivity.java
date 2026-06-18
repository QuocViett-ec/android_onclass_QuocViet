package com.example.k234112e_hqv;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyUelQueryActivity extends AppCompatActivity {
    private static final int REQUEST_AUDIO_PERMISSION = 100;
    private static final int REQUEST_VOICE = 101;

    EditText edtQuery;
    ImageButton btnVoice;
    Button btnGetData;
    TextView txtResult;
    List<TrainingProgram> programs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_uel_query);
        initViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        edtQuery = findViewById(R.id.edtQuery);
        btnVoice = findViewById(R.id.btnVoice);
        btnGetData = findViewById(R.id.btnGetData);
        txtResult = findViewById(R.id.txtResult);
        programs = MyUelMockData.getTrainingPrograms();

        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAudioPermission()) {
                    startVoiceRecognition();
                } else {
                    requestAudioPermission();
                }
            }
        });

        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processQuery();
            }
        });
    }

    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_AUDIO_PERMISSION);
    }

    private void startVoiceRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            showErrorMessage("Thiết bị không hỗ trợ nhận dạng giọng nói (cần Google App)");
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "vi-VN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói truy vấn MYUEL (VD: học kỳ 1 thương mại điện tử)");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        try {
            startActivityForResult(intent, REQUEST_VOICE);
        } catch (ActivityNotFoundException ex) {
            showErrorMessage("Không tìm thấy ứng dụng nhận dạng giọng nói");
        }
    }

    private void handleVoiceResult(Intent data) {
        if (data == null) {
            showErrorMessage("Không nhận dạng được giọng nói");
            return;
        }

        ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (results == null || results.isEmpty()) {
            showErrorMessage("Không nhận dạng được giọng nói");
            return;
        }

        edtQuery.setText(results.get(0));
        edtQuery.setSelection(edtQuery.getText().length());
        // Auto-search after voice input
        processQuery();
    }

    private void processQuery() {
        String query = edtQuery.getText().toString().trim();
        if (query.isEmpty()) {
            showErrorMessage("Please enter or speak a query");
            return;
        }

        TrainingProgram program = VectorSearchUtil.findNearestProgram(query, programs);
        if (program == null) {
            txtResult.setText("⚠️ Không tìm thấy chương trình đào tạo phù hợp.");
            return;
        }

        SemesterProgram semester = VectorSearchUtil.findNearestSemester(query, program);
        if (semester == null) {
            txtResult.setText("⚠️ Không tìm thấy học kỳ phù hợp.");
            return;
        }

        displayResult(query, program, semester);
    }

    private void displayResult(String query, TrainingProgram program, SemesterProgram semester) {
        int totalCredits = 0;
        for (Subject s : semester.getSubjects()) totalCredits += s.getCredits();

        StringBuilder builder = new StringBuilder();
        builder.append("✅ Kết quả từ MYUEL\n");
        builder.append("══════════════════════════════\n\n");
        builder.append("🔍 Từ khóa:\n   ").append(query).append("\n\n");
        builder.append("🎓 Chương trình:\n   ").append(program.getProgramName()).append("\n\n");
        builder.append("📌 Học kỳ khớp:\n   ").append(semester.getSemesterName()).append("\n");
        builder.append("   (").append(semester.getSubjects().size()).append(" môn | ").append(totalCredits).append(" tín chỉ)");
        builder.append("\n\n");
        builder.append("📚 Danh sách môn học:\n");
        builder.append("   ─────────────────────\n");

        for (int i = 0; i < semester.getSubjects().size(); i++) {
            Subject subject = semester.getSubjects().get(i);
            builder.append("   ").append(i + 1).append(". ").append(subject.getName())
                    .append("\n      [").append(subject.getCode()).append("] - ")
                    .append(subject.getCredits()).append(" tín chỉ\n");
        }

        builder.append("\n🔗 Link MYUEL:\n   ").append(program.getUrl());
        builder.append("\n\n🧠 Vector similarity:\n");
        builder.append("   Euclidean dist: ").append(String.format(Locale.US, "%.4f", VectorSearchUtil.lastEuclideanDistance));
        builder.append("\n   Cosine sim: ").append(String.format(Locale.US, "%.4f", VectorSearchUtil.lastCosineSimilarity));
        txtResult.setText(builder.toString());
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VOICE) {
            if (resultCode == RESULT_OK) {
                handleVoiceResult(data);
            } else {
                showErrorMessage("Đã hủy nhập giọng nói");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecognition();
            } else {
                showErrorMessage("RECORD_AUDIO permission denied");
            }
        }
    }
}
