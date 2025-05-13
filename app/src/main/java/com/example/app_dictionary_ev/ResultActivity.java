package com.example.app_dictionary_ev;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private boolean isFavorite = false;
    private TextToSpeech textToSpeech;
    private boolean isAutoPlayEnabled = false;
    private String word;
    private ImageButton iHeart;
    private List<Meaning> meanings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ResultActivity", "onCreate called");
        setContentView(R.layout.search_result);

        // Ánh xạ các view
        TextView tvWord = findViewById(R.id.tvWord);
        TextView tvPronounce = findViewById(R.id.tvPronounce);
        TextView tvPos = findViewById(R.id.tvPos);
        RecyclerView rvMeanings = findViewById(R.id.rvMeanings);
        iHeart = findViewById(R.id.iHeart);
        ImageButton iSpeaker = findViewById(R.id.iSpeaker);
        ImageButton btnBack = findViewById(R.id.btnHome);

        Intent intent = getIntent();
        word = intent.getStringExtra("word");
        String pronunciation = intent.getStringExtra("pronunciation");
        String pos = intent.getStringExtra("pos");
        String meaningsJson = intent.getStringExtra("meanings");



        tvWord.setText(word);
        tvPronounce.setText(pronunciation);
        tvPos.setText(pos);

        meanings = null;
        if (meaningsJson != null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Meaning>>() {}.getType();
                meanings = gson.fromJson(meaningsJson, type);
            } catch (Exception e) {
                Log.e("ResultActivity", "Failed to parse meaningsJson: " + e.getMessage());
            }
        }

        if (meanings != null && !meanings.isEmpty()) {
            rvMeanings.setLayoutManager(new LinearLayoutManager(this));
            MeaningAdapter meaningAdapter = new MeaningAdapter(meanings);
            rvMeanings.setAdapter(meaningAdapter);
        } else {
            rvMeanings.setVisibility(View.GONE);
        }


        // Cài đặt SharedPreferences và TTS
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        isAutoPlayEnabled = prefs.getBoolean("autoPlayEnabled", false);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                float speed = prefs.getFloat("speed", 1.0f);
                textToSpeech.setSpeechRate(speed);

                textToSpeech.setLanguage(Locale.US);
                if (isAutoPlayEnabled && word != null) {
                    speakWord(word);
                }
            } else {
                Toast.makeText(ResultActivity.this, "TextToSpeech không khả dụng", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent home = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(home);
            finish();
        });

        dbHelper = new DatabaseHelper(this);
        word = tvWord.getText().toString();
        isFavorite = dbHelper.isWordFavorite(word);
        updateHeartIcon();
        iHeart.setOnClickListener(v -> {
            String type =tvPos.getText().toString();
            StringBuilder meaningBuilder = new StringBuilder();
            if (meanings != null) {
                for (Meaning meaning : meanings) {
                    meaningBuilder.append("➜ ").append(meaning.getDefinition());
                    if (meaning.getExample() != null && !meaning.getExample().isEmpty()) {
                        meaningBuilder.append("\n").append(meaning.getExample());
                    }
                    if (meaning.getNote() != null && !meaning.getNote().isEmpty()) {
                        meaningBuilder.append("\n").append(meaning.getNote());
                    }
                    meaningBuilder.append("\n");
                }
            }
            String meaningText = meaningBuilder.toString().trim();

            if (isFavorite) {
                if (dbHelper.removeFavoriteWord(word)) {
                    isFavorite = false;
                }
            } else {
                if (dbHelper.addFavoriteWord(word, pronunciation, type, meaningText)) {
                    isFavorite = true;
                }
            }
            updateHeartIcon();
        });

        iSpeaker.setOnClickListener(v -> {
            if (word != null) {
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
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, "1.0");
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