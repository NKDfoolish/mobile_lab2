package com.example.ex2_3;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.content.Context;

public class MainActivity extends AppCompatActivity {
    private EditText inputSentence;
    private TextView resultText;
    private Module model;
    private Tokenizer tokenizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputSentence = findViewById(R.id.inputSentence);
        resultText = findViewById(R.id.resultText);

        try {
            initializeModel();
        } catch (IOException e) {
            String error = "Error initializing: " + e.getMessage();
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            resultText.setText(error);
            e.printStackTrace();
        }
    }

    private void initializeModel() throws IOException {
        // First check if vocab.json exists
        if (!Arrays.asList(getAssets().list("")).contains("vocab.json")) {
            throw new IOException("vocab.json not found in assets");
        }

        // Initialize tokenizer first
        tokenizer = new Tokenizer(this);

        // Then check if model file exists
        if (!Arrays.asList(getAssets().list("")).contains("phobert_sentiment.pt")) {
            throw new IOException("phobert_sentiment.pt not found in assets");
        }

        // Load model
        model = Module.load(assetFilePath(this, "phobert_sentiment.pt"));
    }

    public void analyzeSentiment(View view) {
        if (tokenizer == null || model == null) {
            Toast.makeText(this, "Model not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        String sentence = inputSentence.getText().toString().trim();
        if (sentence.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập câu!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    // Tokenize input
                    long[] tokens = tokenizer.encode(params[0]);
                    Tensor inputTensor = Tensor.fromBlob(tokens, new long[]{1, tokens.length});

                    // Run inference
                    IValue[] outputTuple = model.forward(IValue.from(inputTensor)).toTuple();
                    float[] scores = outputTuple[0].toTensor().getDataAsFloatArray();

                    // Get prediction
                    int prediction = argmax(scores);
                    String[] labels = {"Tiêu cực", "Tích cực", "Trung lập"};
                    return labels[prediction];
                } catch (Exception e) {
                    return "Lỗi: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                resultText.setText("Cảm xúc: " + result);
                if (result.equals("Tích cực")) {
                    resultText.setTextColor(Color.GREEN);
                } else if (result.equals("Tiêu cực")) {
                    resultText.setTextColor(Color.RED);
                } else {
                    resultText.setTextColor(Color.GRAY);
                }
            }
        }.execute(sentence);
    }

    private int argmax(float[] array) {
        int maxIndex = 0;
        float maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
}