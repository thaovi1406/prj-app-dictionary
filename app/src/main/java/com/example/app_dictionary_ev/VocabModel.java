package com.example.app_dictionary_ev;

import java.util.List;

public class VocabModel {
    private String word;
    private String pronunciation;
    private String pos;
    private List<Meaning> meanings;
    private boolean isSelected = false;

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
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    public VocabModel() {}
    public VocabModel(String word, String pronunciation, String pos, List<Meaning> meanings) {
        this.word = word;
        this.pronunciation = pronunciation;
        this.pos = pos;
        this.meanings = meanings;
        this.isSelected = false;
    }
}
