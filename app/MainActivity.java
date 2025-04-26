package com.example.app_dictionary_ev;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        CardView cardViewHistory = findViewById(R.id.cardView_History);
        cardViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });
        CardView cardViewTranslate = findViewById(R.id.cardView_Docs);
        cardViewTranslate.setOnClickListener(v -> {
            Intent intent = new Intent(this, TranslateTextActivity.class);
            startActivity(intent);
        });

        CardView cardViewSetting = findViewById(R.id.cardView_Settings);
        cardViewSetting.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        CardView cardViewFavourite = findViewById(R.id.cardView_Favourite);
        cardViewFavourite.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);
        });
    }


}