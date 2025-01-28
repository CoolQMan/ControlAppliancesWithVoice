package com.team17.controlapplianceswithvoice;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class VoiceFragment extends Fragment {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private boolean isListening = false;

    Context context;
    ImageButton mic_button;
    TextView mic_status;
    View pulse_ring;

    public VoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_voice, container, false);
        requestPermission();
        context = getContext();

        mic_button = v.findViewById(R.id.mic_button);
        mic_status = v.findViewById(R.id.mic_status);
        pulse_ring = v.findViewById(R.id.pulse_ring);

        // Setup the pulse animation
        pulse_ring.setVisibility(View.INVISIBLE);

        // Initialize SpeechRecognizer
        setupSpeechRecognizer();

        mic_button.setOnClickListener(v1 -> {
            if (isListening) {
                // Stop listening and stop animation
                stopListening();
            } else {
                // Start listening and start animation
                startListening();
            }
        });

        return v;
    }

    private void setupSpeechRecognizer() {
        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("voiceResult", "Ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("voiceResult", "Speech beginning");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // This can be used to change the pulse size based on voice volume (optional)
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Not needed for this implementation
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("voiceResult", "End of speech");
                stopListening();
            }

            @Override
            public void onError(int error) {
//                Log.d("voiceResult", "Error occurred: " + error);
                stopListening();
            }

            @Override
            public void onResults(Bundle results) {
                // Get the results from speech recognition
                if (results != null) {
                    // Get the list of spoken words
                    ArrayList<String> recognizedWords = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (recognizedWords != null && !recognizedWords.isEmpty()) {
                        String recognizedSpeech = recognizedWords.get(0);
                        Log.d("voiceResult", "Recognized Speech: " + recognizedSpeech);
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // You can use this for partial recognition (optional)
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Not needed for this implementation
            }
        });

        // Prepare the recognizer intent
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
    }

    private void startListening() {
        if (hasPermission()) {
            mic_status.setText("Listening...");
            pulse_ring.setVisibility(View.VISIBLE);
            pulse_ring.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pusle_animation));  // Start pulse animation

            isListening = true;
            speechRecognizer.startListening(recognizerIntent);
        } else {
            Toast.makeText(context, "Permission to record audio is required", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopListening() {
        mic_status.setText("Stopped");
        pulse_ring.setVisibility(View.INVISIBLE);
        pulse_ring.clearAnimation();  // Stop pulse animation

        isListening = false;
        speechRecognizer.stopListening();
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    public void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }
}
