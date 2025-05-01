package com.example.app_dictionary_ev.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.app_dictionary_ev.data.dao.DictionaryDao;
import com.example.app_dictionary_ev.data.model.DictionaryEntry;

@Database(entities = {DictionaryEntry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DictionaryDao dictionaryDao();

    private static volatile AppDatabase INSTANCE;

    // Remove the getInstance(SearchHome) method as it's not standard
    // Use getDatabase(Context) instead for consistency

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "dictionary_db"
                            )
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}