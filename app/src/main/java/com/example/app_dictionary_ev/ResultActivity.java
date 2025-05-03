//package com.example.app_dictionary_ev;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.speech.tts.TextToSpeech;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import java.util.Locale;
//
//public class ResultActivity extends AppCompatActivity {
//    private DatabaseHelper dbHelper;
//    private boolean isFavorite = false;
//    private TextToSpeech textToSpeech;
//    private boolean isAutoPlayEnabled = false;
//    private String word;
//    private ImageButton iHeart;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.search_result);
//
//        TextView tvWord = findViewById(R.id.tvWord);
//        TextView tvPronounce = findViewById(R.id.tvPronounce);
//        TextView tvPos = findViewById(R.id.tvPos);
//        TextView tvMeaning = findViewById(R.id.tvMeaning);
//        iHeart = findViewById(R.id.iHeart);
//
//        Intent intent = getIntent();
//        word = intent.getStringExtra("word");
//        String pronounce = intent.getStringExtra("pronounce");
//        String pos = intent.getStringExtra("pos");
//        String meaning = intent.getStringExtra("meaning");
//
//        tvWord.setText(word);
//        tvPronounce.setText(pronounce);
//        tvPos.setText(pos);
//        tvMeaning.setText(meaning);
//
//        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
//        isAutoPlayEnabled = prefs.getBoolean("autoPlayEnabled", false);
//
//        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status == TextToSpeech.SUCCESS) {
//                    textToSpeech.setLanguage(Locale.US);
//                    if (isAutoPlayEnabled && word != null) {
//                        speakWord(word);
//                    }
//                } else {
//                    Toast.makeText(ResultActivity.this, "TextToSpeech không khả dụng", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        ImageButton btnBack = findViewById(R.id.btnHome);
//        btnBack.setOnClickListener(v -> {
//            Intent home = new Intent(ResultActivity.this, MainActivity.class);
//            startActivity(home);
//            finish();
//        });
//
//        dbHelper = new DatabaseHelper(this);
//        String word = tvWord.getText().toString();
//        isFavorite = dbHelper.isWordFavorite(word);
//        updateHeartIcon();
//        iHeart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String word = tvWord.getText().toString();
//                String pronunciation = tvPronounce.getText().toString();
//                String type = "("+tvPos.getText().toString()+")";
//                String meaning ="➜" + tvMeaning.getText().toString();
//
//                if (isFavorite) {
//                    if (dbHelper.removeFavoriteWord(word)) {
//                        Toast.makeText(ResultActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
//                        isFavorite = false;
//                    }
//                } else {
//                    if (dbHelper.addFavoriteWord(word, pronunciation, type, meaning)) {
//                        Toast.makeText(ResultActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
//                        isFavorite = true;
//                    }
//                }
//                updateHeartIcon();
//            }
//        });
//    }
//    private void updateHeartIcon() {
//        if (isFavorite) {
//            iHeart.setImageResource(R.drawable.ic_heart_filled);
//        } else {
//            iHeart.setImageResource(R.drawable.ic_heart);
//        }
//    }
//
//    private void speakWord(String word) {
//        if (textToSpeech != null) {
//            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (textToSpeech != null) {
//            textToSpeech.stop();
//            textToSpeech.shutdown();
//        }
//    }
//}

package com.example.app_dictionary_ev;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private boolean isFavorite = false;
    private TextToSpeech textToSpeech;
    private boolean isAutoPlayEnabled = false;
    private String word;
    private ImageButton iHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ResultActivity", "onCreate called");
        setContentView(R.layout.search_result);

        CustomHeader customHeader = findViewById(R.id.customHeader);
        customHeader.setTitle("");

        TextView tvWord = findViewById(R.id.tvWord);
        TextView tvPronounce = findViewById(R.id.tvPronounce);
        TextView tvPos = findViewById(R.id.tvPos);
        TextView tvMeaning = findViewById(R.id.tvMeaning);
        iHeart = findViewById(R.id.iHeart);

        Intent intent = getIntent();
        word = intent.getStringExtra("word");
        String pronounce = intent.getStringExtra("pronounce");
        String pos = intent.getStringExtra("pos");
        String meaning = intent.getStringExtra("meaning");

        tvWord.setText(word);
        tvPronounce.setText(pronounce);
        tvPos.setText(pos);
        tvMeaning.setText(meaning);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        isAutoPlayEnabled = prefs.getBoolean("autoPlayEnabled", false);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Áp dụng tốc độ từ SharedPreferences
                    float speed = prefs.getFloat("speed", 1.0f); // Mặc định 1.0f
                    textToSpeech.setSpeechRate(speed);

                    textToSpeech.setLanguage(Locale.US);
                    if (isAutoPlayEnabled && word != null) {
                        speakWord(word);
                    }
                } else {
                    Toast.makeText(ResultActivity.this, "TextToSpeech không khả dụng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton btnBack = findViewById(R.id.btnHome);
        btnBack.setOnClickListener(v -> {
            Intent home = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(home);
            finish();
        });

        dbHelper = new DatabaseHelper(this);
        String word = tvWord.getText().toString();
        isFavorite = dbHelper.isWordFavorite(word);
        updateHeartIcon();
        iHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = tvWord.getText().toString();
                String pronunciation = tvPronounce.getText().toString();
                String type = "("+tvPos.getText().toString()+")";
                String meaning ="➜" + tvMeaning.getText().toString();

                if (isFavorite) {
                    if (dbHelper.removeFavoriteWord(word)) {
                        Toast.makeText(ResultActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        isFavorite = false;
                    }
                } else {
                    if (dbHelper.addFavoriteWord(word, pronunciation, type, meaning)) {
                        Toast.makeText(ResultActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                        isFavorite = true;
                    }
                }
                updateHeartIcon();
            }
        });

        // Xử lý phát âm khi nhấn nút loa (iSpeaker)
        ImageButton iSpeaker = findViewById(R.id.iSpeaker);
        iSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakWord(word);
            }
        });
    }

    private void updateHeartIcon() {
        if (isFavorite) {
            iHeart.setImageResource(R.drawable.ic_heart_filled);
        } else {
            iHeart.setImageResource(R.drawable.ic_heart);
        }
    }

    private void speakWord(String word) {
        if (textToSpeech != null) {
            // Cập nhật âm lượng tối đa
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

            // Cập nhật TextToSpeech
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1.0"); // Max volume
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, params);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
