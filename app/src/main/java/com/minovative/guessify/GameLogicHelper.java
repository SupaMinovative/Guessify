package com.minovative.guessify;

import static com.minovative.guessify.GameUIHelper.generateHeart;

import android.content.Context;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameLogicHelper {
    public static void onCorrectCheck(GameDisplayAdapter.GameDisplayViewHolder holder,Context context,
                                      GameState gameState,GameDisplayAdapter adapter) {

        List<String> randomCorrect = new ArrayList<>();
        Collections.addAll(randomCorrect,"Well done! 😎","Great! 👍","Nice! 🔥","Awesome! ✨");
        Collections.shuffle(randomCorrect);

        holder.result.setTextColor(ContextCompat.getColor(context,R.color.green));
        holder.result.setText(randomCorrect.get(0));

        gameState.setStarCount(gameState.getStarCount() + 1);
        holder.starDisplay.setText(gameState.getStarCount() + " ⭐");

        gameState.setWordCounter(gameState.getWordCounter() + 1);

        adapter.notifyItemChanged(gameState.getWordCounter());
    }


    public static void onIncorrectCheck(GameDisplayAdapter.GameDisplayViewHolder holder,GameState gameState,Context context,
                                        GameDisplayAdapter adapter) {

        List<String> randomIncorrect = new ArrayList<>();
        Collections.addAll(randomIncorrect,"Too bad! 🫠","Try again! 😌","Missed! 😏","Not quite! 😋");
        Collections.shuffle(randomIncorrect);

        int lifeCount = gameState.getLifeCount();
        if (lifeCount > 1) {

            gameState.setLifeCount(gameState.getLifeCount() - 1);
            holder.lifeDisplay.setText(generateHeart(gameState.getLifeCount()));
            holder.result.setTextColor(ContextCompat.getColor(context,R.color.red));
            holder.result.setText(randomIncorrect.get(0));
            gameState.setWordCounter(gameState.getWordCounter() + 1);
            adapter.notifyItemChanged(gameState.getWordCounter());

        }else if (lifeCount == 1) {

            lifeCount--;
            holder.lifeDisplay.setText(generateHeart(lifeCount));
            holder.result.setTextColor(ContextCompat.getColor(context,R.color.red));
            holder.result.setText(randomIncorrect.get(0));
            gameState.setWordCounter(gameState.getWordCounter() + 1);
            adapter.notifyItemChanged(gameState.getWordCounter());


        }
    }


}
