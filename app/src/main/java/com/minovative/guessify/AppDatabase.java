package com.minovative.guessify;

import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Room;

@Database(entities = {Word.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public AppDatabase() {

    }

    public abstract WordDao wordDao();

    private static AppDatabase INSTANCE = null;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "my-database")
                    .fallbackToDestructiveMigration().build();
        } return INSTANCE;
    }
}
