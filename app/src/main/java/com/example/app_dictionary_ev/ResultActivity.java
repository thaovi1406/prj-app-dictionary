package com.example.app_dictionary_ev;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private boolean isAutoPlayEnabled = false;
    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        TextView tvWord = findViewById(R.id.tvWord);
        TextView tvPronounce = findViewById(R.id.tvPronounce);
        TextView tvPos = findViewById(R.id.tvPos);
        TextView tvMeaning = findViewById(R.id.tvMeaning);

        Intent intent = getIntent();
        word = intent.getStringExtra("word");
        String pronounce = intent.getStringExtra("pronounce");
        String pos = intent.getStringExtra("pos");
        String meaning = intent.getStringExtra("meaning");

        tvWord.setText(word);
        tvPronounce.setText(pronounce);
        tvPos.setText(pos);
        tvMeaning.setText("➜ " + meaning);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        isAutoPlayEnabled = prefs.getBoolean("autoPlayEnabled", false);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
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
    }

    private void speakWord(String word) {
        if (textToSpeech != null) {
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
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