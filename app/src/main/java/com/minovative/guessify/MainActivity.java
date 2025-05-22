package com.minovative.guessify;

import static com.minovative.guessify.SaveAndLoadDataHelper.saveLevelStateToDatabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private TextView star;
    private SharedPreferences prefs;
    private String currentLanguage;
    Toolbar toolbar;
    GridViewAdapter adapter;
   // LevelViewModel viewModel;
    private Level level;


    List<Level> levelList = new ArrayList<>();
    private AppDatabase db;
    private LevelDao levelDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        star = findViewById(R.id.starMain);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        db = AppDatabase.getInstance(this);
        levelDao = db.levelDao();

        prefs = getSharedPreferences("LangPrefs" ,MODE_PRIVATE);
        currentLanguage = prefs.getString("language" ,"en");

        LevelViewModel levelViewModel = new ViewModelProvider(this,
                new LevelViewModelFactory(getApplication(), levelDao, currentLanguage))
                .get(LevelViewModel.class);

        levelViewModel.getLevelByLanguage(currentLanguage).observe(this, existingLevels -> {

            if (existingLevels != null) {
                adapter.updateLevels(existingLevels);
            }
        });

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            GameStateDao gameStateDao = db.gameStateDao();
            int starCount = gameStateDao.getStarCount();

            runOnUiThread(() -> {
                star.setText(starCount + " ⭐");
            });
        }).start();
        loadJsonAndInsert();
        setAdapter();

    }



    public void setAdapter() {
        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(2);

        prefs = getSharedPreferences("LangPrefs" ,MODE_PRIVATE);
        currentLanguage = prefs.getString("language" ,"en");
        adapter = new GridViewAdapter(this ,new ArrayList<>() ,currentLanguage, this);
        gridView.setAdapter(adapter);
    }
/*
        if ("en".equals(currentLanguage)) {

            levelList.add(new Level(1 ,"easy" ,0 ,5 ,true ,"en", false));
            levelList.add(new Level(2 ,"easy" ,10 ,10 ,false ,"en",false));
            levelList.add(new Level(3 ,"easy" ,20 ,20 ,false ,"en",false));

        }
        else if ("de".equals(currentLanguage)) {

            levelList.add(new Level(1,"einfach" ,0 ,5 ,true ,"en",false));
            levelList.add(new Level(2,"einfach" ,10 ,10 ,false ,"en",false));
            levelList.add(new Level(3 ,"einfach" ,20 ,20 ,false ,"en",false));
        }
        GridViewAdapter adapter = new GridViewAdapter(this ,levelList, currentLanguage);
        gridView.setAdapter(adapter);
    }*/
private void loadJsonAndInsert() {

    new Thread(( ) -> {

        String jsonString = null;

        try {
            jsonString = LevelJsonUtils.AssetsJsonFile(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        Type levelListType = new TypeToken<List<Level>>() {

        }.getType();
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
        /*
        List<Level> levelsToInsert = new ArrayList<>();

        for (Level level : loadedLevel) {
            boolean levelAlreadyExists = false;
            for (Level existingLevel : allLevels) {
                if (existingLevel.getLevel() == level.getLevel()) {
                    levelAlreadyExists = true;
                    break;
                }
            }
            if (!levelAlreadyExists) {
                levelsToInsert.add(level);
            }
        }
        if (!levelsToInsert.isEmpty()) {
            levelDao.insertAllLevelState(levelsToInsert);
        }
          levelList.clear();
        levelList.addAll(levelDao.getLevelByLanguage(currentLanguage));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(levelList, Comparator.comparingInt(Level::getLevel));
        }
        */





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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.language_bar) {
            showLanguageSelector();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void showLanguageSelector() {

        PopupMenu popup = new PopupMenu(this ,findViewById(R.id.language_bar));
        Menu menu = popup.getMenu();

        getMenuInflater().inflate(R.menu.menu_language ,menu);

        popup.setOnMenuItemClickListener(item -> {
            SharedPreferences prefs = getSharedPreferences("LangPrefs" ,MODE_PRIVATE);
            MenuItem langItem = toolbar.getMenu().findItem(R.id.language_bar);

            if (item.getItemId() == R.id.item_english) {
                prefs.edit().putString("language" ,"en").apply();
                langItem.setIcon(R.drawable.ic_en);
                currentLanguage = "en";

                Toast.makeText(this, "Language selected " + prefs.getString("language", "en"), Toast.LENGTH_LONG).show();
            } else if (item.getItemId() == R.id.item_german) {
                prefs.edit().putString("language" ,"de").apply();
                Toast.makeText(this, "Language selected " + prefs.getString("language", "de"), Toast.LENGTH_LONG).show();
                langItem.setIcon(R.drawable.ic_de);
                currentLanguage = "de";
            }
            recreate();
            return true;
        });

        popup.show();
    }


    public void saveLanguage(String language) {
        prefs = getSharedPreferences("LangPrefs" ,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", language);
        editor.apply();
    }


    public static void shakeButton(View view) {
        Animation shake = new TranslateAnimation(0,0,0,5);
        shake.setDuration(4000);
        shake.setInterpolator(new CycleInterpolator(5));
        shake.setRepeatMode(Animation.RESTART);
        shake.setRepeatCount(Animation.INFINITE);
        view.startAnimation(shake);
    };



    /*
    @Override
    public void onUnlockedButton(int currentLevel) {
            Log.d("DEBUG", "Main : Unlock Button listener is being called from " + currentLevel + " and saving data to database.");
            saveLevelStateToDatabase(this,currentLevel,currentLanguage);

    } */

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
