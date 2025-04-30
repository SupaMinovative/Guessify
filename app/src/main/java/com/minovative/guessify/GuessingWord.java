package com.minovative.guessify;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GuessingWord {
    private String originalWord;
    Set<Integer> missingIndexes;

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

    public String maskedOriginalWord() {

        //indexes size to be missing
        int numMissing = originalWord.length()/2;

        Random random = new Random();

        while (missingIndexes.size() < numMissing) {
            int index = random.nextInt(originalWord.length());
            missingIndexes.add(index);
        }

        StringBuilder maskedWord = new StringBuilder();

        for (int i = 0; i < originalWord.length(); i++) {

            if (missingIndexes.contains(i)) {
                maskedWord.append('_');

            } else {
                maskedWord.append(i);
            }

        } return maskedWord.toString();
    }

    public boolean isCorrect(List<String> userInput) {

        StringBuilder userGuess = new StringBuilder();
        int inputIndex = 0;

        for (int i = 0; i < originalWord.length(); i++) {

            if (missingIndexes.contains(i)) {
                userGuess.append(userInput.get(i));
                inputIndex++;
            } else {

                userGuess.append(originalWord.charAt(i));
            }
        } return userGuess.toString().equals(originalWord);
    }
}
