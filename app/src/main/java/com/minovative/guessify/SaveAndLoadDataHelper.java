package com.minovative.guessify;

import android.app.Activity;
import android.app.Application;


public class SaveAndLoadDataHelper {

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
    public static void saveHelpItemToDatabase(int helpItem, Application application) {

        new Thread(() -> {

            AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
            GameStateDao gameStateDao = db.gameStateDao();
            GameState currentState = gameStateDao.getGameState();

            if (currentState == null) {

                currentState = new GameState();
                currentState.setHelpItem(helpItem);
                gameStateDao.insertGameState(currentState);

            } else {
                currentState.setHelpItem(helpItem);
                gameStateDao.insertGameState(currentState);
            }
        }).start();
    }

    public static void saveLevelStateToDatabase(Activity activity,int level) {

        new Thread(() -> {

            AppDatabase db = AppDatabase.getInstance(activity.getApplicationContext());
            LevelDao levelDao = db.levelDao();
            Level currentLevelState = levelDao.getLevel(level);

            if (currentLevelState == null) {

                currentLevelState = new Level();
                currentLevelState.setLevel(level);

            } else {

                currentLevelState.setUnlocked(true);
                currentLevelState.setPlay(true);
            }
            levelDao.insertLevelState(currentLevelState);
        }).start();
    }
}
