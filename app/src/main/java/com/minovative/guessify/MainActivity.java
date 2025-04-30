package com.minovative.guessify;

import android.os.Bundle;
import android.widget.GridView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(2);

        List<Level> levelList = new ArrayList<>();
        levelList.add(new Level("Level 1", "easy", 0));
        levelList.add(new Level("Level 2", "easy", 10));
        levelList.add(new Level("Level 3", "easy", 20));

        GridViewAdapter adapter = new GridViewAdapter(this, levelList);
        gridView.setAdapter(adapter);
    }
}