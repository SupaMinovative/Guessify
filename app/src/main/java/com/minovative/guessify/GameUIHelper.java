package com.minovative.guessify;

import static com.minovative.guessify.MethodHelper.showDialog;
import static com.minovative.guessify.SaveAndLoadDataHelper.saveHelpItemToDatabase;

import android.app.Application;
import android.content.Context;
import android.widget.TextView;

public class GameUIHelper {


    public static void onHelpItemClicked(GameDisplayAdapter.GameDisplayViewHolder holder,GuessingWord newGuessWord,GameState gameState,
                                         Context context,Word currentWord,Application application){
        holder.helpItemText.setOnClickListener(view -> {

            if (gameState.getHelpItem() == 0) {

                showDialog(context, "You don't have any 🧩!");
                return;
            }

            boolean revealed = newGuessWord.getWordHint(1,currentWord.getWord(),context,holder.wordContainer);

            if (revealed) {

                gameState.setHelpItem(gameState.getHelpItem()-1);
                holder.helpItemText.setText(gameState.getHelpItem() + " 🧩");
                saveHelpItemToDatabase(gameState.getHelpItem(), application);

            } else {

                holder.helpItemText.setText("✅");
                holder.helpItemText.setOnClickListener(null);
            }
        });
    }

    public static String generateHeart(int count) {
        StringBuilder hearts = new StringBuilder();

        for (int i = 0; i < count; i++) {
            hearts.append("❤️");
        }
        return hearts.toString();
    }

    public static void setEmptyText(TextView textView) {
        textView.setText("");
    }

}
