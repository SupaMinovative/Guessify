package com.minovative.guessify;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;

public class GameDisplayAdapter extends RecyclerView.Adapter<GameDisplayAdapter.GameDisplayViewHolder> {

    private List<Word> wordList;
    private RecyclerView recyclerView;

    private Context context;

    GameState gameState = new GameState();
    int starCount = gameState.getStarCount();
    int lifeCount = 3;
    int helpItem = gameState.getHelpItem();
    int wordCounter = 0;
    Word currentWord;
    private OnLastWordCompletionListener listener;
    int currentLevel;

    List<EditText> editTextList = new ArrayList<>();
    public GameDisplayAdapter(List<Word> wordList, RecyclerView recyclerView, Context context, OnLastWordCompletionListener listener, int currentLevel) {
        this.wordList = wordList;
        this.recyclerView = recyclerView;
        this.context = context;
        this.listener = listener;
        this.currentLevel = currentLevel;
    }

    // Interface for summary scores
    public interface OnLastWordCompletionListener {
        void onLastWordReached(int roundStarCount, int roundLife);

        // update level state play as played
        void onEndLevelUpdate();

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


        holder.starDisplay.setText(starCount + " ⭐");
        holder.lifeDisplay.setText(GuessingWord.generatedHeart(lifeCount));
        holder.helpItemText.setText(helpItem + " 🧩");

        holder.backButton.setOnClickListener(view -> {
            Intent i = new Intent(context, MainActivity.class);
            context.startActivity(i);
        });



        startGame(holder);

    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }


    private void startGame(GameDisplayViewHolder holder) {
        currentWord = wordList.get(wordCounter);
        GuessingWord word = new GuessingWord(currentWord.getWord());
        holder.helpItemText.setOnClickListener(view -> {

            helpItem--;
            holder.helpItemText.setText(helpItem + " 🧩");
            Log.d ("DEBUG", "HELP ITEM CLICKED");
            word.getWordHint(2, currentWord.getWord(), context, holder.wordContainer, editTextList);

        });
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

        new android.os.Handler().postDelayed(() -> {

            int wordListSize = wordList.size();
            if (wordCounter < wordListSize) {
                currentWord = wordList.get(wordCounter);
                GuessingWord newGuessWord = new GuessingWord(currentWord.getWord());
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

        //String[] randomCorrectArray = context.getResources().getStringArray(R.array.correct_feedback);

        //List<String> randomCorrect = Arrays.asList(randomCorrectArray);
        Collections.addAll(randomCorrect,"Well done!","Great! 👍","Nice!","Awesome!");
        Collections.shuffle(randomCorrect);
        holder.result.setTextColor(ContextCompat.getColor(context, R.color.green));
        holder.result.setText(randomCorrect.get(0));
        starCount++;
        holder.starDisplay.setText(starCount + " ⭐");
        wordCounter++;
        notifyItemChanged(wordCounter);
        proceedNextWord(holder);
        Log.d ("DEBUG" , "Current Level " + currentLevel + " is ending and triggering update stars on game end");
        updateStarOnGameEnd();
    }

    private void onIncorrectCheck(GameDisplayViewHolder holder) {
        if (lifeCount > 1) {
            lifeCount--;
            holder.lifeDisplay.setText(GuessingWord.generatedHeart(lifeCount));
            holder.result.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.result.setText("✗ Incorrect");
            wordCounter++;
            notifyItemChanged(wordCounter);
            proceedNextWord(holder);
        }
         else if (lifeCount == 1) {
            lifeCount--;
            holder.lifeDisplay.setText(GuessingWord.generatedHeart(lifeCount));
            holder.result.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.result.setText("✗ Incorrect");

            wordCounter++;
            notifyItemChanged(wordCounter);
            new android.os.Handler().postDelayed(() -> {
                setEmptyText(holder.hintTextView);
                setEmptyText(holder.starDisplay);
                setEmptyText(holder.helpItemText);
                setEmptyText(holder.result);
                holder.result.setText("GAME OVER");
                holder.result.setTextSize(40);
                holder.wordContainer.removeAllViews();
                holder.result.setTextColor(ContextCompat.getColor(context, R.color.red));
                holder.restartButton.setVisibility(View.VISIBLE);
                holder.checkButton.setVisibility(View.GONE);
                holder.overImg.setVisibility(View.VISIBLE);
        }, 1500);

            holder.restartButton.setOnClickListener(view -> {
                lifeCount = 3;
                starCount = 0;
                wordCounter = 0;

                holder.starDisplay.setText(starCount + " ⭐");
                holder.helpItemText.setText(helpItem + " 🧩");
                holder.lifeDisplay.setText(GuessingWord.generatedHeart(lifeCount));
                setEmptyText(holder.result);
                holder.result.setTextSize(25);
                holder.restartButton.setVisibility(View.GONE);
                holder.overImg.setVisibility(View.GONE);
                holder.checkButton.setVisibility(View.VISIBLE);
                startGame(holder);
            });

        }
    }



    private void setEmptyText(TextView textView) {
        textView.setText("");
    }
    private void updateStarOnGameEnd() {
        if (wordCounter == wordList.size()) {
            if (listener != null) {
                listener.onLastWordReached(starCount, lifeCount);
                listener.onEndLevelUpdate();
            }
        }
    }



    public static class GameDisplayViewHolder extends RecyclerView.ViewHolder {

        LinearLayout wordContainer;
        TextView starDisplay;
        TextView lifeDisplay;
        TextView helpItemText;
        TextView hintTextView;
        TextView result;
        Button checkButton;
        Button restartButton;
        Button backButton;
        ImageView overImg;
        public GameDisplayViewHolder(@NonNull View itemView) {
            super(itemView);

            starDisplay = itemView.findViewById(R.id.starDisplay);
            lifeDisplay = itemView.findViewById(R.id.lifeDisplay);
            helpItemText = itemView.findViewById(R.id.helpButton);
            hintTextView = itemView.findViewById(R.id.hintTextView);
            wordContainer = itemView.findViewById(R.id.wordContainer);
            checkButton = itemView.findViewById(R.id.checkButton);
            backButton = itemView.findViewById(R.id.backButton);
            result = itemView.findViewById(R.id.resultTextView);
            restartButton = itemView.findViewById(R.id.restartButton);
            overImg = itemView.findViewById(R.id.overImg);
        }


    }
}
