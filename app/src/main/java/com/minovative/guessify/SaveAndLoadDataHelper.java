package com.minovative.guessify;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

public class SaveAndLoadDataHelper {

    public interface OnLevelDataFetchedCallback {
        void onFetched(List<Level> levelList);
    }
    public static void saveStarsToDatabase(int starCount, Application application) {

        new Thread(() -> {

            AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());

            GameStateDao gameStateDao = db.gameStateDao();

            GameState currentState = gameStateDao.getGameState();

            if (currentState == null) {
                currentState = new GameState();
                currentState.setStarCount(starCount);
                gameStateDao.insertGameState(currentState);
            } else {
                currentState.setStarCount(starCount);
                gameStateDao.insertGameState(currentState);
            }
        }).start();
    }

    public static void getLevelStateFromDatabase(Activity activity, String language, OnLevelDataFetchedCallback callback) {

        new Thread(() -> {

            AppDatabase db = AppDatabase.getInstance(activity.getApplicationContext());

            LevelDao levelDao = db.levelDao();

            List<Level> currentLevelState = levelDao.getLevelByLanguageSync(language);
            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onFetched(currentLevelState));
            }
        }).start();
    }
/*
    public static void loadStarsFromDatabase(int starCount, Application application) {

        new Thread(() -> {

            AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
            GameStateDao gameStateDao = db.gameStateDao();

        }).start();
    }*/

    public static void saveLevelStateToDatabase(Activity activity,int level,String language) {

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(activity.getApplicationContext());
            LevelDao levelDao = db.levelDao();

            Level currentLevelState = levelDao.getLevel(level,language);

            if (currentLevelState == null) {
                Log.d ("DEBUG", "Level is empty");
                currentLevelState = new Level();
                currentLevelState.setLevel(level);
                currentLevelState.setLanguage(language);
            } else {

                Log.d ("DEBUG", "Level "+ level + " is set with unlocked");
                currentLevelState.setUnlocked(true);

                Log.d ("DEBUG", "Level "+ level + " is set with setPlay");
                currentLevelState.setPlay(true);

                Log.d ("DEBUG", "Level is being update with unlock and set play");


            }
            levelDao.insertLevelState(currentLevelState);
            Log.d ("DEBUG", "Inserting level state to database" + currentLevelState.toString());

        }).start();
    }



}
