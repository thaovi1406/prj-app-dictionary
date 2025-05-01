package com.example.app_dictionary_ev;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dictionary_ev.data.dao.DictionaryDao;
import com.example.app_dictionary_ev.data.db.AppDatabase;
import com.example.app_dictionary_ev.data.db.DatabaseInitializer;

public class SearchHome extends AppCompatActivity {
    private DictionaryViewModel viewModel;
    private EditText searchEditText;
    private ImageButton clearButton;
    private DictionaryAdapter adapter;
    private RecyclerView rvSuggestions;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ view
        searchEditText = findViewById(R.id.searchText);
        clearButton = findViewById(R.id.buttonClear);
        rvSuggestions = findViewById(R.id.rvSuggestions);
        progressBar = findViewById(R.id.progressBar);

        // Thiết lập RecyclerView cho gợi ý tìm kiếm
        adapter = new DictionaryAdapter();
        rvSuggestions.setAdapter(adapter);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(this));
        rvSuggestions.setVisibility(View.GONE); // Ẩn ban đầu

        // Khởi tạo ViewModel
        AppDatabase database = AppDatabase.getDatabase(this);
        DictionaryDao dictionaryDao = database.dictionaryDao();
        DictionaryViewModelFactory factory = new DictionaryViewModelFactory(dictionaryDao);
        viewModel = new ViewModelProvider(this, factory).get(DictionaryViewModel.class);

        // Quan sát kết quả tìm kiếm
        viewModel.getSearchResults().observe(this, results -> {
            progressBar.setVisibility(View.GONE);
            if (results != null && !results.isEmpty()) {
                adapter.setData(results);
                rvSuggestions.setVisibility(View.VISIBLE);
            } else {
                rvSuggestions.setVisibility(View.GONE);
            }
        });

        // Xử lý sự kiện tìm kiếm
        searchEditText.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(runnable);
                String keyword = s.toString().trim();
                clearButton.setVisibility(keyword.isEmpty() ? View.GONE : View.VISIBLE);

                if (!keyword.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    rvSuggestions.setVisibility(View.VISIBLE);

                    runnable = () -> {
                        if (keyword.length() >= 3) {
                            viewModel.searchByPrefix(keyword);
                        } else {
                            rvSuggestions.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    };
                    handler.postDelayed(runnable, 300); // Debounce 300ms
                } else {
                    rvSuggestions.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        // Xử lý nút xóa
        clearButton.setOnClickListener(v -> {
            searchEditText.setText("");
            rvSuggestions.setVisibility(View.GONE);
        });

        // Xử lý khi chọn một gợi ý
        adapter.setOnItemClickListener(entry -> {
            // Chuyển sang màn hình chi tiết từ
            Intent intent = new Intent(SearchHome.this, ResultActivity.class);
            intent.putExtra("word", entry.getWord());
            startActivity(intent);
        });
    }
}