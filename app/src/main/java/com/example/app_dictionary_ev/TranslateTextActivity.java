package com.example.app_dictionary_ev;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class TranslateTextActivity extends AppCompatActivity {
    private Translator Translate_from_Vietnamese_to_English;
    private boolean boolean_download_language_translation_model = false;
    private EditText edittext_enter_text;
    private TextView textView_translated;
    private ImageButton button_language_translation, button_swap;

    private TextView title_Enter, title_Translated;

    private ImageButton button_clear;
    private boolean isVietnameseToEnglish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_docs);

        edittext_enter_text = findViewById(R.id.editEnterText);
        textView_translated = findViewById(R.id.textTranslated);
        button_language_translation = findViewById(R.id.buttonPlayTrans);
        button_swap =findViewById(R.id.buttonSwap);
        title_Enter =findViewById(R.id.tEnter);
        title_Translated =findViewById(R.id.tTranslated);
        button_clear = findViewById(R.id.buttonClear);

        // Create a translation configuration from English to Vietnamese
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();

        Translate_from_Vietnamese_to_English =
                Translation.getClient(options);

        Download_language_translation_model();

        button_language_translation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boolean_download_language_translation_model){
                    //Once the translation model is loaded, if the Translate Language button is pressed
                    //it will start translating the text
                    String text = edittext_enter_text.getText().toString().trim();
                    Language_translation(text);
                }
                else {
                    //When the translation model is not loaded yet,
                    // if the Translate Language button is pressed, it will notify waiting for the model to load
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
                    Toast.makeText(TranslateTextActivity.this, "Dịch từ Tiếng Việt sang Tiếng Anh", Toast.LENGTH_SHORT).show();
                    title_Enter.setText("Việt");
                    title_Translated.setText("Anh");
                } else {
                    Toast.makeText(TranslateTextActivity.this, "Dịch từ Tiếng Anh sang Tiếng Việt", Toast.LENGTH_SHORT).show();
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
        boolean_download_language_translation_model = false; // reset lại trạng thái
        Download_language_translation_model(); // tải model mới
    }
    private void Download_language_translation_model(){
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
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                            }
                        });
    }
    private void Language_translation(String van_ban){
        Translate_from_Vietnamese_to_English.translate(van_ban)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        //Text translated successfully
                        textView_translated.setText(s);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Error cannot translate text
                                // ...
                            }
                        });
    }
}