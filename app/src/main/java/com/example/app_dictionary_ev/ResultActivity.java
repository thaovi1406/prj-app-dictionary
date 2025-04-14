package com.example.app_dictionary_ev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        TextView tvWord = findViewById(R.id.tvWord);
        TextView tvPronounce = findViewById(R.id.tvPronounce);
        TextView tvPos = findViewById(R.id.tvPos);
        TextView tvMeaning = findViewById(R.id.tvMeaning);

        Intent intent = getIntent();
        String word = intent.getStringExtra("word");
        String pronounce = intent.getStringExtra("pronounce");
        String pos = intent.getStringExtra("pos");
        String meaning = intent.getStringExtra("meaning");

        tvWord.setText(word);
        tvPronounce.setText(pronounce);
        tvPos.setText(pos);
        tvMeaning.setText("➜" + meaning);

        ImageButton btnBack = findViewById(R.id.btnHome);
        btnBack.setOnClickListener(v -> {
            Intent home = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(home);
            finish();
        });
    }

}
