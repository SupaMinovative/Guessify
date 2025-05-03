package com.minovative.guessify;

import static com.minovative.guessify.GameDisplayAdapter.saveStarsToDatabase;
import static com.minovative.guessify.MainActivity.shakeButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.w3c.dom.Text;

import java.util.List;

public class GridViewAdapter extends ArrayAdapter<Level> {
    private TextView levelTextView;
    private TextView difficultyTextView;
    private Button unlockButton;

    private Button startButton;
    ImageView lockButton;
    private int starReward;
    private int starTotal;

    String language;

    private Context context;


    public GridViewAdapter(Context context ,List<Level> list ,String language) {
        super(context ,0 ,list);
        this.language = language;

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

            Level level = getItem(position);
            starReward = level.getStarRewarded();

            boolean levelUnlocked = level.isUnlocked();
            boolean levelPlay = level.isPlay();

            getStarFromDatabase();


            if (levelUnlocked != false && levelPlay) {
                shakeButton(unlockButton);
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

                int currentLevel = level.getLevel();
                int starRequired = level.getStarRequired();
                switch (currentLevel) {
                    case 1:
                        unlockButton.setOnClickListener(View -> {
                            level.setUnlocked(true);
                            level.setPlay(true);
                            startUnlockButton(level);
                        });
                        break;
                    case 2:

                        unlockButton.setOnClickListener(View -> {

                            startUnlockButton(level);
                           ((Activity) context).runOnUiThread(() -> {

                                starTotal -= starRequired;
                                saveStarsToDatabase(starTotal);
                                level.setUnlocked(true);
                                level.setPlay(true);
                            });
                        });
                        break;
                    default: unlockButton.setOnClickListener(View -> {
                        startUnlockButton(level);
                    });
                }

            };
        }
        return convertView;
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
