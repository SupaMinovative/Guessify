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
import android.widget.Toast;

import java.util.List;

public class GridViewAdapter extends ArrayAdapter<Level> {


    private int starReward;
    private int starTotal;
    private List<Level> levelList;

    String language;

    private Context context;

    private OnStarTotalLoaded starLoadedCallback;
    private OnUnlockButtonClickedCallback unlockCallback;

    private int finalStarTotal;
    private int currentLevel;
    private Activity activity;
    private Application application;
    private boolean levelUnlocked;

    private List<Level> list;

    public GridViewAdapter(Activity activity ,List<Level> list ,String language, Context context) {
        super(activity ,0 ,list);
        this.levelList = list;
        this.context = activity;
        this.activity = activity;
        this.language = language;
        this.application = activity.getApplication();

    }

    // LiveData for levels state
    public void updateLevels(List<Level> newLevels) {
        this.levelList.clear();
        this.levelList.addAll(newLevels);
        notifyDataSetChanged();
    }

    // UnlockButton action to update level state to db
    public interface OnUnlockButtonClickedCallback {
        void onUnlockedButton(int currentLevel);
    }

    public void setOnUnlockButtonClickedCallback(OnUnlockButtonClickedCallback callback) {
        this.unlockCallback = callback;
    }
    static class ViewHolder {
        ImageView lockButton;
        TextView levelTextView;
        TextView difficultyTextView;
        Button unlockButton;
        Button startButton;
    }

    @Override
    public View getView(int position ,View convertView , ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_level_layout ,parent ,false);

            holder = new ViewHolder();
            holder.levelTextView = convertView.findViewById(R.id.levelTextView);
            holder.difficultyTextView = convertView.findViewById(R.id.difficultyTextView);
            holder.unlockButton = convertView.findViewById(R.id.unlockButton);
            holder.lockButton = convertView.findViewById(R.id.lockButton);
            holder.startButton = convertView.findViewById(R.id.startButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

            Level level = getItem(position);

        assert level != null;
        boolean levelPlayed = level.isPlay();
            levelUnlocked = level.isUnlocked();
            currentLevel = level.getLevel();
            Log.d("DEBUG" ,"Position: " + position + ", Current level: " + currentLevel + ", Level " + level.getLevel());

            Log.d("DEBUG" ,"stars from database " + starTotal);

            OnUnlockButtonClickedCallback unlockListener = (v -> {
                if (unlockCallback != null) {
                    unlockCallback.onUnlockedButton(currentLevel);
                }
            });

            Log.d("DEBUG", "levelUnlocked: " + levelUnlocked + ", levelPlayed: " + levelPlayed);

            holder.startButton.setVisibility(View.GONE);
        holder.unlockButton.setVisibility(View.GONE);
        holder.lockButton.setVisibility(View.GONE);


            if (levelUnlocked) {
                Log.d("DEBUG", "This level " + currentLevel + " already unlocked");
                new android.os.Handler().postDelayed(() -> {
                    holder.startButton.setVisibility(View.VISIBLE);

                }, 250);

            } else if (!levelUnlocked && !levelPlayed) {
                holder.unlockButton.setVisibility(View.VISIBLE);
                holder.lockButton.setVisibility(View.VISIBLE);
                shakeButton(holder.unlockButton);
            }  /*else {
                    unlockButton.setVisibility(View.VISIBLE);
                    lockButton.setVisibility(View.VISIBLE);
                }*/

            if (holder.levelTextView != null) {

                holder.levelTextView.setText("Level " + level.getLevel());
            }

            if (holder.difficultyTextView != null) {

                holder.difficultyTextView.setText(level.getDifficulty());
            }


            if (holder.unlockButton != null) {
                if ("en".equals(language)) {
                    holder.unlockButton.setText("Unlock with " + level.getStarRequired() + " ⭐");
                } else if ("de".equals(language)) {
                    holder.unlockButton.setText("Freischalten mit " + level.getStarRequired() + " ⭐");
                }

            }

            if (holder.startButton != null) {

                    holder.startButton.setOnClickListener(view -> {
                        startGameButton(level ,level.getStarRewarded());
                    });
            }

            GameDisplayAdapter.OnLastWordCompletionListener listener = new GameDisplayAdapter.OnLastWordCompletionListener() {
                @Override
                public void onLastWordReached(int roundStarCount ,int roundLife) {

                }

                @Override
                public void onEndLevelUpdate( ) {
                    Log.d("DEBUG" ,"Level play status: " + level.isPlay());
                    level.setPlay(true);
                    Log.d("DEBUG" ,"Level unlocked: " + level.isUnlocked());
                    level.setUnlocked(true);
                    Log.d("DEBUG" ,"Save data triggered, updating level state. Level " + level + " Language " + language + " Current Level " + currentLevel);
                    saveLevelStateToDatabase(activity ,currentLevel ,language);
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
                            holder.unlockButton.setOnClickListener(View -> {

                                Log.d("DEBUG" ,"Intent clicked starting new intent with level " + level.getLevel() + "  Current level clicked : " + currentLevel);
                                setButtonUnlockedLevel(level, holder);
                                Log.d ("DEBUG" , "Data is being save with unlock state triggered from level " + currentLevel);
                                Log.d("DEBUG" ,"Position: " + position + ", Current level: " + currentLevel + ", Star Rewards " + starReward);
                                saveLevelStateToDatabase(activity ,currentLevel ,language);
                                unlockListener.onUnlockedButton(currentLevel);
                                notifyDataSetChanged();
                                Log.d("DEBUG" ,"setButtonUnlocked Listener triggered from level "  + currentLevel);

                            });
                            break;
                        case 2:
                        case 3:

                            holder.unlockButton.setOnClickListener(View -> {

                                unlockListener.onUnlockedButton(currentLevel);
                                Log.d("DEBUG" ,"Star from database" + finalStarTotal + "Star Required " + starRequired);
                                if (finalStarTotal >= starRequired) {
                                    finalStarTotal -= starRequired;

                                    Log.d ("DEBUG" , "Data is being save with unlock state triggered from level " + currentLevel);
                                    setButtonUnlockedLevel(level, holder);

                                    Log.d("DEBUG" ,"Position: " + position + ", Current level: " + currentLevel + ", Star Rewards " + level.getStarRewarded());
                                    saveLevelStateToDatabase(activity ,currentLevel ,language);

                                    Log.d("DEBUG" ,"setButtonUnlocked Listener triggered from level "  + currentLevel);
                                } else {
                                    Toast.makeText(context, "You don't have enough stars!", Toast.LENGTH_SHORT).show();

                                }
                                Log.d("DEBUG" ,"Intent clicked starting new intent with level " + level.getLevel() + "  Current level clicked : " + currentLevel);
                                Log.d("DEBUG" ,"stars after deducted" + finalStarTotal);
                                notifyDataSetChanged();
                                starLoadedCallback.onStarTotalUpdated(finalStarTotal);

                            });
                            break;
                        default:
                            holder.unlockButton.setOnClickListener(View -> {
                                setButtonUnlockedLevel(level, holder);
                            });
                    }
                }

                @Override
                public void onStarTotalUpdated(int updatedStarTotal) {
                    saveStarsToDatabase(finalStarTotal ,application);

                }
            });

        return convertView;
    }

    private void setButtonUnlockedLevel(Level level, ViewHolder holder) {
        startGameButton(level,level.getStarRewarded());
        holder.unlockButton.setVisibility(View.GONE);
        holder.lockButton.setVisibility(View.GONE);
        holder.startButton.setVisibility(View.VISIBLE);
        level.setUnlocked(true);
        level.setPlay(true);
    }

    public void getStarFromDatabase(OnStarTotalLoaded starLoadedCallback) {

        new Thread(( ) -> {

            AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
            GameStateDao gameStateDao = db.gameStateDao();

            starTotal = gameStateDao.getStarCount();

            starLoadedCallback.onStarTotalLoaded(starTotal);

        }).start();
    }

    private void startGameButton(Level level, int starsRewardFromThisLevel) {

        Intent intent = null;
        try {
            intent = getActivityName(level.getLevel());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        intent.putExtra("LANGUAGE_SELECTED" ,language);
        intent.putExtra("STAR_REWARD" ,starsRewardFromThisLevel);
        intent.putExtra("CURRENT_LEVEL" ,currentLevel);
        intent.putExtra("UNLOCK_VALUE" ,levelUnlocked);
        activity.startActivity(intent);

        Log.d("DEBUG" ,"LANGUAGE SELECTED " + language);
    }


    private Intent getActivityName(int level) throws ClassNotFoundException {
        Intent intent = null;

        switch (level) {
            case 1:
                return intent = new Intent(getContext() ,Level1Activity.class);

            case 2:
                return intent = new Intent(getContext() ,Level2Activity.class);

            case 3:
                return intent = new Intent(getContext() ,Level3Activity.class);
                /*
            case 4:
                return Level1Activity.class;
            case 5:
                return Level1Activity.class;*/
            default:
                return intent = new Intent(getContext() ,MainActivity.class);

        }
    }


}
