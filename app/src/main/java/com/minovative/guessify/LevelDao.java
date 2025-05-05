package com.minovative.guessify;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LevelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLevelState(Level level);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllLevelState(List<Level> levelList);
    @Query("SELECT * FROM level WHERE level =:level AND language =:language")
    Level getLevel(int level, String language);

    @Query("SELECT * FROM level WHERE language =:language")
    List<Level> getLevelByLanguage(String language);
}
