package com.minovative.guessify;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executors;

public class LevelViewModel extends AndroidViewModel {

    private final LevelDao levelDao;
        private final LiveData<List<Level>> levelList;

        String language;

    public LevelViewModel(@NonNull Application application ,LevelDao levelDao, String language) {

        super(application);
        this.levelDao = levelDao;
        this.language = language;
        levelList = levelDao.getLevelByLanguage(language);
    }

    public LiveData<List<Level>> getLevelList() {
        return levelList;
    }

    public void updateLevel(Level level) {

        Executors.newSingleThreadExecutor().execute(()-> {

            levelDao.insertLevelState(level);
        });
    }

    public LiveData<List<Level>> getLevelByLanguage(String language) {

        return levelDao.getLevelByLanguage(language);
    }
}
