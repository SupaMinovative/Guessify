package com.minovative.guessify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import org.w3c.dom.Text;

import java.util.List;

public class GridViewAdapter extends ArrayAdapter<Level> {
    private TextView levelTextView;
    private TextView difficultyTextView;
    Button unlockButton;
    ImageView lockButton;

    public GridViewAdapter(Context context, List<Level> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_level_layout, parent, false);

            CardView cardView = convertView.findViewById(R.id.cardView);

            levelTextView = convertView.findViewById(R.id.levelTextView);
            difficultyTextView = convertView.findViewById(R.id.difficultyTextView);
            unlockButton = convertView.findViewById(R.id.unlockButton);
            lockButton = convertView.findViewById(R.id.lockButton);

            Level level = getItem(position);

            if (levelTextView != null) {

                levelTextView.setText(level.getLevel());
            }

            if (difficultyTextView != null) {

                difficultyTextView.setText(level.getDifficulty());
            }
            if (unlockButton != null) {

                unlockButton.setText("Unlock with " + level.getStarRequired());
            }
            if (lockButton != null) {

                lockButton.setOnClickListener(View -> {
                    Intent intent = new Intent(GridViewAdapter.this.getContext(), Level1Activity.class);
                    getContext().startActivity(intent);
                });
            }
        } return convertView;
    }

}
