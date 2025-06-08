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
    //Chuyển đổi List<Meaning> thành String để lưu vào database
    public List<Meaning> meanings;
    //List<Meaning> là danh sách các nghĩa của từ, mỗi nghĩa có thể có nhiều ví dụ và ghi chú khác nhau.

    public static class Meaning { //đại diện cho một nghĩa của từ
        public String definition;
        public String example;
        public String note;
    }

}


// Converter để lưu List<Meaning> dưới dạng JSON