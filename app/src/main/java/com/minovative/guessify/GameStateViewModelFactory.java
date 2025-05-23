package com.minovative.guessify;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GameStateViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final GameStateDao gameStateDao;

    public GameStateViewModelFactory(Application application, GameStateDao gameStateDao) {
        this.application = application;
        this.gameStateDao = gameStateDao;
    }

    @NonNull
    @Override

    public <T extends ViewModel> T create(@NonNull Class<T> modellClass) {

        if (modellClass.isAssignableFrom(GameStateViewModel.class)) {

            return (T) new GameStateViewModel(application, gameStateDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
