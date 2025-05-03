package com.example.app_dictionary_ev;

public class TranslationHistoryModel {
    private int id;
    private String inputText;
    private String translatedText;


    public TranslationHistoryModel(int id, String inputText, String translatedText) {
        this.id = id;
        this.inputText = inputText;
        this.translatedText = translatedText;

    }

    public int getId() {
        return id;
    }

    public String getInputText() {
        return inputText;
    }

    public String getTranslatedText() {
        return translatedText;
    }


}