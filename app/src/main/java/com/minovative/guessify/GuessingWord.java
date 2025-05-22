package com.minovative.guessify;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GuessingWord {
    private String originalWord;
    Set<Integer> missingIndexes;
    private StringBuilder maskedWord;


    public GuessingWord(String originalWord) {
        this.originalWord = originalWord;
        this.missingIndexes = new HashSet<>();
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }

    public interface AnswerCallBack {
        void onCorrect();
        void onIncorrect();
    }
    public String maskedOriginalWord() {

        //indexes size to be missing
        int numMissing = originalWord.length() / 2;

        Random random = new Random();

        while (missingIndexes.size() < numMissing) {

            int index = random.nextInt(originalWord.length());
            missingIndexes.add(index);
        }

          maskedWord = new StringBuilder();

        for (int i = 0; i < originalWord.length(); i++) {

            if (missingIndexes.contains(i)) {
                maskedWord.append('_');

            } else {
                maskedWord.append(originalWord.charAt(i));
            }

        } return maskedWord.toString();
    }



    public boolean isCorrect(List<String> userInput) {

        StringBuilder userGuess = new StringBuilder();
        int inputIndex = 0;

        Log.d ("DEBUG", "ORIGINAL WORD FROM CORRECT CHECK " + originalWord);

        for (int i = 0; i < originalWord.length(); i++) {

            if (missingIndexes.contains(i)) {
                userGuess.append(userInput.get(inputIndex));
                inputIndex++;
            } else {

                userGuess.append(originalWord.charAt(i));
            }
        } return userGuess.toString().toLowerCase().equals(originalWord);
    }

    public void showGuessUI(GuessingWord word, Word currentWord, Context context,
                            List<EditText> editTextList, TextView hintTextView,
                            LinearLayout wordContainer, Button check, AnswerCallBack callBack) {



        String masked = word.maskedOriginalWord();
        List<View> viewList = new ArrayList<>();

        hintTextView.setText("💡 " + currentWord.getHint());
        for (int i = 0; i < masked.length(); i++) {
            char c = masked.charAt(i);
            if (c == '_') {

                EditText editText = new EditText(context);
                editText.setWidth(125);
                editText.setGravity(Gravity.CENTER);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
                editText.setPadding(5,5,5,5);
                editText.setTextSize(40);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setTag(i);
                viewList.add(editText);
                editTextList.add(editText);
                wordContainer.addView(editText);

                int currentIndex = editTextList.size() -1;

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence ,int i ,int i1 ,int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence ,int i ,int i1 ,int i2) {

                        if (charSequence.length() == 1) {
                            if (currentIndex +1 < editTextList.size()) {

                                editTextList.get(currentIndex +1).requestFocus();
                            }
                        }

                        editText.setOnKeyListener((v, keyCode,event) -> {

                            if (keyCode == KeyEvent.KEYCODE_DEL && editText.getText().length() == 0) {

                                if (currentIndex -1 >= 0) {

                                    editTextList.get(currentIndex - 1).requestFocus();

                                }
                            } return false;
                        });
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

            } else {

                TextView textView = new TextView(context);
                textView.setText(String.valueOf(c));
                textView.setPadding(5,5,5,5);
                textView.setTextSize(40);
                textView.setTag(i);
                viewList.add(textView);
                wordContainer.addView(textView);
            }
        }

        check.setOnClickListener(view -> {
            List<String> userInput = new ArrayList<>();
            for (EditText et : editTextList) {
                userInput.add(et.getText().toString());
            }

            boolean correct = word.isCorrect(userInput);


            if (correct) {
                callBack.onCorrect();

            }
            if (!correct) {
                callBack.onIncorrect();
            }

        });
    }
    public void getWordHint(int numToReveal, String word, Context context,
                            LinearLayout wordContainer, List<EditText> editTextList) {


        List<Integer> missingList = new ArrayList<>(missingIndexes);
        Random random = new Random();

        int revealCount = Math.min(numToReveal, missingIndexes.size());

        int ranIndex = random.nextInt(missingList.size());
        int indexToReveal = missingList.get(ranIndex);
        Log.d ("DEBUG", "ORIGINAL WORD:" + word);
        Log.d ("DEBUG", "INDEX TO REVEAL " + indexToReveal + "Char at original " + word.charAt(indexToReveal));

        for (int i = 0; i < revealCount; i++) {

            if (maskedWord.charAt(indexToReveal) == '_') {

                Log.d ("DEBUG", "Index to reveal from set Char " + indexToReveal);
                maskedWord.setCharAt(indexToReveal, word.charAt(indexToReveal));
                revealHintUI(context, wordContainer, word, editTextList, indexToReveal);
                missingIndexes.remove(indexToReveal);
                Log.d ("DEBUG", "Reveal hint is being triggered");
            }

            missingList.remove(ranIndex);
        }
    }
    public void revealHintUI(Context context, LinearLayout wordContainer, String word,
                             List<EditText> editTextList, int indexToReveal) {


        Log.d ("DEBUG", "Word from reveal hint UI " + word);
        TextView revealedLetter = new TextView(context);

        revealedLetter.setText(String.valueOf(word.charAt(indexToReveal)));
        revealedLetter.setPadding(5,5,5,5);
        revealedLetter.setTextSize(40);
        revealedLetter.setTextColor(ContextCompat.getColor(context, R.color.primaryBlue));

        revealedLetter.setGravity(Gravity.CENTER);
        int editTextIndex = -1;
        for (int i = 0; i < indexToReveal; i++) {
            EditText et = editTextList.get(i);

            if(wordContainer.indexOfChild(et) == indexToReveal) {
                editTextIndex = i;
                break;
            }
        }
        if (editTextIndex != -1) {

            EditText et =editTextList.get(editTextIndex);
            wordContainer.removeView(et);
            editTextList.remove(editTextIndex);
        }

        wordContainer.addView(revealedLetter, indexToReveal);

    }
    public static String generatedHeart(int count) {
        StringBuilder hearts = new StringBuilder();

        for (int i = 0; i < count; i++) {
            hearts.append("❤️");
        } return hearts.toString();
    }

    public void showNextWord(LinearLayout wordContainer, List<EditText> editTextList,TextView result) {

        wordContainer.removeAllViews();
        editTextList.clear();
        result.setText("");
    }

}
