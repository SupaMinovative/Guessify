package com.minovative.guessify;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameDisplayAdapter extends RecyclerView.Adapter<GameDisplayAdapter.GameDisplayViewHolder> {

    private List<Word> wordList;
    private RecyclerView recyclerView;

    private static Context context;

    GameState gameState = new GameState();
    int starCount = gameState.getStarCount();
    int lifeCount = 3;
    int helpItem = gameState.getHelpItem();
    int wordCounter = 0;
    Word currentWord;
    private OnLastWordCompletionListener listener;

    List<EditText> editTextList = new ArrayList<>();
    public GameDisplayAdapter(List<Word> wordList, RecyclerView recyclerView, Context context, OnLastWordCompletionListener listener) {
        this.wordList = wordList;
        this.recyclerView = recyclerView;
        this.context = context;
        this.listener = listener;
    }

    public interface OnLastWordCompletionListener {
        void onLastWordReached(int roundStarCount, int roundLife);

    }
    @NonNull
    @Override
    public GameDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_display_card, parent, false);
        return new GameDisplayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameDisplayAdapter.GameDisplayViewHolder holder,int position) {

        holder.setIsRecyclable(false);

        Word currentWord = wordList.get(position);

        Log.d("DEBUG","current word " + wordList.get(position));
        holder.starDisplay.setText(starCount + " ⭐");
        holder.lifeDisplay.setText(GuessingWord.generatedHeart(lifeCount));
        holder.helpItem.setText(helpItem + " 🧩");

        Log.d("DEBUG","first guessing word " + currentWord.getWord());
        startGame(holder);


    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }


    private void startGame(GameDisplayViewHolder holder) {
        currentWord = wordList.get(wordCounter);
        GuessingWord word = new GuessingWord(currentWord.getWord());

        word.showGuessUI(word,currentWord,context,editTextList,holder.hintTextView,holder.wordContainer,holder.checkButton,new GuessingWord.AnswerCallBack() {

            @Override
            public void onCorrect() {
                onCorrectCheck(holder);
            }

            @Override
            public void onIncorrect() {
                onIncorrectCheck(holder);
            }

        });

    }

    private void proceedNextWord(GameDisplayViewHolder holder) {


        Log.d("DEBUG","first guessing word " + currentWord.getWord());

        new android.os.Handler().postDelayed(() -> {


            int wordListSize = wordList.size();
            if (wordCounter < wordListSize) {
                currentWord = wordList.get(wordCounter);
                GuessingWord newGuessWord = new GuessingWord(currentWord.getWord());
                Log.d("DEBUG","next guessing word counter " + wordCounter);
                newGuessWord.showNextWord(holder.wordContainer,editTextList,holder.result);
                newGuessWord.showGuessUI(newGuessWord,currentWord,context,editTextList,holder.hintTextView,holder.wordContainer,holder.checkButton,new GuessingWord.AnswerCallBack() {
                    @Override
                    public void onCorrect() {
                        onCorrectCheck(holder);
                    }

                    @Override
                    public void onIncorrect() {
                        onIncorrectCheck(holder);
                    }
                });
            };
        }, 1500);
    }


    private void onCorrectCheck(GameDisplayViewHolder holder) {
        List<String> randomCorrect = new ArrayList<>();
        Collections.addAll(randomCorrect,"Well done!","Great! 👍","Nice!","Awesome!");
        Collections.shuffle(randomCorrect);
        Log.d("DEBUG","correct feedback " + randomCorrect.get(0));
        holder.result.setTextColor(context.getResources().getColor(R.color.green));
        holder.result.setText(randomCorrect.get(0));
        Log.d("DEBUG","correct");
        starCount++;
        holder.starDisplay.setText(starCount + " ⭐");
        wordCounter++;
        notifyItemChanged(wordCounter);
        Log.d("DEBUG","correct + word counter " + wordCounter);
        proceedNextWord(holder);
        updateStarOnGameEnd();
    }

    private void onIncorrectCheck(GameDisplayViewHolder holder) {

        lifeCount--;
        holder.lifeDisplay.setText(GuessingWord.generatedHeart(lifeCount));
        holder.result.setTextColor(context.getResources().getColor(R.color.red));
        holder.result.setText("✗ Incorrect");
        wordCounter++;
        notifyItemChanged(wordCounter);
        Log.d("DEBUG","incorrect + word counter " + wordCounter);
        proceedNextWord(holder);
        updateStarOnGameEnd();

    }


    private void updateStarOnGameEnd() {
        if (wordCounter == wordList.size()) {
            if (listener != null) {
                Log.d ("DEBUG", "On last card reached");
                listener.onLastWordReached(starCount, lifeCount);
            }
        }
    }
    public static void saveStarsToDatabase(int starCount) {

         new Thread(() -> {


                 AppDatabase db = AppDatabase.getInstance(context);
                 GameStateDao gameStateDao = db.gameStateDao();

             GameState currentState = gameStateDao.getGameState();

            if (currentState == null) {
                currentState = new GameState();
                currentState.setStarCount(starCount);
                gameStateDao.insertGameState(currentState);
            } else {
                currentState.setStarCount(starCount);
                gameStateDao.insertGameState(currentState);
            }
         }).start();
     }
    public static class GameDisplayViewHolder extends RecyclerView.ViewHolder {

        LinearLayout wordContainer;
        TextView starDisplay;
        TextView lifeDisplay;
        TextView helpItem;
        TextView hintTextView;
        TextView result;
        Button checkButton;
        public GameDisplayViewHolder(@NonNull View itemView) {
            super(itemView);

            starDisplay = itemView.findViewById(R.id.starDisplay);
            lifeDisplay = itemView.findViewById(R.id.lifeDisplay);
            helpItem = itemView.findViewById(R.id.helpButton);
            hintTextView = itemView.findViewById(R.id.hintTextView);
            wordContainer = itemView.findViewById(R.id.wordContainer);
            checkButton = itemView.findViewById(R.id.checkButton);
            result = itemView.findViewById(R.id.resultTextView);
        }


    }
}
