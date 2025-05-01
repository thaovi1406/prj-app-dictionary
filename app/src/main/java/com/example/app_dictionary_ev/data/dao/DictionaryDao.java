package com.example.app_dictionary_ev.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.app_dictionary_ev.data.model.DictionaryEntry;

import java.util.List;

@Dao
public interface DictionaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DictionaryEntry> entries);

    @Query("SELECT * FROM dictionary WHERE word = :word")
    DictionaryEntry findByWord(String word);

    @Query("SELECT * FROM dictionary")
    List<DictionaryEntry> getAll();

    @Query("SELECT COUNT(*) FROM dictionary")
    int getCount();

    @Query("SELECT * FROM dictionary WHERE word LIKE :query || '%' LIMIT 20")
    List<DictionaryEntry> searchWords(String query);
}