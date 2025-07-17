package com.minovative.guessify;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ShopGridViewAdapter extends ArrayAdapter<Shop> {

private OnBuyClicked listener;
    public ShopGridViewAdapter(Activity activity,List<Shop> shopList, OnBuyClicked listener) {
        super(activity,0, shopList);
        this.listener = listener;
    }


        static class ViewHolder {
            TextView puzzleQtt;
            Button buyBtn;
        }
    public interface OnBuyClicked {
        void onBuyClicked(Shop currentShop);
    }

        @NonNull
        @Override
        public View getView(int position,View convertView,@NonNull ViewGroup parent) {

             ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.shop_card_layout,parent,false);

                holder = new ViewHolder();
                holder.puzzleQtt = convertView.findViewById(R.id.puzzleQtt);
                holder.buyBtn = convertView.findViewById(R.id.buyBtn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Shop currentShop = getItem(position);

            if(currentShop != null) {
                holder.puzzleQtt.setText(currentShop.getPuzzleQtt() + " 🧩");
                holder.buyBtn.setText("BUY WITH " + currentShop.getStarRequired() + " ⭐");
            }

            holder.buyBtn.setOnClickListener(view -> {
                listener.onBuyClicked(currentShop);
            });

            return convertView;
        }
    }

