package com.example.app_dictionary_ev.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.app_dictionary_ev.data.dao.DictionaryDao;
import com.example.app_dictionary_ev.data.model.DictionaryEntry;

@Database(entities = {DictionaryEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Khai báo DAO
    public abstract DictionaryDao dictionaryDao();

    // Singleton pattern
    private static volatile AppDatabase INSTANCE;

    // Phương thức để lấy instance của cơ sở dữ liệu
    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "dictionary_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}