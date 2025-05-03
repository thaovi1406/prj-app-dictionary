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

    public static class Meaning {
        public String definition;
        public String example;
        public String note;
    }
}


// Converter để lưu List<Meaning> dưới dạng JSON