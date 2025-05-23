package com.minovative.guessify;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GuessingWord {
    private String originalWord;
    private Set<Integer> missingIndexes;
    private StringBuilder maskedWord;
    private Map<Integer,EditText> indexToEditTextMap = new HashMap<>();

    public GuessingWord(String originalWord) {
        this.originalWord = originalWord;
        this.missingIndexes = new HashSet<>();
    }

    public String getOriginalWord( ) {
        return originalWord;
    }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }

    public interface AnswerCallBack {
        void onCorrect( );

        void onIncorrect( );
    }

    public String generateMaskedWord( ) {

        missingIndexes.clear();

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
        }
        return maskedWord.toString();
    }

    public boolean isCorrect(List<String> userInput) {

        StringBuilder userGuess = new StringBuilder();
        int inputIndex = 0;

        for (int i = 0; i < originalWord.length(); i++) {

            if (missingIndexes.contains(i)) {

                userGuess.append(userInput.get(inputIndex));
                inputIndex++;

            } else {

                userGuess.append(originalWord.charAt(i));
            }
        }
        return userGuess.toString().toLowerCase().equals(originalWord);
    }

    public void showGuessUI(GuessingWord word, Word currentWord, Context context,
                            List<EditText> editTextList, TextView hintTextView,
                            LinearLayout wordContainer, Button check, AnswerCallBack callBack) {

        String masked = word.generateMaskedWord();

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
                indexToEditTextMap.put(i,editText);
                editTextList.add(editText);
                wordContainer.addView(editText);

                int currentIndex = editTextList.size() - 1;

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence,int i,int i1,int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence,int i,int i1,int i2) {

                        if (charSequence.length() == 1) {

                            if (currentIndex + 1 < editTextList.size()) {

                                editTextList.get(currentIndex + 1).requestFocus();
                            }
                        }

                        editText.setOnKeyListener((v,keyCode,event) -> {

                            if (keyCode == KeyEvent.KEYCODE_DEL && editText.getText().length() == 0) {

                                if (currentIndex - 1 >= 0) {

                                    editTextList.get(currentIndex - 1).requestFocus();

                                }
                            }
                            return false;
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
                wordContainer.addView(textView);
            }
        }

        check.setOnClickListener(view -> {

            List<String> userInput = new ArrayList<>();
            List<Integer> sortedIndexes = new ArrayList<>(indexToEditTextMap.keySet());
            Collections.sort(sortedIndexes);

            for (int index : sortedIndexes) {

                EditText et = indexToEditTextMap.get(index);
                if (et == null) throw new IllegalStateException();
                userInput.add(et.getText().toString().toLowerCase());
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

    public boolean getWordHint(int numToReveal, String word, Context context,
                               LinearLayout wordContainer) {

        List<Integer> missingList = new ArrayList<>(missingIndexes);
        Collections.shuffle(missingList);

        int revealCount = Math.min(numToReveal,missingList.size());
        boolean revealed = false;

        for (int i = 0; i < revealCount; i++) {

            if (missingList.isEmpty()) {

                return revealed;
            }

            int indexToReveal = missingList.get(i);

            if (maskedWord.charAt(indexToReveal) == '_') {

                maskedWord.setCharAt(indexToReveal,word.charAt(indexToReveal));
                revealHintUI(context,wordContainer,word,indexToReveal);
                missingIndexes.remove(indexToReveal);
                revealed = true;
            }
        }
        return revealed;
    }

    public void revealHintUI(Context context, LinearLayout wordContainer, String word, int indexToReveal) {

        EditText et = indexToEditTextMap.get(indexToReveal);
        TextView revealedLetter = new TextView(context);

        revealedLetter.setText(String.valueOf(word.charAt(indexToReveal)));
        revealedLetter.setPadding(5,5,5,5);
        revealedLetter.setTextSize(40);
        revealedLetter.setTextColor(ContextCompat.getColor(context,R.color.primaryBlue));
        revealedLetter.setGravity(Gravity.CENTER);

        if (et != null) {

            wordContainer.removeView(et);
            indexToEditTextMap.remove(indexToReveal);
        }
        wordContainer.addView(revealedLetter,indexToReveal);
    }

    public static String generateHeart(int count) {
        StringBuilder hearts = new StringBuilder();

        for (int i = 0; i < count; i++) {
            hearts.append("❤️");
        }
        return hearts.toString();
    }

    public void showNextWord(LinearLayout wordContainer, List<EditText> editTextList, TextView result) {

        wordContainer.removeAllViews();
        editTextList.clear();
        result.setText("");
    }
}
