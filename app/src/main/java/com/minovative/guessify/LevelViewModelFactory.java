package com.minovative.guessify;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LevelViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final LevelDao levelDao;
    private final String language;

    public LevelViewModelFactory(Application application ,LevelDao levelDao ,String language) {
        this.application = application;
        this.levelDao = levelDao;
        this.language = language;
    }

    @NonNull
    @Override

    public <T extends ViewModel> T create(@NonNull Class<T> modellClass) {

        if (modellClass.isAssignableFrom(LevelViewModel.class)) {

            return (T) new LevelViewModel(application ,levelDao ,language);
        }
            throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
