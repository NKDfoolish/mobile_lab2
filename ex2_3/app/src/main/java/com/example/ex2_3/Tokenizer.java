package com.example.ex2_3;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tokenizer {
    private static final int PAD_TOKEN = 1;
    private static final int UNK_TOKEN = 0;
    private static final int MAX_LENGTH = 256;
    private Map<String, Integer> vocab;

    public Tokenizer(Context context) throws IOException {
        // Load vocabulary from assets
        AssetManager assetManager = context.getAssets();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(assetManager.open("vocab.json")))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
            vocab = new Gson().fromJson(json.toString(), type);
        }
    }

    public long[] encode(String text) {
        // Split text into words (assuming text is already word-segmented)
        String[] words = text.split("\\s+");
        List<Integer> tokens = new ArrayList<>();

        // Convert words to tokens
        for (String word : words) {
            Integer token = vocab.getOrDefault(word, UNK_TOKEN);
            tokens.add(token);
        }

        // Pad or truncate to MAX_LENGTH
        while (tokens.size() < MAX_LENGTH) {
            tokens.add(PAD_TOKEN);
        }
        if (tokens.size() > MAX_LENGTH) {
            tokens = tokens.subList(0, MAX_LENGTH);
        }

        // Convert to long array
        long[] result = new long[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            result[i] = tokens.get(i);
        }
        return result;
    }
}