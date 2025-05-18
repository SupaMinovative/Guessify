package com.minovative.guessify;

import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Room;

@Database(entities = {Word.class, GameState.class, Level.class}, version = 18)

public abstract class AppDatabase extends RoomDatabase {
    public AppDatabase() {

    }

    public abstract WordDao wordDao();

    public abstract GameStateDao gameStateDao();

    public abstract LevelDao levelDao();
    private static AppDatabase INSTANCE = null;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "my-database")
                    .fallbackToDestructiveMigration().build();
        } return INSTANCE;
    }
}
