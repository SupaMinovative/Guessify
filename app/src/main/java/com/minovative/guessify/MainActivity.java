package com.minovative.guessify;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        star = findViewById(R.id.starMain);
        item = findViewById(R.id.itemTextView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull (getSupportActionBar ()).setTitle("");

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        db = AppDatabase.getInstance(this);
        levelDao = db.levelDao();
        gameStateDao = db.gameStateDao();

        prefs = getSharedPreferences("LangPrefs" ,MODE_PRIVATE);
        currentLanguage = prefs.getString("language" ,"en");

        GameStateViewModel gameStateViewModel = new ViewModelProvider(this,
                new GameStateViewModelFactory(getApplication(), gameStateDao))
                .get(GameStateViewModel.class);
        gameStateViewModel.getStarCount().observe(this, currentStarCount -> {

                    if (currentStarCount != null) {

                        adapter.updateStars(currentStarCount);
                        SaveAndLoadDataHelper.saveStarsToDatabase(currentStarCount, getApplication());
                        star.setText(currentStarCount + " ⭐");
                    }
                });

        gameStateViewModel.getHelpItem().observe(this, currentHelpItem -> {

            if (currentHelpItem != null) {

                adapter.updateItem(currentHelpItem);
                item.setText(currentHelpItem + " 🧩");
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

        loadJsonAndInsert();
        setAdapter();
    }

    public void setAdapter() {
        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(2);

        prefs = getSharedPreferences("LangPrefs" ,MODE_PRIVATE);
        currentLanguage = prefs.getString("language" ,"en");
        adapter = new GridViewAdapter(this ,new ArrayList<>() ,currentLanguage);
        gridView.setAdapter(adapter);
    }

private void loadJsonAndInsert() {

    new Thread(( ) -> {
/*
        db.levelDao().deleteAll();

        db.gameStateDao().deleteAll();*/
        String jsonString = null;

        try {
            jsonString = LevelJsonUtils.AssetsJsonFile(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        Type levelListType = new TypeToken<List<Level>>() { }.getType();

        List<Level> loadedLevel = gson.fromJson(jsonString ,levelListType);
        List<Level> allLevels = levelDao.getLevelByLanguageSync(currentLanguage);
        List<Level> levelsToInsert = new ArrayList<>();

        for (Level levelsFromJson : loadedLevel) {

            boolean exists = false;

            for (Level level : allLevels) {

                if (level.getLevel() == levelsFromJson.getLevel()) {

                    exists = true;
                    break;
                }
            }
            if (!exists) {

                levelsToInsert.add(levelsFromJson);
            }
        }
        if (!levelsToInsert.isEmpty()) {

            levelDao.insertAllLevelState(levelsToInsert);
        }
    }).start();
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
