package com.example.app_dictionary_ev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Đặt layout cho activity trước khi gọi findViewById
        setContentView(R.layout.favourite_activity);

        // Bây giờ có thể tìm view được
        RecyclerView recyclerView = findViewById(R.id.rvVocab);

        // Tạo dữ liệu ví dụ
        List<VocabHisModal> favoriteItems = new ArrayList<>();
        favoriteItems.add(new VocabHisModal("Attention", "/əˈtenʃn/", "(v)", "sự chú ý"));
        favoriteItems.add(new VocabHisModal("Attract", "/əˈtrækt/", "(v)", "hút"));
        favoriteItems.add(new VocabHisModal("Hello", "/həˈləʊ/", "(v)", "xin chào"));
        favoriteItems.add(new VocabHisModal("Hi", "/haɪ/", "(v)", "xin chào"));
        favoriteItems.add(new VocabHisModal("Mouse", "/maʊs/", "(n)", "chuột"));
        favoriteItems.add(new VocabHisModal("Desk", "/desk/", "(n)", "bàn"));
        favoriteItems.add(new VocabHisModal("Chair", "/tʃeə(r)/", "(n)", "ghế"));

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new VocabAdapter(this, favoriteItems));

        // Thiết lập sự kiện cho nút quay lại
        ImageButton btnBack = findViewById(R.id.btnHome);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
