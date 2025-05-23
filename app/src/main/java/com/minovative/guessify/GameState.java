package com.minovative.guessify;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_state")
public class GameState {
    @PrimaryKey public int id = 1;
@ColumnInfo(name = "star")
private  int starCount;
@ColumnInfo(name = "item")
private int helpItem = 3;


    public int getStarCount() {
        return starCount;
    }
    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public int getHelpItem() {
        return helpItem;
    }

    public void setHelpItem(int helpItem) {
        this.helpItem = helpItem;
    }
}