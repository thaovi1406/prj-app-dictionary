package com.example.app_dictionary_ev;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton; 

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private HistoryDatabaseHelper dbHelper;
    private EditText searchText;
    private ImageButton buttonClear;
    private RecyclerView rvHistory;
    private VocabAdapter adapter;
    private List<VocabModel> allHistory;
    private List<VocabModel> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        dbHelper = new HistoryDatabaseHelper(this);

        CustomHeader customHeader = findViewById(R.id.customHeader);
        customHeader.setTitle("Lịch sử");

        searchText = findViewById(R.id.searchText);
        buttonClear = findViewById(R.id.buttonClear);
        rvHistory = findViewById(R.id.rvVocab);

        allHistory = getHistoryWords();
        filteredList = new ArrayList<>(allHistory);

        adapter = new VocabAdapter(this, filteredList);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvHistory.setAdapter(adapter);

        // Tìm kiếm
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                filteredList.clear();
                for (VocabModel item : allHistory) {
                    if (item.getWord().toLowerCase().contains(query)) {
                        filteredList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        buttonClear.setOnClickListener(v -> searchText.setText(""));

        ImageButton btnBack = findViewById(R.id.btnHome);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(HistoryActivity.this, MainActivity.class));
            finish();
        });
    }

    private List<VocabModel> getHistoryWords() {
        List<VocabModel> historyItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("history", new String[]{"word", "pronunciation", "type", "meaning"},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
            String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow("pronunciation"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));

            // Tạo model chính
            VocabModel vocab = new VocabModel();
            vocab.setWord(word);
            vocab.setPronunciation(pronunciation);
            vocab.setPos(type);

            // Meaning đơn lẻ vì trong lịch sử chỉ lưu 1 nghĩa
            Meaning m = new Meaning();
            m.setDefinition(meaning);
            List<Meaning> meaningList = new ArrayList<>();
            meaningList.add(m);
            vocab.setMeanings(meaningList);

            historyItems.add(vocab);
        }

        cursor.close();
        db.close();
        Collections.reverse(historyItems); // Mới nhất lên đầu
        return historyItems;
    }
}
