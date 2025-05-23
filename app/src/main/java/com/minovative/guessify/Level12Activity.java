package com.minovative.guessify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level12Activity extends AppCompatActivity implements GameDisplayAdapter.OnLastWordCompletionListener {
    private List<Word> wordList = new ArrayList<>();
    private AppDatabase db;
    private  GameDisplayAdapter adapter;
    private RecyclerView recyclerView;
    private int level;
    private String language;
    private int starTotal;
    private int starReward;
    private int currentLevel;
    private int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level1);

        currentItem = getIntent().getIntExtra("HELP_ITEM",0);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new GameDisplayAdapter(wordList,recyclerView,this, this, currentLevel, currentItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });

        level = 12;
        language = getIntent().getStringExtra("LANGUAGE_SELECTED");
        starTotal = getIntent().getIntExtra("STAR_TOTAL",0);
        starReward = getIntent().getIntExtra("STAR_REWARD", 0);
        currentLevel = getIntent().getIntExtra("CURRENT_LEVEL", 0);
        recyclerView.setAdapter(adapter);

        loadJsonAndInsert();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exist?")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton("No", null)
                .show();
    }

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

    @SuppressLint("NotifyDataSetChanged")
    private void shuffleWordList(List<Word> gameWordList) {

        Collections.shuffle(gameWordList);
        int maxIndex = Math.min(10, gameWordList.size());
        wordList.clear();
        wordList.addAll(gameWordList.subList(0, maxIndex));

        runOnUiThread(() -> {

            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onLastWordReached(int roundStarCount, int roundLife, int helpItem) {

        Intent intent = new Intent(this, GameSummary.class);

        intent.putExtra("STAR_TOTAL", starTotal);
        intent.putExtra("STAR_EARNED", roundStarCount);
        intent.putExtra("LIFE_SUMMARY", roundLife);
        intent.putExtra("STAR_REWARD", starReward);
        intent.putExtra("HELP_ITEM_TOTAL", helpItem);
        startActivity(intent);
    }
}

