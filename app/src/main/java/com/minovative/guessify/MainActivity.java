package com.minovative.guessify;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private TextView star;

    private SharedPreferences prefs;
    private String currentLanguage;
    Toolbar toolbar;

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

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            GameStateDao gameStateDao = db.gameStateDao();
            int starCount = gameStateDao.getStarCount();

            runOnUiThread(() -> {
                star.setText(starCount + " ⭐");
            });
        }).start();

        setAdapter();

    }


    public void setAdapter() {
        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(2);
        List<Level> levelList = new ArrayList<>();
        prefs = getSharedPreferences("LangPrefs",  MODE_PRIVATE);
        currentLanguage = prefs.getString("language", "en");

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main ,menu);

        prefs = getSharedPreferences("LangPrefs" ,MODE_PRIVATE);
        currentLanguage = prefs.getString("language" ,"en");


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

    @Override
    protected void onPause() {
        saveLanguage(currentLanguage);
        super.onPause();

    }


    @Override
    protected void onDestroy() {
        saveLanguage(currentLanguage);
        super.onDestroy();

    }

    public static void shakeButton(View view) {
        Animation shake = new TranslateAnimation(0,0,0,5);
        shake.setDuration(4000);
        shake.setInterpolator(new CycleInterpolator(5));
        shake.setRepeatMode(Animation.RESTART);
        shake.setRepeatCount(Animation.INFINITE);
        view.startAnimation(shake);
    };

}
