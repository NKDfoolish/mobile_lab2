package com.example.spetotext;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 100;
    private SpeechRecognizer speechRecognizer;
    private TextView originalText, translatedText;
    private FloatingActionButton recordButton;
    private Spinner languageSpinner;
    private boolean isListening = false;
    private Translator translator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        originalText = findViewById(R.id.originalText);
        translatedText = findViewById(R.id.translatedText);
        recordButton = findViewById(R.id.recordButton);
        languageSpinner = findViewById(R.id.languageSpinner);

        setupLanguageSpinner();
        checkPermission();
        setupSpeechRecognizer();

        recordButton.setOnClickListener(v -> toggleListening());
    }

    private void setupLanguageSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Spanish", "French", "German"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_CODE);
        } else {
            // Permission already granted, initialize speech recognizer
            initializeSpeechRecognizer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSpeechRecognizer();
            } else {
                originalText.setText("Permission Denied");
            }
        }
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        setupSpeechRecognizer();
    }

    private void setupSpeechRecognizer() {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                originalText.setText("Listening...");
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                isListening = false;
                recordButton.setImageResource(android.R.drawable.ic_btn_speak_now);
            }

            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No speech input";
                        break;
                    default:
                        message = "Error code: " + error;
                }
                originalText.setText(message);
                isListening = false;
                recordButton.setImageResource(android.R.drawable.ic_btn_speak_now);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0);
                    originalText.setText(text);
                    translateText(text);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

            // Keep other empty methods
        });
    }

    private void toggleListening() {
        if (!isListening) {
            if (!SpeechRecognizer.isRecognitionAvailable(this)) {
                originalText.setText("Speech recognition not available");
                return;
            }

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            try {
                speechRecognizer.startListening(intent);
                isListening = true;
                recordButton.setImageResource(android.R.drawable.ic_media_pause);
                originalText.setText("Listening...");
            } catch (Exception e) {
                originalText.setText("Error: " + e.getMessage());
                isListening = false;
            }
        } else {
            speechRecognizer.stopListening();
            isListening = false;
            recordButton.setImageResource(android.R.drawable.ic_btn_speak_now);
        }
    }

    private void translateText(String text) {
        translatedText.setText("Translating...");
        String targetLanguage = getTargetLanguageCode();
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(targetLanguage)
                .build();
        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded()
                .addOnSuccessListener(unused -> {
                    translator.translate(text)
                            .addOnSuccessListener(translatedText::setText)
                            .addOnFailureListener(e -> {
                                translatedText.setText("Translation failed: " + e.getLocalizedMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    translatedText.setText("Model download failed: " + e.getLocalizedMessage());
                });
    }

    private String getTargetLanguageCode() {
        switch (languageSpinner.getSelectedItemPosition()) {
            case 0: return TranslateLanguage.SPANISH;
            case 1: return TranslateLanguage.FRENCH;
            case 2: return TranslateLanguage.GERMAN;
            default: return TranslateLanguage.SPANISH;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (translator != null) {
            translator.close();
        }
    }


}