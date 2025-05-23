package com.minovative.guessify;

import static com.minovative.guessify.MethodHelper.moveGameOver;
import static com.minovative.guessify.MethodHelper.showDialog;

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
import java.util.Collections;
import java.util.List;

public class GameDisplayAdapter extends RecyclerView.Adapter<GameDisplayAdapter.GameDisplayViewHolder> {

    private List<Word> wordList;
    private RecyclerView recyclerView;
    private Context context;
    private GameState gameState = new GameState();
    private int starCount;
    private int lifeCount = 3;
    private int helpItem = gameState.getHelpItem();
    private int wordCounter = 0;
    private Word currentWord;
    private OnLastWordCompletionListener listener;
    private int currentLevel;
    private boolean gameStarted = false;
    private List<EditText> editTextList = new ArrayList<>();
    private int itemCount;

    public GameDisplayAdapter(List<Word> wordList,RecyclerView recyclerView,Context context
            ,OnLastWordCompletionListener listener,int currentLevel, int itemCount) {
        this.wordList = wordList;
        this.recyclerView = recyclerView;
        this.context = context;
        this.listener = listener;
        this.currentLevel = currentLevel;
        this.itemCount = itemCount;
        helpItem = itemCount;
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
        holder.lifeDisplay.setText(GuessingWord.generateHeart(lifeCount));
        holder.helpItemText.setText(helpItem + " 🧩");

        holder.backButton.setOnClickListener(view -> {

            Intent i = new Intent(context,MainActivity.class);
            context.startActivity(i);
        });


        if (position == 0 && wordCounter == 0) {

            gameStarted = true;
            startGame(holder);
        }
    }

    @Override
    public int getItemCount( ) {
        return wordList.size();
    }

    private void startGame(GameDisplayViewHolder holder) {

        currentWord = wordList.get(wordCounter);
        GuessingWord word = new GuessingWord(currentWord.getWord());

        word.showGuessUI(word,currentWord,context,editTextList,holder.hintTextView,holder.wordContainer
                ,holder.checkButton,new GuessingWord.AnswerCallBack() {

            @Override
            public void onCorrect( ) {
                onCorrectCheck(holder);
            }

            @Override
            public void onIncorrect( ) {
                onIncorrectCheck(holder);
            }
        });

        holder.helpItemText.setOnClickListener(null);
        holder.helpItemText.setOnClickListener(view -> {

            if (helpItem == 0) {

                showDialog(context, "You don't have any 🧩!");
                return;
            }

            boolean revealed = word.getWordHint(1,currentWord.getWord(),context,holder.wordContainer);

            if (revealed) {

                helpItem--;
                holder.helpItemText.setText(helpItem + " 🧩");

            } else {

                holder.helpItemText.setText("✅");
                holder.helpItemText.setOnClickListener(null);
            }
        });
    }

    private void proceedNextWord(GameDisplayViewHolder holder) {

        editTextList.clear();

        new android.os.Handler().postDelayed(( ) -> {

            holder.helpItemText.setText(helpItem + " 🧩");

            int wordListSize = wordList.size();

            if (wordCounter < wordListSize) {

                currentWord = wordList.get(wordCounter);
                GuessingWord newGuessWord = new GuessingWord(currentWord.getWord());

                holder.helpItemText.setOnClickListener(null);
                holder.helpItemText.setOnClickListener(view -> {

                    if (helpItem == 0) {

                        showDialog(context, "You don't have any 🧩!");
                        return;
                    }

                    boolean revealed = newGuessWord.getWordHint(1,currentWord.getWord(),context,holder.wordContainer);

                    if (revealed) {

                        helpItem--;
                        holder.helpItemText.setText(helpItem + " 🧩");

                    } else {

                        holder.helpItemText.setText("✅");
                        holder.helpItemText.setOnClickListener(null);
                    }
                });

                newGuessWord.showNextWord(holder.wordContainer,editTextList,holder.result);
                newGuessWord.showGuessUI(newGuessWord,currentWord,context,editTextList,holder.hintTextView
                        ,holder.wordContainer,holder.checkButton,new GuessingWord.AnswerCallBack() {
                    @Override
                    public void onCorrect( ) {
                        onCorrectCheck(holder);
                    }

                    @Override
                    public void onIncorrect( ) {
                        onIncorrectCheck(holder);
                    }
                });
            }
        },1500);
    }

    private void onCorrectCheck(GameDisplayViewHolder holder) {

        List<String> randomCorrect = new ArrayList<>();
        Collections.addAll(randomCorrect,"Well done! 😎","Great! 👍","Nice! 🔥","Awesome! ✨");
        Collections.shuffle(randomCorrect);

        holder.result.setTextColor(ContextCompat.getColor(context,R.color.green));
        holder.result.setText(randomCorrect.get(0));

        starCount++;
        holder.starDisplay.setText(starCount + " ⭐");

        wordCounter++;
        notifyItemChanged(wordCounter);
        proceedNextWord(holder);
        updateStarOnGameEnd();
    }

    private void onIncorrectCheck(GameDisplayViewHolder holder) {

        List<String> randomIncorrect = new ArrayList<>();
        Collections.addAll(randomIncorrect,"Too bad! 🫠","Try again! 😌","Missed! 😏","Not quite! 😋");
        Collections.shuffle(randomIncorrect);

        if (lifeCount > 1) {

            lifeCount--;
            holder.lifeDisplay.setText(GuessingWord.generateHeart(lifeCount));
            holder.result.setTextColor(ContextCompat.getColor(context,R.color.red));
            holder.result.setText(randomIncorrect.get(0));
            wordCounter++;
            notifyItemChanged(wordCounter);
            proceedNextWord(holder);
            updateStarOnGameEnd();

        } else if (lifeCount == 1) {

            lifeCount--;
            holder.lifeDisplay.setText(GuessingWord.generateHeart(lifeCount));
            holder.result.setTextColor(ContextCompat.getColor(context,R.color.red));
            holder.result.setText(randomIncorrect.get(0));
            wordCounter++;
            notifyItemChanged(wordCounter);

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
                holder.helpItemText.setText(helpItem + " 🧩");
                holder.lifeDisplay.setText(GuessingWord.generateHeart(lifeCount));
                setEmptyText(holder.result);
                holder.result.setTextSize(25);
                holder.restartButton.setVisibility(View.GONE);
                holder.checkButton.setVisibility(View.VISIBLE);
                startGame(holder);
            });
        }
    }

    private void setEmptyText(TextView textView) {
        textView.setText("");
    }

    private void updateStarOnGameEnd( ) {
        if (wordCounter == wordList.size()) {
            if (listener != null) {
                listener.onLastWordReached(starCount,lifeCount, helpItem);
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
