package com.minovative.guessify;

import static com.minovative.guessify.GameLogicHelper.onCorrectCheck;
import static com.minovative.guessify.GameLogicHelper.onIncorrectCheck;
import static com.minovative.guessify.GameUIHelper.generateHeart;
import static com.minovative.guessify.GameUIHelper.onHelpItemClicked;
import static com.minovative.guessify.GameUIHelper.setEmptyText;
import static com.minovative.guessify.MethodHelper.moveGameOver;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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


import java.util.ArrayList;
import java.util.List;

public class GameDisplayAdapter extends RecyclerView.Adapter<GameDisplayAdapter.GameDisplayViewHolder> {

    private List<Word> wordList;
    private RecyclerView recyclerView;
    private Context context;
    private GameState gameState = new GameState();
    private int starCount;
    private int lifeCount = gameState.getLifeCount();
    private int wordCounter = gameState.getWordCounter();
    private Word currentWord;
    private OnLastWordCompletionListener listener;
    private Application application;
    private boolean gameStarted = false;
    private List<EditText> editTextList = new ArrayList<>();


    public GameDisplayAdapter(List<Word> wordList,RecyclerView recyclerView,Context context,
              Application application, OnLastWordCompletionListener listener) {
        this.wordList = wordList;
        this.recyclerView = recyclerView;
        this.context = context;
        this.listener = listener;
        this.application = application;
    }

    // Interface for summary scores
    public interface OnLastWordCompletionListener {
        void onLastWordReached(int roundStarCount,int roundLife, int helpItem);

    }

    @NonNull
    @Override
    public GameDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_display_card,parent,false);
        return new GameDisplayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameDisplayAdapter.GameDisplayViewHolder holder,int position) {

        holder.setIsRecyclable(false);

        holder.starDisplay.setText(starCount + " ⭐");
        holder.lifeDisplay.setText(generateHeart(gameState.getLifeCount()));
        holder.helpItemText.setText(gameState.getHelpItem() + " 🧩");

        holder.backButton.setOnClickListener(view -> {

            Intent i = new Intent(context,MainActivity.class);
            context.startActivity(i);
        });

        if (position == 0 && gameState.getWordCounter() == 0) {

            gameStarted = true;
            startGame(holder);
        }
    }

    @Override
    public int getItemCount( ) {
        return wordList.size();
    }

    private void startGame(GameDisplayViewHolder holder) {

        currentWord = wordList.get(gameState.getWordCounter());
        GuessingWord word = new GuessingWord(currentWord.getWord());

        word.showGuessUI(currentWord,context,editTextList,holder.hintTextView,holder.wordContainer
                ,holder.checkButton,new GuessingWord.AnswerCallBack() {

                    @Override
                    public void onCorrect() {
                        onCorrectCheck(holder, context, gameState, GameDisplayAdapter.this);
                        proceedNextWord(holder);
                        updateStarOnGameEnd();
                    }

                    @Override
                    public void onIncorrect( ) {

                        onIncorrectCheck(holder, gameState, context, GameDisplayAdapter.this);
                        proceedNextWord(holder);
                        updateStarOnGameEnd();
                        if (lifeCount == 1) {
                            onGameOver(holder);
                        }
                    }

                });

        holder.helpItemText.setOnClickListener(null);
        onHelpItemClicked(holder,word, gameState, context,currentWord, application);
    }


    private void proceedNextWord(GameDisplayViewHolder holder) {

        editTextList.clear();

        new android.os.Handler().postDelayed(( ) -> {

            holder.helpItemText.setText(gameState.getHelpItem() + " 🧩");

            int wordListSize = wordList.size();

            if (gameState.getWordCounter() < wordListSize) {

                currentWord = wordList.get(gameState.getWordCounter());
                GuessingWord newGuessWord = new GuessingWord(currentWord.getWord());

                holder.helpItemText.setOnClickListener(null);

                onHelpItemClicked(holder,newGuessWord, gameState,context,currentWord, application);

                newGuessWord.showNextWord(holder.wordContainer,editTextList,holder.result);

                newGuessWord.showGuessUI(currentWord,context,editTextList,holder.hintTextView
                        ,holder.wordContainer,holder.checkButton,new GuessingWord.AnswerCallBack() {
                            @Override
                            public void onCorrect( ) {
                                onCorrectCheck(holder, context, gameState, GameDisplayAdapter.this);

                                proceedNextWord(holder);
                                updateStarOnGameEnd();
                            }

                            @Override
                            public void onIncorrect( ) {

                                onIncorrectCheck(holder, gameState, context, GameDisplayAdapter.this);
                                proceedNextWord(holder);
                                updateStarOnGameEnd();
                                if (lifeCount == 1) {
                                    onGameOver(holder);
                                }
                            }

                        });
            }
        },1500);
    }


    private void onGameOver(GameDisplayViewHolder holder){

            new android.os.Handler().postDelayed(( ) -> {

                setEmptyText(holder.hintTextView);
                setEmptyText(holder.starDisplay);
                setEmptyText(holder.helpItemText);
                setEmptyText(holder.result);
                holder.result.setText("GAME OVER");
                holder.result.setTextSize(40);
                holder.wordContainer.removeAllViews();
                holder.result.setTextColor(ContextCompat.getColor(context,R.color.red));
                holder.restartButton.setVisibility(View.VISIBLE);
                holder.checkButton.setVisibility(View.GONE);
                holder.overImg.setVisibility(View.VISIBLE);
                moveGameOver(holder.overImg);
            },1500);

            holder.restartButton.setOnClickListener(view -> {

                gameStarted = false;
                holder.overImg.clearAnimation();
                holder.overImg.setVisibility(View.GONE);
                lifeCount = 3;
                starCount = 0;
                wordCounter = 0;
                holder.starDisplay.setText(starCount + " ⭐");
                holder.helpItemText.setText(gameState.getHelpItem() + " 🧩");
                holder.lifeDisplay.setText(generateHeart(lifeCount));
                setEmptyText(holder.result);
                holder.result.setTextSize(25);
                holder.restartButton.setVisibility(View.GONE);
                holder.checkButton.setVisibility(View.VISIBLE);
                startGame(holder);
            });
        }

    private void updateStarOnGameEnd( ) {
        if (gameState.getWordCounter()  == wordList.size()) {
            if (listener != null) {
                listener.onLastWordReached(gameState.getStarCount(),lifeCount, gameState.getHelpItem());
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