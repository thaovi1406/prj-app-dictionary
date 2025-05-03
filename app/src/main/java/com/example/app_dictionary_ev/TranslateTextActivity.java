package com.example.app_dictionary_ev;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class TranslateTextActivity extends AppCompatActivity {
    private Translator Translate_from_Vietnamese_to_English;
    private boolean boolean_download_language_translation_model = false;
    private EditText edittext_enter_text;
    private TextView textView_translated;
    private ImageButton button_language_translation, button_swap, button_clear;
    private TextView title_Enter, title_Translated;
    private boolean isVietnameseToEnglish = true;
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private TranslationHistoryAdapter adapter;
    private List<TranslationHistoryModel> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_docs);

        CustomHeader customHeader = findViewById(R.id.customHeader);
        customHeader.setTitle("Dịch văn bản");

        dbHelper = new DatabaseHelper(this);
        edittext_enter_text = findViewById(R.id.editEnterText);
        textView_translated = findViewById(R.id.textTranslated);
        button_language_translation = findViewById(R.id.buttonPlayTrans);
        button_swap = findViewById(R.id.buttonSwap);
        title_Enter = findViewById(R.id.tEnter);
        title_Translated = findViewById(R.id.tTranslated);
        button_clear = findViewById(R.id.buttonClear);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.rvTranslated);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        historyList = new ArrayList<>();
        adapter = new TranslationHistoryAdapter(this, historyList);
        recyclerView.setAdapter(adapter);

        // Tải lịch sử ban đầu
        loadHistory();

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();

        Translate_from_Vietnamese_to_English = Translation.getClient(options);

        Download_language_translation_model();

        button_language_translation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boolean_download_language_translation_model) {
                    String text = edittext_enter_text.getText().toString().trim();
                    Language_translation(text);
                } else {
                    Toast.makeText(TranslateTextActivity.this, "Please wait for the translation model to load.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVietnameseToEnglish = !isVietnameseToEnglish;
                updateTranslator();

                if (isVietnameseToEnglish) {
                    title_Enter.setText("Việt");
                    title_Translated.setText("Anh");
                } else {
                    title_Enter.setText("Anh");
                    title_Translated.setText("Việt");
                }
                String tempInput = edittext_enter_text.getText().toString();
                String tempOutput = textView_translated.getText().toString();

                edittext_enter_text.setText(tempOutput);
                textView_translated.setText(tempInput);
            }
        });

        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext_enter_text.setText("");
                textView_translated.setText("");
            }
        });
    }

    private void updateTranslator() {
        TranslatorOptions options;
        if (isVietnameseToEnglish) {
            options = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
        } else {
            options = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                    .build();
        }

        Translate_from_Vietnamese_to_English = Translation.getClient(options);
        boolean_download_language_translation_model = false;
        Download_language_translation_model();
    }

    private void Download_language_translation_model() {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        Translate_from_Vietnamese_to_English.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        boolean_download_language_translation_model = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TranslateTextActivity.this, "Failed to download translation model: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void Language_translation(String van_ban) {
        if (van_ban.isEmpty()) {
            Toast.makeText(TranslateTextActivity.this, "Vui lòng nhập văn bản để dịch", Toast.LENGTH_SHORT).show();
            return;
        }


        Translate_from_Vietnamese_to_English.translate(van_ban)
                .addOnSuccessListener(translatedText -> {
                    textView_translated.setText(translatedText);

                    boolean success = dbHelper.insertTranslationHistory(
                            van_ban,
                            translatedText
                    );

                    if (success) {
                        loadHistory();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(TranslateTextActivity.this, "Lỗi khi lưu lịch sử!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TranslateTextActivity.this, "Lỗi dịch: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadHistory() {
        historyList.clear();
        Cursor cursor = dbHelper.getTranslationHistory();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HISTORY_ID));
                String inputText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INPUT_TEXT));
                String translatedText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRANSLATED_TEXT));


                historyList.add(new TranslationHistoryModel(id, inputText, translatedText));
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
}