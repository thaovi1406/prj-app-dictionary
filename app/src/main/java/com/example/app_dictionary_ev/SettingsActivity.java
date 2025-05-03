package com.example.app_dictionary_ev;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_dictionary_ev.DatabaseHelper;
import com.example.app_dictionary_ev.HistoryDatabaseHelper;
import android.database.sqlite.SQLiteDatabase;

public class SettingsActivity extends AppCompatActivity {

    private TextView speedTextView;
    private final String[] speedOptions = {"Nhanh", "Chậm", "Bình thường"};
    private final float[] speedValues = {1.5f, 0.5f, 1.0f};
    private SharedPreferences prefs;
    private Switch switchAutoPlay;
    private DatabaseHelper databaseHelper;  // Giả sử bạn có DatabaseHelper để làm việc với CSDL
    private HistoryDatabaseHelper historyDatabaseHelper; // Thêm đối tượng HistoryDatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        CustomHeader customHeader = findViewById(R.id.customHeader);
        customHeader.setTitle("Cài đặt");

        speedTextView = findViewById(R.id.speedTextView);
        prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        float speed = prefs.getFloat("speed", 1.0f);
        speedTextView.setText(getSpeedText(speed));

        ImageButton btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        switchAutoPlay = findViewById(R.id.switchAutoPlay);
        boolean isAutoPlayEnabled = prefs.getBoolean("autoPlayEnabled", false);
        switchAutoPlay.setChecked(isAutoPlayEnabled);
        switchAutoPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("autoPlayEnabled", isChecked);
            editor.apply();

            String message = isChecked ? "Tự động phát âm: Bật" : "Tự động phát âm: Tắt";
            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
        });

        // Khởi tạo DatabaseHelper và HistoryDatabaseHelper
        databaseHelper = new DatabaseHelper(this);
        historyDatabaseHelper = new HistoryDatabaseHelper(this);  // Khởi tạo HistoryDatabaseHelper
    }

    private String getSpeedText(float speed) {
        if (speed == 1.5f) {
            return "Nhanh";
        } else if (speed == 0.5f) {
            return "Chậm";
        }
        return "Bình thường";
    }

    // Hàm chọn tốc độ phát âm
    public void onSpeedClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn tốc độ phát âm");
        builder.setItems(speedOptions, (dialog, which) -> {
            Float selectedSpeed = speedValues[which];
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("speed", selectedSpeed);
            editor.apply();

            speedTextView.setText(speedOptions[which]);
            Toast.makeText(this, "Tốc độ phát âm: " + speedOptions[which], Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // Hàm xóa dữ liệu trong các bảng
    public void onRestoreDatabaseClick(View view) {
        // Xóa dữ liệu từ bảng yêu cầu
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        SQLiteDatabase historyDb = historyDatabaseHelper.getWritableDatabase(); // Dùng đối tượng historyDatabaseHelper

        // Giả sử bạn có các bảng như favorites và history
        db.delete(DatabaseHelper.TABLE_NAME, null, null);  // Xóa dữ liệu trong bảng favorites
        historyDb.delete(HistoryDatabaseHelper.TABLE_HISTORY, null, null);  // Xóa dữ liệu trong bảng history

        // Thông báo người dùng đã xóa thành công
        Toast.makeText(this, "Dữ liệu đã được khôi phục!", Toast.LENGTH_SHORT).show();
    }
}
