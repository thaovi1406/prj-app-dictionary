package com.example.app_dictionary_ev;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchText extends AppCompatActivity {

    private RecyclerView rvSuggestions;
    private com.example.app_dictionary_ev.SuggestionAdapter suggestionAdapter;
    private List<String> allSuggestions;
    private List<String> filteredSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText searchText = findViewById(R.id.searchText);
        rvSuggestions = findViewById(R.id.rvSuggestions);

        rvSuggestions.setLayoutManager(new LinearLayoutManager(this));
        allSuggestions = new ArrayList<>();
        allSuggestions.add("attention (n) sự chú ý");
        allSuggestions.add("res (n) đồ vật");
        allSuggestions.add("res ngtestae");
        allSuggestions.add("publica");
        allSuggestions.add("bắc) động");
        allSuggestions.add("vì) cho thuyền trở về, resall");
        allSuggestions.add("vì) cho thuyền trở về, resall");
        allSuggestions.add("vì) cho thuyền trở về, resall");
        allSuggestions.add("vì) cho thuyền trở về, resall");

        filteredSuggestions = new ArrayList<>(allSuggestions);
        suggestionAdapter = new com.example.app_dictionary_ev.SuggestionAdapter(filteredSuggestions);
        rvSuggestions.setAdapter(suggestionAdapter);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                filterSuggestions(newText);
                rvSuggestions.setVisibility(newText.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterSuggestions(String query) {
        filteredSuggestions.clear();
        if (query.isEmpty()) {
            filteredSuggestions.addAll(allSuggestions);
        } else {
            for (String suggestion : allSuggestions) {
                if (suggestion.toLowerCase().contains(query.toLowerCase())) {
                    filteredSuggestions.add(suggestion);
                }
            }
        }
        suggestionAdapter.updateSuggestions(filteredSuggestions);
    }
}