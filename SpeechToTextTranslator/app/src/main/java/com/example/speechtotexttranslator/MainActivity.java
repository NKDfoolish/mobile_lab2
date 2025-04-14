package com.example.speechtotexttranslator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SpeechRecognizer speechRecognizer;
    private TextView originalTextView, translatedTextView;
    private FloatingActionButton recordButton;
    private Spinner targetLanguageSpinner;
    private boolean isListening = false;
    private Translator translator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            initializeViews();
            setupLanguageSpinner();
            setupSpeechRecognizer();
        }
    }

    private void initializeViews() {
        originalTextView = findViewById(R.id.originalTextView);
        translatedTextView = findViewById(R.id.translatedTextView);
        recordButton = findViewById(R.id.recordButton);
        targetLanguageSpinner = findViewById(R.id.targetLanguageSpinner);

        recordButton.setOnClickListener(v -> toggleListening());
    }

    private void setupLanguageSpinner() {
        String[] languages = {"Spanish", "French", "German", "Italian"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetLanguageSpinner.setAdapter(adapter);
    }

    private void requestPermission() {
        ActivityResultLauncher<String> launcher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        initializeViews();
                        setupLanguageSpinner();
                        setupSpeechRecognizer();
                    } else {
                        Toast.makeText(this, "Microphone permission required", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
        launcher.launch(Manifest.permission.RECORD_AUDIO);
    }

    private void setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_LONG).show();
            return;
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                runOnUiThread(() -> {
                    originalTextView.setText("Listening...");
                    Log.d("Speech", "Ready for speech");
                });
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("Speech", "Speech beginning");
            }

            @Override
            public void onRmsChanged(float v) {
                // Update UI to show voice detection
                if (v > 1) {
                    Log.d("Speech", "Voice detected: " + v);
                }
            }

            @Override
            public void onBufferReceived(byte[] bytes) {}

            @Override
            public void onEndOfSpeech() {
                Log.d("Speech", "Speech ended");
            }

            @Override
            public void onError(int i) {
                String message;
                switch (i) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No speech detected";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "Speech service busy";
                        break;
                    default:
                        message = "Error code: " + i;
                }
                Log.e("Speech", "Error: " + message);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    originalTextView.setText(message);
                    isListening = false;
                    recordButton.setImageResource(android.R.drawable.ic_btn_speak_now);
                });
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0);
                    Log.d("Speech", "Result: " + text);
                    runOnUiThread(() -> {
                        originalTextView.setText(text);
                        translateText(text);
                    });
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0);
                    Log.d("Speech", "Partial: " + text);
                    runOnUiThread(() -> originalTextView.setText("Hearing: " + text));
                }
            }

            @Override
            public void onEvent(int i, Bundle bundle) {}
        });
    }

    private void toggleListening() {
        if (!isListening) {
            originalTextView.setText("");
            translatedTextView.setText("");

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);

            try {
                runOnUiThread(() -> {
                    speechRecognizer.startListening(intent);
                    isListening = true;
                    recordButton.setImageResource(android.R.drawable.ic_media_pause);
                    Toast.makeText(this, "Start speaking...", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.e("SpeechRecognizer", "Error: " + e.getMessage());
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                isListening = false;
            }
        } else {
            runOnUiThread(() -> {
                speechRecognizer.stopListening();
                isListening = false;
                recordButton.setImageResource(android.R.drawable.ic_btn_speak_now);
            });
        }
    }

    private void translateText(String text) {
        String targetLang = TranslateLanguage.SPANISH;
        switch (targetLanguageSpinner.getSelectedItem().toString()) {
            case "French": targetLang = TranslateLanguage.FRENCH; break;
            case "German": targetLang = TranslateLanguage.GERMAN; break;
            case "Italian": targetLang = TranslateLanguage.ITALIAN; break;
        }

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(targetLang)
                .build();

        translator = Translation.getClient(options);

        // Show loading message
        translatedTextView.setText("Translating...");

        translator.downloadModelIfNeeded()
                .addOnSuccessListener(unused -> {
                    translator.translate(text)
                            .addOnSuccessListener(translatedText -> {
                                translatedTextView.setText("Translated: " + translatedText);
                            })
                            .addOnFailureListener(e -> {
                                translatedTextView.setText("Translation failed");
                                Toast.makeText(MainActivity.this,
                                        "Translation error: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    translatedTextView.setText("Model download failed");
                    Toast.makeText(MainActivity.this,
                            "Model download error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
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