//package com.example.app_dictionary_ev;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.constraintlayout.widget.ConstraintLayout;
//
//import com.example.app_dictionary_ev.data.db.DatabaseInitializer;
//import com.google.firebase.FirebaseApp;
//
//public class MainActivity extends AppCompatActivity {
//    private ProgressBar progressBar;
//    private ConstraintLayout mainContent;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(this);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//
//        progressBar = findViewById(R.id.progressBar);
//        mainContent = findViewById(R.id.main);
//
//        showLoading(true);
//        // Kiểm tra và tải dữ liệu
//        checkAndLoadData();
//    }
//    private void showLoading(boolean isLoading) {
//        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
//        mainContent.setVisibility(isLoading ? View.GONE : View.VISIBLE);
//    }
//    private void checkAndLoadData() {
//        DatabaseInitializer.populateDatabase(this, new DatabaseInitializer.InitializationCallback() {
//            @Override
//            public void onComplete(int count) {
//                runOnUiThread(() -> {
//                    showLoading(false);
//                    Toast.makeText(MainActivity.this, "Tải dữ liệu thành công", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this,"Đã ghi " + count + " từ vào cơ sở dữ liệu", Toast.LENGTH_LONG).show();
//
//                    initUI();
//                });
//            }
//
//            @Override
//            public void onError(Exception e) {
//                runOnUiThread(() -> {
//                    showLoading(false);
//                    Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    initUI();
//                });
//            }
//        });
//    }
//    private void initUI(){
//        CardView cardViewHistory = findViewById(R.id.cardView_History);
//        cardViewHistory.setOnClickListener(v -> {
//            Intent intent = new Intent(this, HistoryActivity.class);
//            startActivity(intent);
//        });
//        CardView cardViewTranslate = findViewById(R.id.cardView_Docs);
//        cardViewTranslate.setOnClickListener(v -> {
//            Intent intent = new Intent(this, TranslateTextActivity.class);
//            startActivity(intent);
//        });
//
//        CardView cardViewSetting = findViewById(R.id.cardView_Settings);
//        cardViewSetting.setOnClickListener(v -> {
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivity(intent);
//        });
//        CardView cardViewFavourite = findViewById(R.id.cardView_Favourite);
//        cardViewFavourite.setOnClickListener(v -> {
//            Intent intent = new Intent(this, FavoriteActivity.class);
//            startActivity(intent);
//        });
//    }
//
//
//}

package com.example.app_dictionary_ev;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.app_dictionary_ev.data.db.AppDatabase;
import com.example.app_dictionary_ev.data.db.DatabaseInitializer;
import com.example.app_dictionary_ev.data.model.DictionaryEntry;
import com.google.firebase.FirebaseApp;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SearchHelper.OnWordSelectedListener {
    private ProgressBar progressBar;
    private ConstraintLayout mainContent;
    private SearchHelper searchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        mainContent = findViewById(R.id.main);

        searchHelper = new SearchHelper(this, findViewById(android.R.id.content));
        searchHelper.setOnWordSelectedListener(this);

        checkDatabaseInitialization();

    }
    @Override
    public void onWordSelected(DictionaryEntry entry) {
        // Xử lý khi từ được chọn
        Intent intent = new Intent(this, ResultActivity.class);

        // Truyền dữ liệu về từ điển vào Intent
        intent.putExtra("word", entry.word);
        intent.putExtra("definition", entry.meanings != null && !entry.meanings.isEmpty() ? entry.meanings.get(0).definition : "Không có định nghĩa");
        intent.putExtra("pos", entry.pos); // Truyền từ loại nếu cần

        // Khởi chạy ResultActivity
        startActivity(intent);
    }
    private void checkDatabaseInitialization(){
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean needInit = needToInitializeData();
            runOnUiThread(() -> {
                if (needInit) {
                    showLoading(true);
                    checkAndLoadData();
                } else {
                    showLoading(false);
                    initUI();
                }
            });
        });
    }

    private boolean needToInitializeData() {
        SharedPreferences prefs = getSharedPreferences("db_init_prefs", MODE_PRIVATE);
        if (prefs.getBoolean("is_initialized", false)) {
            return false;
        }
        int count = AppDatabase.getDatabase(this).dictionaryDao().getCount();
        return count == 0;

    }
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mainContent.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
    private void checkAndLoadData() {
        DatabaseInitializer.populateDatabase(this, new DatabaseInitializer.InitializationCallback() {
            @Override
            public void onComplete(int count) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(MainActivity.this, "Tải dữ liệu thành công", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this,"Đã ghi " + count + " từ vào cơ sở dữ liệu", Toast.LENGTH_LONG).show();

                    initUI();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    initUI();
                });
            }
        });
    }
    private void initUI(){
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