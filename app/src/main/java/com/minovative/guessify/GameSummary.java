package com.minovative.guessify;


import static com.minovative.guessify.MainActivity.shakeButton;
import static com.minovative.guessify.SaveAndLoadDataHelper.saveStarsToDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class GameSummary extends AppCompatActivity {
    private TextView stars;
    private TextView starsEarned;
    private TextView starRewardText;
    private TextView bonusText;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_summary);

        stars = findViewById(R.id.starSummaryText);
        starsEarned = findViewById(R.id.starEarnedText);
        bonusText = findViewById(R.id.bonusText);
        nextButton = findViewById(R.id.nextButton);
        starRewardText = findViewById(R.id.starRewardText);

        int starCount = getIntent().getIntExtra("STAR_EARNED" ,0);
        int lifeCount = getIntent().getIntExtra("LIFE_SUMMARY" ,0);
        int starReward = getIntent().getIntExtra("STAR_REWARD" ,0);
        int totalStars = starCount+starReward+lifeCount;

        saveStarsToDatabase(totalStars, getApplication());

        stars.setText(generatedStar(lifeCount));

        starsEarned.setText("+ " + starCount);

        new android.os.Handler().postDelayed(() -> {

            for (int i = 1; i <= starReward; i++) {
                int finalI = i;
                new android.os.Handler().postDelayed(() -> {
                    starRewardText.setText("+" + finalI);

                },i * 150);
            }
        } ,1000);

        new android.os.Handler().postDelayed(() -> {

            for (int i = 1; i <= lifeCount; i++) {
                int finalI = i;
                new android.os.Handler().postDelayed(() -> {
                    bonusText.setText("+" + finalI);

                },i * 500);
            }
        } ,1000);

        shakeButton(starsEarned);
        shakeButton(starRewardText);
        shakeButton(bonusText);
        shakeButton(starRewardText);

        nextButton.setOnClickListener(view -> {
            Intent i = new Intent(GameSummary.this ,MainActivity.class);
            startActivity(i);
        });
    }

    public static String generatedStar(int count) {

        StringBuilder stars = new StringBuilder();

        for (int i = 0; i < count; i++) {
            stars.append("⭐");
        }

        if (count < 3) {

            for (int i = count; i < 3; i++) {
                stars.append("✰");
            }
        }
        return stars.toString();
    }
}