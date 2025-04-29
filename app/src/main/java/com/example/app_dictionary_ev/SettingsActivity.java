package com.example.app_dictionary_ev;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;



public class SettingsActivity extends AppCompatActivity {

    private Switch switchAutoPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup thanh tiêu đề
        TextView tvTitle = findViewById(R.id.tvTitle);
        ImageView ivBack = findViewById(R.id.ivBack);

        tvTitle.setText("Cài đặt"); // Đặt tiêu đề thành "Cài đặt"

        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Các phần code khác của bạn
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

                if (isChecked) {
                    Toast.makeText(SettingsActivity.this, "Tự động phát âm: Bật", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Tự động phát âm: Tắt", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}