package com.minovative.guessify;

import static com.minovative.guessify.MethodHelper.generateStars;
import static com.minovative.guessify.MethodHelper.shakeButton;
import static com.minovative.guessify.SaveAndLoadDataHelper.saveHelpItemToDatabase;
import static com.minovative.guessify.SaveAndLoadDataHelper.saveStarsToDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class GameSummary extends AppCompatActivity {
    private TextView stars;
    private TextView starsEarned;
    private TextView starRewardText;
    private TextView bonusText;
    private TextView itemBonusText;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_summary);

        stars = findViewById(R.id.starSummaryText);
        starsEarned = findViewById(R.id.starEarnedText);
        bonusText = findViewById(R.id.bonusText);
        nextButton = findViewById(R.id.backBtnShop);
        starRewardText = findViewById(R.id.starRewardText);
        itemBonusText = findViewById(R.id.itemBonusText);

        int starTotal = getIntent().getIntExtra("STAR_TOTAL" ,0);
        int starCount = getIntent().getIntExtra("STAR_EARNED" ,0);
        int lifeCount = getIntent().getIntExtra("LIFE_SUMMARY" ,0);
        int starReward = getIntent().getIntExtra("STAR_REWARD" ,0);
        int helpItemTotal = getIntent().getIntExtra("HELP_ITEM_TOTAL" ,0);
        int helpItemReward = helpItemTotal + lifeCount;
        int allStarsFromThisLevel = starTotal+starCount+starReward+lifeCount;
        saveStarsToDatabase(allStarsFromThisLevel, getApplication());
        saveHelpItemToDatabase(helpItemReward, getApplication());

        stars.setText(generateStars(lifeCount));
        starsEarned.setText("+ " + starCount);

        new android.os.Handler().postDelayed(() -> {

            for (int i = 1; i <= starReward; i++) {
                int finalI = i;
                new android.os.Handler().postDelayed(() -> {
                    starRewardText.setText("+" + finalI);
                },i * 125);
            }
        } ,1000);

        new android.os.Handler().postDelayed(() -> {

            for (int i = 1; i <= lifeCount; i++) {
                int finalI = i;
                new android.os.Handler().postDelayed(() -> {
                    bonusText.setText("+" + finalI);
                    itemBonusText.setText("+" + finalI);
                },i * 500);
            }
        } ,1000);

        shakeButton(starsEarned);
        shakeButton(starRewardText);
        shakeButton(bonusText);
        shakeButton(starRewardText);
        shakeButton(itemBonusText);

        nextButton.setOnClickListener(view -> {

            Intent i = new Intent(GameSummary.this ,MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}