package com.example.app_dictionary_ev;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.app_dictionary_ev.data.dao.DictionaryDao;
import com.example.app_dictionary_ev.data.model.DictionaryEntry;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DictionaryViewModel extends AndroidViewModel {
    private final DictionaryDao dictionaryDao;
    private final ExecutorService executorService;
    private final MutableLiveData<List<DictionaryEntry>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DictionaryViewModel(@NonNull Application application, DictionaryDao dictionaryDao) {
        super(application);
        this.dictionaryDao = dictionaryDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    public LiveData<List<DictionaryEntry>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            clearSearchResults();
            return;
        }

        isLoading.postValue(true);
        errorMessage.postValue(null);

        executorService.execute(() -> {
            try {
                List<DictionaryEntry> results;
                if (keyword.length() >= 3) {
                    results = dictionaryDao.searchByPrefix(keyword);
                } else {
                    results = dictionaryDao.fuzzySearch(keyword);
                }
                searchResults.postValue(results);
            } catch (Exception e) {
                errorMessage.postValue("Search error: " + e.getMessage());
                searchResults.postValue(null);
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Add the missing clearSearchResults method
    public void clearSearchResults() {
        searchResults.postValue(null);
        errorMessage.postValue(null);
    }

    public void searchByPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            clearSearchResults();
            return;
        }

        isLoading.postValue(true);
        executorService.execute(() -> {
            try {
                List<DictionaryEntry> results = dictionaryDao.searchByPrefix(prefix);
                searchResults.postValue(results);
            } catch (Exception e) {
                errorMessage.postValue("Prefix search error: " + e.getMessage());
                searchResults.postValue(null);
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public void fuzzySearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            clearSearchResults();
            return;
        }

        isLoading.postValue(true);
        executorService.execute(() -> {
            try {
                List<DictionaryEntry> results = dictionaryDao.fuzzySearch(query);
                searchResults.postValue(results);
            } catch (Exception e) {
                errorMessage.postValue("Fuzzy search error: " + e.getMessage());
                searchResults.postValue(null);
            } finally {
                isLoading.postValue(false);
            }
        });
    }
}