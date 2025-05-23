package com.minovative.guessify;

import static com.minovative.guessify.MethodHelper.shakeButton;
import static com.minovative.guessify.MethodHelper.showDialog;
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

import androidx.annotation.NonNull;

import java.util.List;

public class GridViewAdapter extends ArrayAdapter<Level> {

    private int starTotal;
    private List<Level> levelList;
    private String language;
    private Context context;
    private OnUnlockButtonClickedCallback unlockCallback;
    private int currentLevel;
    private int currentItems;
    private Activity activity;
    private Application application;
    private boolean levelUnlocked;

    public GridViewAdapter (Activity activity,List<Level> list,String language) {
        super(activity,0,list);
        this.levelList = list;
        this.context = activity;
        this.activity = activity;
        this.language = language;
        this.application = activity.getApplication();
    }

    // LiveData for levels state
    public void updateLevels (List<Level> newLevels) {

        this.levelList.clear();
        this.levelList.addAll(newLevels);
        notifyDataSetChanged();
    }

    public void updateStars (int currentStars) {

        this.starTotal = currentStars;
    }

    public void updateItem (int currentItems) {

        this.currentItems = currentItems;
    }

    // UnlockButton action to update level state to db
    public interface OnUnlockButtonClickedCallback {

        void onUnlockedButton (int currentLevel);
    }

    static class ViewHolder {
        ImageView lockButton;
        TextView levelTextView;
        TextView difficultyTextView;
        Button unlockButton;
        Button startButton;
    }

    @NonNull
    @Override
    public View getView (int position,View convertView,@NonNull ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_level_layout,parent,false);

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
        levelUnlocked = level.isUnlocked();
        currentLevel = level.getLevel();

        OnUnlockButtonClickedCallback unlockListener = (l -> {

            if (unlockCallback != null) {

                unlockCallback.onUnlockedButton(currentLevel);
            }
        });

        holder.startButton.setVisibility(View.GONE);
        holder.unlockButton.setVisibility(View.GONE);
        holder.lockButton.setVisibility(View.GONE);

        if (levelUnlocked) {

            new android.os.Handler().postDelayed(( ) -> {

                holder.startButton.setVisibility(View.VISIBLE);
                holder.unlockButton.clearAnimation();
            },250);

        } else if (!levelUnlocked && starTotal >= level.getStarRequired()) {

            holder.unlockButton.setVisibility(View.VISIBLE);
            holder.lockButton.setVisibility(View.VISIBLE);
            shakeButton(holder.unlockButton);

        } else {

            holder.unlockButton.setVisibility(View.VISIBLE);
            holder.lockButton.setVisibility(View.VISIBLE);
        }

        if (holder.levelTextView != null) {

            holder.levelTextView.setText("Level " + level.getLevel());
        }

        if (holder.difficultyTextView != null) {

            holder.difficultyTextView.setText(level.getDifficulty());
        }

        if (holder.unlockButton != null) {
            holder.unlockButton.setText("Unlock with " + level.getStarRequired() + " ⭐");
        }

        if (holder.startButton != null) {

            holder.startButton.setOnClickListener(view -> {
                startGameButton(level,level.getStarRewarded());
            });
        }

        int currentLevel = level.getLevel();
        int starRequired = level.getStarRequired();
        switch (currentLevel) {
            case 1:
                holder.unlockButton.setOnClickListener(View -> {

                    setButtonUnlockedLevel(level,holder);
                    saveLevelStateToDatabase(activity,currentLevel);
                    unlockListener.onUnlockedButton(currentLevel);
                    notifyDataSetChanged();
                });
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:

                holder.unlockButton.setOnClickListener(View -> {

                    int previousLevelNumber = currentLevel - 1;
                    Level previousLevel = null;

                    for (Level l : levelList) {

                        if (l.getLevel() == previousLevelNumber) {

                            previousLevel = l;
                            break;
                        }
                    }

                    if (previousLevel != null && previousLevel.isUnlocked()) {

                        unlockListener.onUnlockedButton(currentLevel);

                        if (starTotal >= starRequired) {

                            starTotal -= starRequired;

                            setButtonUnlockedLevel(level,holder);
                            saveStarsToDatabase(starTotal,application);
                            saveLevelStateToDatabase(activity,currentLevel);

                        } else if (starTotal < starRequired) {

                            showDialog(context,"You don't have enough ⭐");
                            return;
                        }
                    } else {

                        showDialog(context,"Unlock previous level first! 🔑");
                    }
                    notifyDataSetChanged();
                });
                break;
            default:
                holder.unlockButton.setOnClickListener(View -> {

                    setButtonUnlockedLevel(level,holder);
                });
        }
        return convertView;
    }

    private void setButtonUnlockedLevel (Level level,ViewHolder holder) {
        startGameButton(level,level.getStarRewarded());
        holder.unlockButton.setVisibility(View.GONE);
        holder.lockButton.setVisibility(View.GONE);
        holder.startButton.setVisibility(View.VISIBLE);
        level.setUnlocked(true);
        level.setPlay(true);
    }

    private void startGameButton (Level level, int starsRewardFromThisLevel) {

        Intent intent = null;

        try {
            intent = getActivityName(level.getLevel());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        intent.putExtra("LANGUAGE_SELECTED",language);
        intent.putExtra("HELP_ITEM",currentItems);
        intent.putExtra("STAR_REWARD",starsRewardFromThisLevel);
        intent.putExtra("STAR_TOTAL",starTotal);
        intent.putExtra("CURRENT_LEVEL",currentLevel);
        intent.putExtra("UNLOCK_VALUE",levelUnlocked);
        activity.startActivity(intent);
    }

    private Intent getActivityName (int level) throws ClassNotFoundException {
        Intent intent = null;

        switch (level) {
            case 1:
                return new Intent(getContext(),Level1Activity.class);

            case 2:
                return new Intent(getContext(),Level2Activity.class);

            case 3:
                return new Intent(getContext(),Level3Activity.class);

            case 4:
                return new Intent(getContext(),Level4Activity.class);

            case 5:
                return new Intent(getContext(),Level5Activity.class);

            case 6:
                return new Intent(getContext(),Level6Activity.class);

            case 7:
                return new Intent(getContext(),Level7Activity.class);

            case 8:
                return new Intent(getContext(),Level8Activity.class);

            case 9:
                return new Intent(getContext(),Level9Activity.class);

            case 10:
                return new Intent(getContext(),Level10Activity.class);

            case 11:
                return new Intent(getContext(),Level11Activity.class);

            case 12:
                return new Intent(getContext(),Level11Activity.class);

            default:
                return new Intent(getContext(),MainActivity.class);
        }
    }

}


