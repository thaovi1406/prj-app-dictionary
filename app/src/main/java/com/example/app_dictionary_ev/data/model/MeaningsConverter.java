package com.example.app_dictionary_ev.data.model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

//lớp MeaningsConverter dùng để chuyển đổi qua lại giữa List<DictionaryEntry.Meaning>
// và String (dạng JSON) khi lưu vào/đọc từ Room database.
public class MeaningsConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static String fromMeanings(List<DictionaryEntry.Meaning> meanings) {
        return gson.toJson(meanings);
    }
    //Chuyển đổi List<DictionaryEntry.Meaning> thành String (dạng JSON) để lưu vào database


    @TypeConverter
    public static List<DictionaryEntry.Meaning> toMeanings(String json) {
        return gson.fromJson(json, new TypeToken<List<DictionaryEntry.Meaning>>(){}.getType());
    }
    //Chuyển đổi String (dạng JSON) thành List<DictionaryEntry.Meaning> để đọc từ database
    //TypeToken: là một lớp trừu tượng trong Gson, dùng để lấy thông tin kiểu dữ liệu của List<DictionaryEntry.Meaning>
    //Khi gọi toMeanings, Gson sẽ sử dụng TypeToken để biết rằng json này cần được chuyển đổi thành List<DictionaryEntry.Meaning>
    //Gson sẽ tự động phân tích cú pháp JSON và tạo ra danh sách các đối tượng Meaning tương ứng.
}