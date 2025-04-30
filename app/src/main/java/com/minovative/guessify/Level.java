package com.minovative.guessify;

public class Level {

    private String level;
    private String difficulty;
    private int starRequired;
    private int starRewarded;

    public Level(String level,String difficulty,int starRequired) {
        this.level = level;
        this.difficulty = difficulty;
        this.starRequired = starRequired;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
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
}
