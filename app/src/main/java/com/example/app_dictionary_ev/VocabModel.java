package com.example.app_dictionary_ev;

import java.util.List;

public class VocabModel {
    private String word;
    private String pronunciation;
    private String pos;
    private List<Meaning> meanings;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }
    public String getFirstMeaning() {
        if (meanings != null && !meanings.isEmpty()) {
            return meanings.get(0).getDefinition();
        }
        return "";
    }

    public String getAllMeanings() {
        if (meanings != null && !meanings.isEmpty()) {
            return meanings.get(0).getDefinition();
        }
        return "";
    }
}
