package com.minovative.guessify;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GameStateViewModel extends AndroidViewModel {

    private final GameStateDao gameStateDao;
    private final LiveData<Integer> starCount;
    private final LiveData<Integer> helpItem;

    public GameStateViewModel(@NonNull Application application, GameStateDao gameStateDao){
        super(application);
        this.gameStateDao = gameStateDao;
        this.starCount = gameStateDao.getStarCount();
        this.helpItem = gameStateDao.getHelpItem();
    }

    public LiveData<Integer> getStarCount() {
        return starCount;
    }


    public LiveData<Integer> getHelpItem() {
        return helpItem;
    }
}
