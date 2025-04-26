package com.example.app_dictionary_ev;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        CardView cardViewSettings = findViewById(R.id.cardView_Settings);
        cardViewSettings.setOnClickListener(v -> {
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