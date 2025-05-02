package com.example.app_dictionary_ev.data.model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class MeaningsConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static String fromMeanings(List<DictionaryEntry.Meaning> meanings) {
        return gson.toJson(meanings);
    }

    @TypeConverter
    public static List<DictionaryEntry.Meaning> toMeanings(String json) {
        return gson.fromJson(json, new TypeToken<List<DictionaryEntry.Meaning>>(){}.getType());
    }
}