package com.minovative.guessify;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class JsonUtils {

    public static final String JSON_FILE_NAME = "game_words.json";

    public static String AssetsJsonFile(Context context) throws IOException {

        try (InputStream is = context.getAssets().open(JSON_FILE_NAME)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer);
            return json;
        }
    }
}
