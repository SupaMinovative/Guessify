package com.minovative.guessify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameDisplayAdapter extends RecyclerView.Adapter<GameDisplayAdapter.GameDisplayViewHolder> {

    private List<Word> wordList;
    private RecyclerView recyclerView;

    public GameDisplayAdapter(List<Word> wordList, RecyclerView recyclerView) {
        this.wordList = wordList;
        this.recyclerView = recyclerView;

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

        Word currentWord = wordList.get(position);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class GameDisplayViewHolder extends RecyclerView.ViewHolder {


        public GameDisplayViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
