package com.example.app_dictionary_ev;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dictionary_ev.data.db.AppDatabase;
import com.example.app_dictionary_ev.data.model.DictionaryEntry;
import com.example.app_dictionary_ev.HistoryDatabaseHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SearchHelper {
    private final Context context;
    private final AppDatabase db;
    private final EditText searchText;
    private final RecyclerView rvSuggestions; // RecyclerView để hiển thị gợi ý
    private final ImageButton buttonClear; // Nút xóa nhanh nd ở ô tìm kiếm
    private final ProgressBar progressBar; // ProgressBar để hiển thị trạng thái tải dữ liệu
    private final SuggestionAdapter suggestionAdapter; //chứa ds từ gợi ý
    private final Handler handler = new Handler();
    private Runnable searchRunnable;
    private OnWordSelectedListener wordSelectedListener;

    public interface OnWordSelectedListener {
        void onWordSelected(DictionaryEntry entry);
    }

    public SearchHelper(Context context, View rootView) {
        this.context = context;
        this.db = AppDatabase.getDatabase(context);

        searchText = rootView.findViewById(R.id.searchText);
        rvSuggestions = rootView.findViewById(R.id.rvSuggestions);
        buttonClear = rootView.findViewById(R.id.buttonClear);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Thiết lập RecyclerView
        rvSuggestions.setLayoutManager(new LinearLayoutManager(context));
        suggestionAdapter = new SuggestionAdapter();
        rvSuggestions.setAdapter(suggestionAdapter);

        setupSearchFunctionality();
    }

    public void setOnWordSelectedListener(OnWordSelectedListener listener) {
        this.wordSelectedListener = listener;
    }

    private void setupSearchFunctionality() {
        // Xử lý sự kiện tìm kiếm
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> searchWords(s.toString());
                handler.postDelayed(searchRunnable, 300); // Delay 300ms
            }
        });

        // Xử lý khi click vào gợi ý
        suggestionAdapter.setOnSuggestionClickListener(entry -> {
            if (wordSelectedListener != null) {
                wordSelectedListener.onWordSelected(entry);
            }

            // ✅ Thêm vào database lịch sử
            // ✅ Lấy thông tin từ entry
            String word = entry.word;
            String pronounce = entry.pronunciation;
            String pos = entry.pos;

            // ✅ Nối toàn bộ nghĩa, ví dụ và ghi chú
            StringBuilder meaningBuilder = new StringBuilder();
            if (entry.meanings != null) {
                for (DictionaryEntry.Meaning meaningObj : entry.meanings) {
                    meaningBuilder.append("➜ ").append(meaningObj.definition);
                    if (meaningObj.example != null && !meaningObj.example.isEmpty()) {
                        meaningBuilder.append("\n\n").append(meaningObj.example);
                    }
                    if (meaningObj.note != null && !meaningObj.note.isEmpty()) {
                        meaningBuilder.append("\n").append(meaningObj.note);
                    }
                    meaningBuilder.append("\n");
                }
            }
            String meaning = meaningBuilder.toString().trim();

            // ✅ Thêm vào database lịch sử
            HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(context);
            dbHelper.addHistoryWord(word, pronounce, pos, meaning);

            // ✅ Ẩn gợi ý sau khi chọn
            rvSuggestions.setVisibility(View.GONE);
        });

        // Xử lý nút clear
        buttonClear.setOnClickListener(v -> {
            searchText.setText("");
            rvSuggestions.setVisibility(View.GONE);
        });
    }

    private void searchWords(String query) {
        if (query.isEmpty()) {
            rvSuggestions.setVisibility(View.GONE);
            return;
        }

        showLoading(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<DictionaryEntry> results = db.dictionaryDao().searchWords(query);

            // Chạy trên main thread để cập nhật UI
            ((android.app.Activity) context).runOnUiThread(() -> {
                showLoading(false);
                if (results.isEmpty()) {
                    rvSuggestions.setVisibility(View.GONE);
                } else {
                    suggestionAdapter.setSuggestions(results);
                    rvSuggestions.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
