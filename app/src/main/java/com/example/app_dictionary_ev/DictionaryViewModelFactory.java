package com.example.app_dictionary_ev;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_dictionary_ev.data.dao.DictionaryDao;

public class DictionaryViewModelFactory implements ViewModelProvider.Factory {
    private Application application = new Application();
    private final DictionaryDao dictionaryDao;

    public DictionaryViewModelFactory(DictionaryDao dictionaryDao) {
        this.application = application;
        this.dictionaryDao = dictionaryDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DictionaryViewModel.class)) {
            return (T) new DictionaryViewModel(application, dictionaryDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}