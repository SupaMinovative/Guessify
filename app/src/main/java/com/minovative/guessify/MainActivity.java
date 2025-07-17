package com.minovative.guessify;

import static com.minovative.guessify.DatabaseHelper.loadJsonAndInsertLevel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private TextView star;
    private TextView item;
    private SharedPreferences prefs;
    private String currentLanguage;
    private Toolbar toolbar;
    private GridViewAdapter adapter;
    private AppDatabase db;
    private LevelDao levelDao;
    private GameStateDao gameStateDao;
    private ImageView shopIcon;
    private int totalStar;
    private int totalHelpItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        star = findViewById(R.id.starMain);
        item = findViewById(R.id.itemTextView);
        toolbar = findViewById(R.id.toolbar);
        shopIcon = findViewById(R.id.shopIcon);
        setSupportActionBar(toolbar);
        GameState gameState = new GameState();

        Objects.requireNonNull (getSupportActionBar ()).setTitle("");

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        db = AppDatabase.getInstance(this);
        levelDao = db.levelDao();
        gameStateDao = db.gameStateDao();

        star.setText(gameState.getStarCount() + " ");

        item.setText(gameState.getHelpItem() + "  🧩");

        prefs = getSharedPreferences("LangPrefs" ,MODE_PRIVATE);
        currentLanguage = prefs.getString("language" ,"en");

        GameStateViewModel gameStateViewModel = new ViewModelProvider(this,
                new GameStateViewModelFactory(getApplication(), gameStateDao))
                .get(GameStateViewModel.class);
        gameStateViewModel.getStarCount().observe(this, currentStarCount -> {

                    if (currentStarCount != null) {

                        adapter.updateStars(currentStarCount);
                        star.setText(currentStarCount + "");
                        totalStar = currentStarCount;
                    }
                });

        gameStateViewModel.getHelpItem().observe(this, currentHelpItem -> {

            if (currentHelpItem != null) {

                adapter.updateItem(currentHelpItem);
                item.setText(currentHelpItem + "  🧩");
                totalHelpItem = currentHelpItem;

            }
        });

        LevelViewModel levelViewModel = new ViewModelProvider(this,
                new LevelViewModelFactory(getApplication(), levelDao, currentLanguage))
                .get(LevelViewModel.class);

        levelViewModel.getLevelByLanguage(currentLanguage).observe(this, existingLevels -> {

            if (existingLevels != null) {

                adapter.updateLevels(existingLevels);
            }
        });

        loadJsonAndInsertLevel(this, currentLanguage,levelDao);
        setAdapter();

        shopIcon.setOnClickListener(view -> {
            Intent i = new Intent(this, ShopActivity.class);
            i.putExtra("CURRENT_STAR",totalStar);
            i.putExtra("CURRENT_HELP_ITEM",totalHelpItem);
            startActivity(i);
        });
    }

    public void setAdapter() {
        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(2);
        prefs = getSharedPreferences("LangPrefs" ,MODE_PRIVATE);
        currentLanguage = prefs.getString("language" ,"en");
        adapter = new GridViewAdapter(this ,new ArrayList<>() ,currentLanguage);
        gridView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main ,menu);

        if ("en".equals(currentLanguage)) {
            menu.findItem(R.id.language_bar).setIcon(R.drawable.ic_en);
        } else {
            menu.findItem(R.id.language_bar).setIcon(R.drawable.ic_de);
        }
        return true;
    }

    public void saveLanguage(String language) {
        prefs = getSharedPreferences("LangPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", language);
        editor.apply();
    }


    @Override protected void onResume() {

        saveLanguage(currentLanguage);
        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
        saveLanguage(currentLanguage);
    }

    @Override
    protected void onDestroy() {

        saveLanguage(currentLanguage);
        super.onDestroy();
    }
}
