package com.example.app_dictionary_ev;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_dictionary_ev.data.dao.DictionaryDao;

public class DictionaryViewModelFactory implements ViewModelProvider.Factory {
    private final DictionaryDao dictionaryDao;

    public DictionaryViewModelFactory(DictionaryDao dictionaryDao) {
        this.dictionaryDao = dictionaryDao;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DictionaryViewModel.class)) {
            return (T) new DictionaryViewModel(dictionaryDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}