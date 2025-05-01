package com.example.app_dictionary_ev.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;
import java.util.Objects;

@Entity(tableName = "dictionary")
public class DictionaryEntry {

    @PrimaryKey
    @NonNull
    private String word;

    private String pronunciation;
    private String pos;

    @TypeConverters(MeaningsConverter.class)
    private List<Meaning> meanings;

    // Constructors
    public DictionaryEntry() {
    }

    public DictionaryEntry(@NonNull String word, String pronunciation, String pos, List<Meaning> meanings) {
        this.word = word;
        this.pronunciation = pronunciation;
        this.pos = pos;
        this.meanings = meanings;
    }

    // Getters and Setters
    @NonNull
    public String getWord() {
        return word;
    }

    public void setWord(@NonNull String word) {
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

    @Override
    public String toString() {
        return "DictionaryEntry{" +
                "word='" + word + '\'' +
                ", pronunciation='" + pronunciation + '\'' +
                ", pos='" + pos + '\'' +
                ", meanings=" + meanings +
                '}';
    }

    // Nested class
    public static class Meaning {
        private String definition;
        private String example;
        private String note;

        public Meaning() {
        }

        public Meaning(String definition, String example, String note) {
            this.definition = definition;
            this.example = example;
            this.note = note;
        }

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }

        public String getExample() {
            return example;
        }

        public void setExample(String example) {
            this.example = example;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        @Override
        public String toString() {
            return "Meaning{" +
                    "definition='" + definition + '\'' +
                    ", example='" + example + '\'' +
                    ", note='" + note + '\'' +
                    '}';
        }
    }
}
