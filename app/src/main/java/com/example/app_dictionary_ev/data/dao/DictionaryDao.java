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

    // Tìm kiếm theo tiền tố (prefix)
    @Query("SELECT * FROM dictionary WHERE word LIKE :query || '%' LIMIT 20")
    List<DictionaryEntry> searchByPrefix(String query);

    // Tìm kiếm mờ (fuzzy search)
    @Query("SELECT * FROM dictionary WHERE word LIKE '%' || :query || '%' LIMIT 20")
    List<DictionaryEntry> fuzzySearch(String query);

    // Tìm kiếm nâng cao (theo từ loại)
    @Query("SELECT * FROM dictionary WHERE word LIKE :query || '%' AND pos = :partOfSpeech LIMIT 20")
    List<DictionaryEntry> searchByPrefixAndPos(String query, String partOfSpeech);

    @Query("SELECT * FROM dictionary WHERE word LIKE :keyword")
    List<DictionaryEntry> searchWords(String keyword);
}