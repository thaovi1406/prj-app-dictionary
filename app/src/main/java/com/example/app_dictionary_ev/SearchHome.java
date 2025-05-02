package com.example.app_dictionary_ev;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.searchText);
        clearButton = findViewById(R.id.buttonClear);

        AppDatabase database = AppDatabase.getInstance(this);
        DictionaryDao dictionaryDao = database.dictionaryDao();

        DictionaryViewModelFactory factory = new DictionaryViewModelFactory(dictionaryDao);
        viewModel = new ViewModelProvider(this, factory).get(DictionaryViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new DictionaryAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getSearchResults().observe(this, results -> {
            adapter.setData(results);
        });

        // Khởi tạo dữ liệu
        DatabaseInitializer.populateDatabase(this, new DatabaseInitializer.InitializationCallback() {
            @Override
            public void onComplete(int count) {
                if (count > 0) {
                    Toast.makeText(SearchHome.this, "Initialized " + count + " entries", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(SearchHome.this, "Failed to initialize database: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim();
                viewModel.search(keyword);
                clearButton.setVisibility(keyword.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });

        clearButton.setOnClickListener(v -> {
            searchEditText.setText("");
            clearButton.setVisibility(View.GONE);
        });
    }
}