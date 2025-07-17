package com.minovative.guessify;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;

public class MethodHelper {

    /**
     * Shows a centered dialog message that auto-dismisses after 1.5 seconds.
     */

    public static void showDialog(Context context,String message) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage(message)
                .create();

        dialog.setOnShowListener(d -> {

            TextView messageView = dialog.findViewById(android.R.id.message);

            if (messageView != null) {

                messageView.setGravity(Gravity.CENTER);
                messageView.setWidth(250);
                messageView.setTextSize(20);
            }

            new android.os.Handler().postDelayed(dialog::dismiss,1500);
        });
        dialog.show();
    }

    /**
     * Shows star count on game summary
     */

    public static String generateStars(int count) {

        StringBuilder stars = new StringBuilder();

        for (int i = 0; i < count; i++) {
            stars.append("⭐");
        }

        if (count < 3) {

            for (int i = count; i < 3; i++) {
                stars.append("✰");
            }
        }
        return stars.toString();
    }

    public static void shakeButton(View view) {
        Animation shake = new TranslateAnimation(0,0,0,5);
        shake.setDuration(4000);

        shake.setInterpolator(new CycleInterpolator(5));
        shake.setRepeatMode(Animation.RESTART);
        shake.setRepeatCount(Animation.INFINITE);
        view.startAnimation(shake);
    };

    public static void moveGameOver(ImageView view) {
        Animation move = new TranslateAnimation(0,0,200,-200);
        move.setDuration(10000);

        move.setInterpolator(new CycleInterpolator(1));
        move.setRepeatMode(Animation.RESTART);
        move.setRepeatCount(Animation.INFINITE);
        view.startAnimation(move);
    }

    public static void onBackBtnPressed(Context context, LifecycleOwner lifecycleOwner,OnBackPressedDispatcher dispatcher){
        dispatcher.addCallback(lifecycleOwner,new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed( ) {
            showExistConfirmation(context, lifecycleOwner, dispatcher);
        }
    });
    }
    private static void showExistConfirmation(Context context,LifecycleOwner lifecycleOwner,OnBackPressedDispatcher dispatcher) {
        new AlertDialog.Builder(context)
                .setTitle("Exist?")
                .setMessage("Are you sure you want to exit?")

                .setNegativeButton("No",null)
                .setPositiveButton("Yes",(dialog,which) -> {
                    onBackBtnPressed(context, lifecycleOwner, dispatcher);
                    ((Activity) context).finish();
                })
                .show();
    }
}
