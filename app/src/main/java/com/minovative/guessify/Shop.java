package com.minovative.guessify;

public class Shop {
    private int starRequired;
    private int puzzleQtt;

    public Shop(int starRequired,int puzzleQtt) {
        this.starRequired = starRequired;
        this.puzzleQtt = puzzleQtt;
    }

    public int getStarRequired( ) {
        return starRequired;
    }

    public void setStarRequired(int starRequired) {
        this.starRequired = starRequired;
    }

    public int getPuzzleQtt( ) {
        return puzzleQtt;
    }

    public void setPuzzleQtt(int puzzleQtt) {
        this.puzzleQtt = puzzleQtt;
    }
}
