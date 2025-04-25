//package com.example.app_dictionary_ev;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.widget.ImageButton;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class BaseActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        new Handler().post(() -> {
//            ImageButton btnBack = findViewById(R.id.btnHome);
//            if (btnBack != null) {
//                btnBack.setOnClickListener(v -> {
//                    Intent intent = new Intent(this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    startActivity(intent);
//                });
//            }
//        });
//    }
//}
