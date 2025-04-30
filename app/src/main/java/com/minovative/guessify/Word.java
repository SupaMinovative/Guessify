package com.minovative.guessify;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "words", indices = {@Index(value = {"word"}, unique = true)})
public class Word {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "word")
    private String word;
    @ColumnInfo(name ="hint")
    private String hint;
    @ColumnInfo(name = "lang")
    private String language;

    public Word(String word,String hint,String language) {
        this.word = word;
        this.hint = hint;
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
