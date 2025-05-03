//package com.example.app_dictionary_ev;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.widget.ImageButton;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FavoriteActivity extends AppCompatActivity {
//    private DatabaseHelper dbHelper;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        dbHelper = new DatabaseHelper(this);
//        setContentView(R.layout.history_activity);
//
//        CustomHeader customHeader = findViewById(R.id.customHeader);
//        customHeader.setTitle("Yêu thích");
//
//        RecyclerView recyclerView = findViewById(R.id.rvVocab);
//
//        List<VocabHisModal> favoriteItems = getFavoriteWords();
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        recyclerView.setAdapter(new VocabAdapter(this, favoriteItems));
//
//
//        ImageButton btnBack = findViewById(R.id.btnHome);
//        btnBack.setOnClickListener(v -> {
//            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        });
//    }
//    private List<VocabHisModal> getFavoriteWords() {
//        List<VocabHisModal> favoriteItems = new ArrayList<>();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query("favorites", new String[]{"word", "pronunciation", "type", "meaning"},
//                null, null, null, null, null);
//
//        while (cursor.moveToNext()) {
//            String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
//            String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow("pronunciation"));
//            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
//            String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
//            favoriteItems.add(new VocabHisModal(word, pronunciation, type, meaning));
//        }
//
//        cursor.close();
//        db.close();
//        return favoriteItems;
//    }
//}
package com.example.app_dictionary_ev;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements VocabAdapter.OnItemCheckListener{
    private DatabaseHelper dbHelper;
    private EditText searchText;
    private ImageButton buttonClear;
    private RecyclerView recyclerView;
    private VocabAdapter adapter;
    private List<VocabHisModal> allFavorites;
    private List<VocabHisModal> filteredList;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_activity);

        dbHelper = new DatabaseHelper(this);

        CustomHeader customHeader = findViewById(R.id.customHeader);
        customHeader.setTitle("Yêu thích");

        deleteButton = findViewById(R.id.deleteButton);
        searchText = findViewById(R.id.searchText);
        buttonClear = findViewById(R.id.buttonClear);
        recyclerView = findViewById(R.id.rvVocab);

        // Initially hide the delete button
        deleteButton.setVisibility(View.GONE);

        // Lấy toàn bộ từ yêu thích
        allFavorites = getFavoriteWords();
        filteredList = new ArrayList<>(allFavorites);

        // Khởi tạo adapter với danh sách đã lọc
        adapter = new VocabAdapter(this, filteredList);
        adapter.setOnItemCheckListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        // Set up delete button
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        // Lọc khi người dùng nhập tìm kiếm
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                filteredList.clear();
                for (VocabHisModal item : allFavorites) {
                    if (item.getWord().toLowerCase().contains(query)) {
                        filteredList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // Nút xoá ô tìm kiếm
        buttonClear.setOnClickListener(v -> searchText.setText(""));

        // Nút quay về
        ImageButton btnBack = findViewById(R.id.btnHome);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
    @Override
    public void onItemCheck(VocabHisModal item) {
        // Show delete button when at least one item is selected
        deleteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemUncheck(VocabHisModal item) {
        // Hide delete button if no items are selected
        if (adapter.getSelectedItems().isEmpty()) {
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa các từ đã chọn?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteSelectedItems();
                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    adapter.clearSelections();
                    deleteButton.setVisibility(View.GONE);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void deleteSelectedItems() {
        List<VocabHisModal> selectedItems = adapter.getSelectedItems();
        dbHelper.deleteMultipleFromFavorites(selectedItems);

        // Remove from all lists
        allFavorites.removeAll(selectedItems);
        filteredList.removeAll(selectedItems);

        // Update adapter
        adapter.notifyDataSetChanged();

        // Hide delete button
        deleteButton.setVisibility(View.GONE);
    }
    // Truy vấn lấy danh sách từ yêu thích từ SQLite
    private List<VocabHisModal> getFavoriteWords() {
        List<VocabHisModal> favoriteItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("favorites", new String[]{"word", "pronunciation", "type", "meaning"},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
            String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow("pronunciation"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
            favoriteItems.add(new VocabHisModal(word, pronunciation, type, meaning));
        }

        cursor.close();
        db.close();
        return favoriteItems;
    }
}
