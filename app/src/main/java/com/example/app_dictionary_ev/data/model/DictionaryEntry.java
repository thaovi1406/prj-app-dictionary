package com.example.app_dictionary_ev.data.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import androidx.annotation.NonNull;

import java.util.List;

@Entity(tableName = "dictionary")
public class DictionaryEntry {
    @PrimaryKey
    @NonNull
    public String word;
    public String pronunciation;
    public String pos;

    @TypeConverters(MeaningsConverter.class)
    public List<Meaning> meanings;

    // Getters
    public String getWord() { return word; }
    public String getPronunciation() { return pronunciation; }
    public String getPos() { return pos; }
    public List<Meaning> getMeanings() { return meanings; }

    // Setters
    public void setWord(@NonNull String word) { this.word = word; }
    public void setPronunciation(String pronunciation) { this.pronunciation = pronunciation; }
    public void setPos(String pos) { this.pos = pos; }
    public void setMeanings(List<Meaning> meanings) { this.meanings = meanings; }

    public static class Meaning {
        public String definition;
        public String example;
        public String note;

        // Getters
        public String getDefinition() { return definition; }
        public String getExample() { return example; }
        public String getNote() { return note; }

        // Setters
        public void setDefinition(String definition) { this.definition = definition; }
        public void setExample(String example) { this.example = example; }
        public void setNote(String note) { this.note = note; }
    }
}