package com.minovative.guessify;


import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {
    private GridView shopGridView;
    private ShopGridViewAdapter adapter;
    private List<Shop> shopList = new ArrayList<>();
    private Button backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);

        AppDatabase db = AppDatabase.getInstance(this);
        GameStateDao gameStateDao = db.gameStateDao();
        backBtn = findViewById(R.id.backBtnShop);
        shopGridView = findViewById(R.id.shopGridView);
        shopGridView.setNumColumns(3);

        GameState gameState = new GameState();
        int totalStar = getIntent().getIntExtra("CURRENT_STAR", 0);
        int totalHelpItem = getIntent().getIntExtra("CURRENT_HELP_ITEM", 0);
        backBtn.setOnClickListener(view -> {
            this.finish();
        });
        GameStateViewModel gameStateViewModel = new ViewModelProvider(this,
                new GameStateViewModelFactory(getApplication(), gameStateDao))
                .get(GameStateViewModel.class);
        shopList.add(new Shop(5,1));
        shopList.add(new Shop(15,3));
        shopList.add(new Shop(30,8));
        shopList.add(new Shop(50,15));
        shopList.add(new Shop(65,20));
        shopList.add(new Shop(80,25));

        adapter = new ShopGridViewAdapter(this,shopList, item -> {
            if(totalStar <= item.getStarRequired()) {
                MethodHelper.showDialog(this,"You don't have enough ⭐!");
            } else {
                    gameState.setStarCount(totalStar - item.getStarRequired());
                    gameState.setHelpItem(item.getPuzzleQtt()+totalHelpItem);
                    gameStateViewModel.updateStarQtt(gameState);
                    gameStateViewModel.updateHelpItemQtt(gameState);
                    MethodHelper.showDialog(this,"You received " + item.getPuzzleQtt() + " 🧩");}

        });
        shopGridView.setAdapter(adapter);
    }


}