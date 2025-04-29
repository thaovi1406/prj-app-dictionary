package com.example.app_dictionary_ev.data.model;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MeaningsConverter {
    private final Gson gson = new Gson();

    @TypeConverter
    public String fromMeanings(List<DictionaryEntry.Meaning> meanings) {
        if (meanings == null) {
            return null;
        }
        return gson.toJson(meanings);
    }

    @TypeConverter
    public List<DictionaryEntry.Meaning> toMeanings(String json) {
        if (json == null) {
            return new ArrayList<>();
        }
        return gson.fromJson(json, new TypeToken<List<DictionaryEntry.Meaning>>(){}.getType());
    }
}