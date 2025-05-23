package com.minovative.guessify;

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
    @Query("SELECT * FROM level WHERE level =:level")
    Level getLevel(int level);
    @Query("SELECT * FROM level WHERE language =:language ORDER by level ASC")
    LiveData<List<Level>> getLevelByLanguage(String language);
    @Query("SELECT * FROM level WHERE language =:language ORDER by level ASC")
    List<Level> getLevelByLanguageSync(String language);

    @Query("DELETE FROM level")
    void deleteAll();
}
