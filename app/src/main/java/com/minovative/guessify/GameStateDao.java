package com.minovative.guessify;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface GameStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGameState(GameState gameState);

    @Update
    void updateGameState(GameState gameState);

    @Query("SELECT * FROM game_state WHERE id = 1 LIMIT 1")
    GameState getGameState();

    @Query("UPDATE game_state SET star = :starCount WHERE id = 1")
    void updateStarCount(int starCount);

    @Query("SELECT star FROM game_state WHERE id = 1")
    LiveData<Integer> getStarCount();

    @Query("SELECT item FROM game_state WHERE id = 1")
    LiveData<Integer> getHelpItem();

    @Query("DELETE FROM game_state")
    void deleteAll();
}
