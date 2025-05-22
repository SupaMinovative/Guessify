package com.minovative.guessify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level3Activity extends AppCompatActivity implements GameDisplayAdapter.OnLastWordCompletionListener {
    private List<Word> wordList = new ArrayList<>();
    private AppDatabase db;
    private  GameDisplayAdapter adapter;
    private RecyclerView recyclerView;
    private int level;
    private String language;
    private int starEarned;
    private int starReward;
    private int currentLevel;
    private boolean isLevelUnlocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level1);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new GameDisplayAdapter(wordList,recyclerView,this, this, currentLevel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });

        level = 3;
        language = getIntent().getStringExtra("LANGUAGE_SELECTED");
        starReward = getIntent().getIntExtra("STAR_REWARD", 0);
        currentLevel = getIntent().getIntExtra("CURRENT_LEVEL", 0);
        isLevelUnlocked = getIntent().getBooleanExtra("UNLOCK_VALUE", false);
        recyclerView.setAdapter(adapter);

        loadJsonAndInsert();
    };

    private void loadJsonAndInsert() {

        new Thread(() -> {

            String jsonString = null;

            try {
                jsonString = JsonUtils.AssetsJsonFile(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Gson gson = new Gson();
            Type wordListType = new TypeToken<List<Word>>(){

            }.getType();
            List<Word> loadedWord = gson.fromJson(jsonString, wordListType);

            db = AppDatabase.getInstance(this);
            WordDao wordDao = db.wordDao();

            wordDao.insertAll(loadedWord);
            List<Word> gameWordList = wordDao.getWordsByLevel(level ,language);
            shuffleWordList(gameWordList);

        }).start();
    }

    private void shuffleWordList(List<Word> gameWordList) {

        Collections.shuffle(gameWordList);
        int maxIndex = Math.min(4, gameWordList.size());
        wordList.clear();
        wordList.addAll(gameWordList.subList(0, maxIndex));

        runOnUiThread(() -> {

            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onLastWordReached(int roundStarCount, int roundLife) {

        Intent intent = new Intent(this, GameSummary.class);
        intent.putExtra("STAR_EARNED", roundStarCount);
        intent.putExtra("LIFE_SUMMARY", roundLife);
        intent.putExtra("STAR_REWARD", starReward);
        startActivity(intent);
    }
    @Override
    public void onEndLevelUpdate() {

    }
}

