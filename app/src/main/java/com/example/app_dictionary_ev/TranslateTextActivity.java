package com.example.app_dictionary_ev;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
    private ImageButton buttonSpeakVN, buttonSpeakEN;
    private TextToSpeech textToSpeechVN, textToSpeechEN;

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
        buttonSpeakVN = findViewById(R.id.buttonSpeakVN);
        buttonSpeakEN = findViewById(R.id.buttonSpeakEN);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.rvTranslated);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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

        button_language_translation.setOnClickListener(v -> {
            if (boolean_download_language_translation_model) {
                String text = edittext_enter_text.getText().toString().trim();
                Language_translation(text);
            } else {
                Toast.makeText(TranslateTextActivity.this, "Please wait for the translation model to load.", Toast.LENGTH_SHORT).show();
            }
        });

        button_swap.setOnClickListener(v -> {
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
        });

        button_clear.setOnClickListener(v -> {
            edittext_enter_text.setText("");
            textView_translated.setText("");
        });

        // Khởi tạo TTS cho tiếng Việt
        textToSpeechVN = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeechVN.setLanguage(new Locale("vi"));
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Ngôn ngữ tiếng Việt không được hỗ trợ", Toast.LENGTH_SHORT).show();
                }
                applySpeechRate();
            }
        });

        // Khởi tạo TTS cho tiếng Anh
        textToSpeechEN = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeechEN.setLanguage(Locale.ENGLISH);
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Ngôn ngữ tiếng Anh không được hỗ trợ", Toast.LENGTH_SHORT).show();
                }
                applySpeechRate();
            }
        });

        // Bắt sự kiện click nút loa tiếng Việt
        buttonSpeakVN.setOnClickListener(v -> {
            String text = edittext_enter_text.getText().toString().trim();
            if (!text.isEmpty() && textToSpeechVN != null) {
                textToSpeechVN.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(TranslateTextActivity.this, "Không có văn bản để đọc hoặc TTS chưa được khởi tạo", Toast.LENGTH_SHORT).show();
            }
        });

        // Bắt sự kiện click nút loa tiếng Anh
        buttonSpeakEN.setOnClickListener(v -> {
            String text = textView_translated.getText().toString().trim();
            if (!text.isEmpty() && textToSpeechEN != null) {
                textToSpeechEN.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(TranslateTextActivity.this, "Không có văn bản để đọc hoặc TTS chưa được khởi tạo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeechVN != null) {
            textToSpeechVN.stop();
            textToSpeechVN.shutdown();
        }
        if (textToSpeechEN != null) {
            textToSpeechEN.stop();
            textToSpeechEN.shutdown();
        }
        super.onDestroy();
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
                .addOnSuccessListener(v -> boolean_download_language_translation_model = true)
                .addOnFailureListener(e ->
                        Toast.makeText(TranslateTextActivity.this, "Failed to download translation model: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
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
        Collections.reverse(historyList);
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();
        applySpeechRate(); // Cập nhật tốc độ nếu người dùng thay đổi trong Settings
    }
    private void applySpeechRate() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        float speechRate = prefs.getFloat("speed", 1.0f);
        if (textToSpeechVN != null) {
            textToSpeechVN.setSpeechRate(speechRate);
        }
        if (textToSpeechEN != null) {
            textToSpeechEN.setSpeechRate(speechRate);
        }
    }
}
