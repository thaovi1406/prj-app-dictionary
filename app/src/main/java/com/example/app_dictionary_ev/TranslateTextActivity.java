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
    private Translator Translate_from_English_to_Vietnamese;
    private boolean boolean_download_language_translation_model = false;
    private EditText edittext_enter_text;
    private TextView textView_translated;
    private ImageButton button_language_translation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_docs);

        edittext_enter_text = findViewById(R.id.editEnterText);
        textView_translated = findViewById(R.id.textTranslated);
        button_language_translation = findViewById(R.id.buttonPlayTrans);

        // Create a translation configuration from English to Vietnamese
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                        .build();

        Translate_from_English_to_Vietnamese =
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

    }
    private void Download_language_translation_model(){
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        Translate_from_English_to_Vietnamese.downloadModelIfNeeded(conditions)
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
        Translate_from_English_to_Vietnamese.translate(van_ban)
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