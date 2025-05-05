package com.minovative.guessify;

import static com.minovative.guessify.MainActivity.shakeButton;
import static com.minovative.guessify.SaveAndLoadDataHelper.saveLevelStateToDatabase;
import static com.minovative.guessify.SaveAndLoadDataHelper.saveStarsToDatabase;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends ArrayAdapter<Level> {

    ImageView lockButton;
    TextView levelTextView;
    TextView difficultyTextView;
    Button unlockButton;
    Button startButton;
    private int starReward;
    private int starTotal;
    private List<Level> levelList;

    String language;

    private Context context;

    private OnStarTotalLoaded starLoadedCallback;

    private int finalStarTotal;
    private Level level;
    private  int currentLevel;
    private Activity activity;
    private Application application;


    public GridViewAdapter(Activity activity,List<Level> list ,String language) {
        super(activity ,0 ,list);
        this.activity = activity;
        this.language = language;
          application = activity.getApplication();

        }



    @Override
    public View getView(int position ,View convertView ,ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_level_layout ,parent ,false);

            levelTextView = convertView.findViewById(R.id.levelTextView);
            difficultyTextView = convertView.findViewById(R.id.difficultyTextView);
            unlockButton = convertView.findViewById(R.id.unlockButton);
            lockButton = convertView.findViewById(R.id.lockButton);
            startButton = convertView.findViewById(R.id.startButton);


                Log.e("DEBUG" ,"Level is getting position.");


                level = getItem(position);


                currentLevel = level.getLevel();
                Log.d("DEBUG" ,"Position: " + position + ", Current level: " + currentLevel);
                starReward = level.getStarRewarded();

                boolean levelUnlocked = level.isUnlocked();
                boolean levelPlay = level.isPlay();

                Log.d("DEBUG" ,"stars from database" + starTotal);

                if (levelUnlocked) {
                    startButton.setVisibility(View.VISIBLE);
                } else if (!levelUnlocked && !levelPlay) {
                    unlockButton.setVisibility(View.VISIBLE);
                    lockButton.setVisibility(View.VISIBLE);
                    shakeButton(unlockButton);
                } else {
                    unlockButton.setVisibility(View.VISIBLE);
                    lockButton.setVisibility(View.VISIBLE);
                }

                if (levelTextView != null) {

                    levelTextView.setText("Level " + level.getLevel());
                }

                if (difficultyTextView != null) {

                    difficultyTextView.setText(level.getDifficulty());
                }

                if (unlockButton != null) {
                    if ("en".equals(language)) {

                        unlockButton.setText("Unlock with " + level.getStarRequired() + " ⭐");
                    } else if ("de".equals(language)) {
                        unlockButton.setText("Freischalten mit " + level.getStarRequired() + " ⭐");
                    }
                /*
                if (unlockButton != null) {

                    unlockButton.setOnClickListener(view -> {

                        startUnlockButton(level);


                        Intent intent = null;

                        try {
                            intent = getActivityName(level.getLevel());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        intent.putExtra("LANGUAGE_SELECTED" ,language);
                        intent.putExtra("STAR_REWARD" ,starReward);
                        getContext().startActivity(intent);
                        lockButton.setVisibility(View.GONE);
                        unlockButton.setVisibility(View.GONE);
                        startButton.setVisibility(View.GONE);

                        Log.d("DEBUG" ,"LANGUAGE SELECTED" + language);

                    });
                }*/


                }

                GameDisplayAdapter.OnLastWordCompletionListener listener = new GameDisplayAdapter.OnLastWordCompletionListener() {
                    @Override
                    public void onLastWordReached(int roundStarCount, int roundLife) {

                    }

                    @Override
                    public void onEndLevelUpdate() {
                        Log.d("DEBUG" ,"Level play status: " + level.isPlay());
                        level.setPlay(true);
                        Log.d("DEBUG" ,"Level unlocked: " + level.isUnlocked());
                        level.setUnlocked(true);
                        Log.d ("DEBUG", "Save data triggered, updating level state. Level " + level + " Language " + language);
                        saveLevelStateToDatabase(activity,currentLevel,language);
                    }
                };

                getStarFromDatabase(new OnStarTotalLoaded() {
                    @Override
                    public void onStarTotalLoaded(int loadedStarTotal) {
                        finalStarTotal = loadedStarTotal;
                        int currentLevel = level.getLevel();
                        int starRequired = level.getStarRequired();
                        switch (currentLevel) {
                            case 1:
                                unlockButton.setOnClickListener(View -> {

                                    Log.d("DEBUG" ,"Intent clicked starting new intent" + level);
                                    setButtonUnlockedLevel(level);
                                    Log.d("DEBUG" ,"setButtonUnlockedLevel triggered");

                                });
                                break;
                            case 2:

                                unlockButton.setOnClickListener(View -> {
                                    Log.d("DEBUG" ,"Current Level clicked" + level);
                                    Log.d("DEBUG" ,"Star from database" + finalStarTotal + "Star Required " + starRequired);
                                    if (finalStarTotal >= starRequired) {
                                        finalStarTotal -= starRequired;
                                    }
                                    startUnlockButton(level);
                                    Log.d("DEBUG" ,"Intent clicked starting new intent" + level);
                                    Log.d("DEBUG" ,"stars after deducted" + finalStarTotal);


                                    starLoadedCallback.onStarTotalUpdated(finalStarTotal);

                                });
                                break;
                            default:
                                unlockButton.setOnClickListener(View -> {
                                    startUnlockButton(level);
                                });
                        }
                    }

                    @Override
                    public void onStarTotalUpdated(int updatedStarTotal) {
                        saveStarsToDatabase(finalStarTotal, application);

                    }
                });
        } return convertView;
    }
/*
    public void setLevelList(List<Level> levelList) {
        this.levelList = levelList;
    }*/
    private void setButtonUnlockedLevel(Level level) {
        startUnlockButton(level);
        unlockButton.setVisibility(View.GONE);
        lockButton.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        level.setUnlocked(true);
        level.setPlay(true);
    }

    public void getStarFromDatabase(OnStarTotalLoaded starLoadedCallback) {

        new Thread(() -> {


            AppDatabase db = AppDatabase.getInstance(getContext());
            GameStateDao gameStateDao = db.gameStateDao();

             starTotal = gameStateDao.getStarCount();

             starLoadedCallback.onStarTotalLoaded(starTotal);



        }).start();
    }



    private void startUnlockButton(Level level) {


            Intent intent = null;
            try {
                intent = getActivityName(level.getLevel());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
                intent.putExtra("LANGUAGE_SELECTED", language);
                intent.putExtra("STAR_REWARD", starReward);
        intent.putExtra("CURRENT_LEVEL", currentLevel);
 getContext().startActivity(intent);
                lockButton.setVisibility(View.GONE);
                unlockButton.setVisibility(View.GONE);
                startButton.setVisibility(View.GONE);

        Log.d("DEBUG" ,"LANGUAGE SELECTED" + language);
                };


    private Intent getActivityName(int level) throws ClassNotFoundException {
        Intent intent   = null;

        switch (level) {
            case 1:
                return intent = new Intent(getContext(), Level1Activity.class);

            case 2:
                return intent = new Intent(getContext(), Level2Activity.class);
               /* return Level2Activity.class;
            case 3:
                return Level1Activity.class;
            case 4:
                return Level1Activity.class;
            case 5:
                return Level1Activity.class;*/
            default: return intent = new Intent(getContext(), MainActivity.class);

        }
    }



}
