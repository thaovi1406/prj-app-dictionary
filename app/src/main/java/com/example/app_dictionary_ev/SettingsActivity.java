package com.example.app_dictionary_ev;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;


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
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isAutoPlayEnabled = prefs.getBoolean("autoPlayEnabled", false);
        switchAutoPlay.setChecked(isAutoPlayEnabled);
        switchAutoPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("autoPlayEnabled", isChecked);
                editor.apply();

            }
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
    public void onSpeedClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn tốc độ phát âm");
        builder.setItems(speedOptions,(dialog, which) -> {
            Float selectedSpeed = speedValues[which];

            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("speed", selectedSpeed);
            editor.apply();

            speedTextView.setText(speedOptions[which]);
            Toast.makeText(this, "Tốc độ phát âm: "+ speedOptions[which], Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
    public void onRestoreDatabaseClick(View view) {
        // Tạo dialog xác nhận
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn khôi phục dữ liệu? Tất cả dữ liệu sẽ bị xóa.")
                .setPositiveButton("Xác Nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nếu chọn "Có", thực hiện xóa dữ liệu
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        SQLiteDatabase historyDb = historyDatabaseHelper.getWritableDatabase();

                        db.delete(DatabaseHelper.TABLE_FAVORITES, null, null);
                        db.delete(DatabaseHelper.TABLE_TRANSLATION_HISTORY, null, null);
                        historyDb.delete(HistoryDatabaseHelper.TABLE_HISTORY, null, null);

                        Toast.makeText(getApplicationContext(), "Dữ liệu đã được khôi phục!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null) // Nếu chọn "Không", đóng dialog
                .show();
    }

}