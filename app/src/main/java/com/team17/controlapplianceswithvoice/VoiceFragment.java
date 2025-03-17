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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VoiceFragment extends Fragment {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private boolean isListening = false;
    String speechRecognize;

    RecyclerApplianceAdapter adapter;
    ApplianceDatabaseHelper dbHelper;
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
        dbHelper = new ApplianceDatabaseHelper(getContext());
        adapter = new RecyclerApplianceAdapter(getContext(), new DashboardFragment()::changeName);

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
                mic_status.setText("Listening...");
                pulse_ring.setVisibility(View.VISIBLE);
                pulse_ring.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pusle_animation));  // Start pulse animation
            }

            @Override
            public void onBeginningOfSpeech() { }

            @Override
            public void onRmsChanged(float rmsdB) { }

            @Override
            public void onBufferReceived(byte[] buffer) { }

            @Override
            public void onEndOfSpeech() {
                stopListening();
            }

            @Override
            public void onError(int error) {
                stopListening();
                if (speechRecognizer != null) {
                    speechRecognizer.destroy();
                }
                setupSpeechRecognizer();
            }

            @Override
            public void onResults(Bundle results) {
                // Get the results from speech recognition
                if (results != null) {
                    // Get the list of spoken words
                    ArrayList<String> recognizedWords = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (recognizedWords != null && !recognizedWords.isEmpty()) {
                        speechRecognize = recognizedWords.get(0);
                        execVoiceCommand(speechRecognize);
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

    private void execVoiceCommand(String speechRecognize) {

        Log.d("voiceResult", "Original: " + speechRecognize);

        // Convert number words to digits
        speechRecognize = convertNumbersToDigits(speechRecognize);
        Log.d("voiceResult", "Converted: " + speechRecognize);

        String[] speechArray = speechRecognize.split(" ");
        String firstWord = speechArray[0].toLowerCase();

        // Validate the first word (it should be "turn" or "switch")
        if (!firstWord.equals("turn") && !firstWord.equals("switch")) {
            showInvalidCommand();
            return;
        }

        // Ensure the command has enough words (e.g., "turn on light 1")
        if (speechArray.length < 3) {
            showInvalidCommand();
            return;
        }

        String action = speechArray[1].toLowerCase(); // "on" or "off"
        String applianceName = speechRecognize.substring(speechRecognize.indexOf(action) + action.length()).trim(); // Extract appliance name

        // Fetch appliances from the database
        ArrayList<ApplianceModel> arrayList = dbHelper.getAllAppliances();

        boolean applianceExists = false;
        int appliancePosition = -1; // Variable to store position

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getApplianceName().equalsIgnoreCase(applianceName)) {
                Log.d("voiceResult", "Name: " + arrayList.get(i).getApplianceName());
                applianceExists = true;
                appliancePosition = i; // Store the position of the appliance
                break;
            }
        }

        if (applianceExists) {
            updateApplianceStatus(appliancePosition, action, arrayList);
        } else {
            showInvalidCommand();
        }
    }

    // Function to handle invalid commands
    private void showInvalidCommand() {
        mic_button.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        mic_status.setText("Invalid Command");

        mic_button.postDelayed(() -> {
            mic_button.clearColorFilter();
            mic_status.setText("Tap to Start Listening");
        }, 3000);
    }

    // Function to update appliance status (to be implemented later)
    private void updateApplianceStatus(int position, String action, ArrayList<ApplianceModel> arrayList) {
        if((action.equals("on") && !arrayList.get(position).getStatus()) || (action.equals("off") && arrayList.get(position).getStatus())) {
            int applianceId = arrayList.get(position).getApplianceId();
            boolean newStatus = action.equals("on");

            // Update database
            dbHelper.toggleApplianceStatus(applianceId);

            // Send command via Bluetooth
            BluetoothManager bluetoothManager = BluetoothManager.getInstance(context);
            if (bluetoothManager.isConnected()) {
                // Format: "A1:ON" or "A1:OFF"
                String command = "A" + applianceId + ":" + (newStatus ? "ON" : "OFF");
                boolean commandSent = bluetoothManager.sendCommand(applianceId, newStatus);

                if (!commandSent) {
                    Toast.makeText(context, "Failed to send Bluetooth command", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Not connected to Bluetooth device", Toast.LENGTH_SHORT).show();
            }

            // Change icon color to green and set status text
            mic_button.setColorFilter(ContextCompat.getColor(context, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
            mic_status.setText("Command Sent");

            // Reset UI after 3 seconds
            mic_button.postDelayed(() -> {
                mic_button.clearColorFilter(); // Reset color
                mic_status.setText("Tap to Start Listening");
            }, 3000);
        } else {
            showInvalidCommand();
        }
    }



    private String convertNumbersToDigits(String input) {
        Map<String, String> numberMap = new HashMap<>();
        numberMap.put("one", "1");
        numberMap.put("two", "2");
        numberMap.put("three", "3");
        numberMap.put("four", "4");
        numberMap.put("five", "5");
        numberMap.put("six", "6");
        numberMap.put("seven", "7");
        numberMap.put("eight", "8");
        numberMap.put("nine", "9");
        numberMap.put("zero", "0");

        String[] words = input.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (numberMap.containsKey(word.toLowerCase())) {
                result.append(numberMap.get(word.toLowerCase())).append(" ");
            } else {
                result.append(word).append(" ");
            }
        }

        return result.toString().trim();
    }


    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }
}
