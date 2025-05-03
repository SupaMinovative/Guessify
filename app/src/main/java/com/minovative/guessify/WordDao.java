package com.minovative.guessify;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertAll(List<Word> words);
    @Query("SELECT * FROM words WHERE level = :level AND lang = :language")
    List<Word> getWordsByLevel(int level, String language);

}


