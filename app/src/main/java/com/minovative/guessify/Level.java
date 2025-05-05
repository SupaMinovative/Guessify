package com.minovative.guessify;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"level" , "language"})
public class Level {

    @ColumnInfo(name ="level")
    @NonNull
    private int level;

    @ColumnInfo(name = "language")
    @NonNull
    private String language;

    private String difficulty;
    @ColumnInfo(name ="star_required")
    private int starRequired;

    @ColumnInfo(name ="star_rewarded")
    private int starRewarded;
    @ColumnInfo(name ="isUnlocked")
    private boolean isUnlocked;
    @ColumnInfo(name ="isPlay")
    private boolean isPlay;


    public Level(int level,String difficulty,int starRequired,int starRewarded,boolean isUnlocked,String language, boolean isPlay) {
        this.level = level;
        this.difficulty = difficulty;
        this.starRequired = starRequired;
        this.starRewarded = starRewarded;
        this.isUnlocked = isUnlocked;
        this.isPlay = isPlay;
        this.language = language;
    }

    public Level() {

    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getStarRequired() {
        return starRequired;
    }

    public void setStarRequired(int starRequired) {
        this.starRequired = starRequired;
    }

    public int getStarRewarded() {
        return starRewarded;
    }

    public void setStarRewarded(int starRewarded) {
        this.starRewarded = starRewarded;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }
}
