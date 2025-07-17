package com.minovative.guessify;

import static com.minovative.guessify.DatabaseHelper.loadJsonAndInsertWords;
import static com.minovative.guessify.MethodHelper.onBackBtnPressed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Level7Activity extends AppCompatActivity {
    private List<Word> wordList = new ArrayList<>();
    private GameDisplayAdapter adapter;
    private RecyclerView recyclerView;
    private int level;
    private String language;
    private int starTotal;
    private int starReward;
    private int currentItem;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level1);

        onBackBtnPressed(this,this,getOnBackPressedDispatcher());

        currentItem = getIntent().getIntExtra("HELP_ITEM",0);

        recyclerView = findViewById(R.id.recyclerView);

        adapter = new GameDisplayAdapter(wordList,recyclerView,this,currentItem,this.getApplication(),
                ((int roundStarCount,int roundLife,int helpItem) -> {
                    Intent intent = new Intent(this,GameSummary.class);
                    intent.putExtra("STAR_TOTAL",starTotal);
                    intent.putExtra("STAR_EARNED",roundStarCount);
                    intent.putExtra("LIFE_SUMMARY",roundLife);
                    intent.putExtra("STAR_REWARD",starReward);
                    intent.putExtra("HELP_ITEM_TOTAL",helpItem);
                    Log.d("DEBUG","Starting summary intent");
                    startActivity(intent);
                }));

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false) {

            @Override
            public boolean canScrollHorizontally( ) {
                return false;
            }
        });

        level = 7;
        language = getIntent().getStringExtra("LANGUAGE_SELECTED");
        starTotal = getIntent().getIntExtra("STAR_TOTAL",0);
        starReward = getIntent().getIntExtra("STAR_REWARD",0);
        recyclerView.setAdapter(adapter);
        loadJsonAndInsertWords(this,this,level,language,wordList,adapter);

    }
}


