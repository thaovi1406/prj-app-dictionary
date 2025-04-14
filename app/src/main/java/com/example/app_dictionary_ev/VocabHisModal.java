package com.example.app_dictionary_ev;

public class VocabHisModal {
    private String word;
    private String pronounce;
    private String pos;
    private String meaning;

    public VocabHisModal(String word, String pronounce, String pos, String meaning) {
        this.word = word;
        this.pronounce = pronounce;
        this.pos = pos;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
