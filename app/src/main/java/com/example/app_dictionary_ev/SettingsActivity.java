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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



public class SettingsActivity extends AppCompatActivity {

    private TextView speedTextView;
    private final String[] speedOptions = {"Nhanh", "Chậm", "Bình thường"};
    private final float[] speedValues = {1.5f, 0.5f, 1.0f};
    private SharedPreferences prefs;
    private Switch switchAutoPlay;

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

                if (isChecked) {
                    Toast.makeText(SettingsActivity.this, "Tự động phát âm: Bật", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Tự động phát âm: Tắt", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

}