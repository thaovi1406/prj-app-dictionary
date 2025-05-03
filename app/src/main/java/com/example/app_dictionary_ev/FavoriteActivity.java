package com.example.app_dictionary_ev;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FavoriteActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText searchText;
    private ImageButton buttonClear;
    private RecyclerView recyclerView;
    private VocabAdapter adapter;
    private List<VocabModel> allFavorites;
    private List<VocabModel> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        dbHelper = new DatabaseHelper(this);
        CustomHeader customHeader = findViewById(R.id.customHeader);
        customHeader.setTitle("Yêu thích");

        searchText = findViewById(R.id.searchText);
        buttonClear = findViewById(R.id.buttonClear);
        recyclerView = findViewById(R.id.rvVocab);

        allFavorites = getFavoriteWords();

        filteredList = new ArrayList<>(allFavorites);

        adapter = new VocabAdapter(this, filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                filteredList.clear();
                for (VocabModel item : allFavorites) {
                    if (item.getWord().toLowerCase().contains(query)) {
                        filteredList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        buttonClear.setOnClickListener(v -> {
            searchText.setText("");
            filteredList.clear();
            filteredList.addAll(allFavorites);
            adapter.notifyDataSetChanged();
        });

        ImageButton btnBack = findViewById(R.id.btnHome);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private List<VocabModel> getFavoriteWords() {
        List<VocabModel> favoriteItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("favorites", new String[]{"word", "pronunciation", "type", "meaning"},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndexOrThrow("word"));
            String pronunciation = cursor.getString(cursor.getColumnIndexOrThrow("pronunciation"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));

            List<Meaning> meanings = new ArrayList<>();
            if (meaning != null && !meaning.isEmpty()) {
                String[] meaningLines = meaning.split("\n");
                Meaning currentMeaning = null;
                for (String line : meaningLines) {
                    line = line.trim();
                    if (line.startsWith("➜")) {
                        if (currentMeaning != null) {
                            meanings.add(currentMeaning);
                        }
                        currentMeaning = new Meaning();
                        currentMeaning.setDefinition(line.replace("➜ ", ""));
                    } else if (currentMeaning != null) {
                        Pattern pattern = Pattern.compile("(.*?)(?:\\((.*?)\\))?$");
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            String examplePart = matcher.group(1) != null ? matcher.group(1).trim() : "";
                            String notePart = matcher.group(2);
                            if (currentMeaning.getExample() == null || currentMeaning.getExample().isEmpty()) {
                                currentMeaning.setExample(examplePart);
                                currentMeaning.setNote(notePart);
                            }
                        } else if (currentMeaning.getExample() == null || currentMeaning.getExample().isEmpty()) {
                            currentMeaning.setExample(line);
                        } else {
                            currentMeaning.setNote(line);
                        }
                    }
                }
                if (currentMeaning != null) {
                    meanings.add(currentMeaning);
                }
            }

            VocabModel vocab = new VocabModel();
            vocab.setWord(word);
            vocab.setPronunciation(pronunciation);
            vocab.setPos(type);
            vocab.setMeanings(meanings.isEmpty() ? null : meanings);
            favoriteItems.add(vocab);
        }

        cursor.close();
        db.close();
        return favoriteItems;
    }
}