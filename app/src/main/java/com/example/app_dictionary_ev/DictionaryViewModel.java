package com.example.app_dictionary_ev;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app_dictionary_ev.data.dao.DictionaryDao;
import com.example.app_dictionary_ev.data.model.DictionaryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DictionaryViewModel extends ViewModel {
    private DictionaryDao dictionaryDao;
    private MutableLiveData<List<DictionaryEntry>> searchResults;
    private ExecutorService executorService;

    public DictionaryViewModel(DictionaryDao dictionaryDao) {
        this.dictionaryDao = dictionaryDao;
        this.searchResults = new MutableLiveData<>();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            searchResults.postValue(new ArrayList<>());
            return;
        }

        executorService.execute(() -> {
            List<DictionaryEntry> results = dictionaryDao.searchWords(keyword);
            searchResults.postValue(results);
        });
    }

    public LiveData<List<DictionaryEntry>> getSearchResults() {
        return searchResults;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}