package com.minovative.guessify;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper {
    public static void loadJsonAndInsertWords(Activity activity, Context context,int level,String language,List<Word> wordList,
                                         GameDisplayAdapter adapter) {

        new Thread(() -> {

            String jsonString = null;

            try {
                jsonString = JsonUtils.AssetsJsonFile(context);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Gson gson = new Gson();
            Type wordListType = new TypeToken<List<Word>>(){

            }.getType();

            List<Word> loadedWord = gson.fromJson(jsonString, wordListType);

            AppDatabase db = AppDatabase.getInstance(activity.getApplicationContext());
            WordDao wordDao = db.wordDao();

            wordDao.insertAll(loadedWord);
            List<Word> gameWordList = wordDao.getWordsByLevel(level ,language);
            shuffleWordList(gameWordList, wordList, activity, adapter);

        }).start();
    }

    private static void shuffleWordList(List<Word> gameWordList, List<Word> wordList, Activity activity, GameDisplayAdapter adapter) {

        Collections.shuffle(gameWordList);
        int maxIndex = Math.min(10, gameWordList.size());
        wordList.clear();
        wordList.addAll(gameWordList.subList(0, maxIndex));

        activity.runOnUiThread(adapter::notifyDataSetChanged);

    }

    public static void loadJsonAndInsertLevel(Context context, String language, LevelDao levelDao) {

        new Thread(( ) -> {

            String jsonString = null;

            try {
                jsonString = LevelJsonUtils.AssetsJsonFile(context);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Gson gson = new Gson();
            Type levelListType = new TypeToken<List<Level>>() { }.getType();

            List<Level> loadedLevel = gson.fromJson(jsonString ,levelListType);
            List<Level> allLevels = levelDao.getLevelByLanguageSync(language);
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

}
