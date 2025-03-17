package com.team17.controlapplianceswithvoice;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Set;

public class SettingsFragment extends Fragment {

    private Button btnPair, btnDeleteAll, btnDarkMode;
    private ApplianceDatabaseHelper dbHelper;
    private BluetoothManager bluetoothManager;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        dbHelper = new ApplianceDatabaseHelper(getContext());
        bluetoothManager = BluetoothManager.getInstance(getContext());
        sharedPreferences = getActivity().getSharedPreferences("bluetooth_prefs", Context.MODE_PRIVATE);

        btnPair = view.findViewById(R.id.btn_pair);
        btnDeleteAll = view.findViewById(R.id.btn_delete_all);
        btnDarkMode = view.findViewById(R.id.btn_dark_mode);

        btnPair.setOnClickListener(v -> showPairedDevices());
        btnDeleteAll.setOnClickListener(v -> showDeleteConfirmation());
        btnDarkMode.setOnClickListener(v -> toggleDarkMode());

        // Update button text based on connection status
        updateBluetoothButtonText();

        return view;
    }

    private void updateBluetoothButtonText() {
        String deviceName = sharedPreferences.getString("device_name", null);
        if (deviceName != null) {
            btnPair.setText("Connected: " + deviceName);
        } else {
            btnPair.setText("Connect to Bluetooth Device");
        }
    }

    private void showPairedDevices() {
        if (!bluetoothManager.isBluetoothSupported()) {
            Toast.makeText(getContext(), "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothManager.isBluetoothEnabled()) {
            Toast.makeText(getContext(), "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothManager.getPairedDevices();
        if (pairedDevices == null || pairedDevices.isEmpty()) {
            Toast.makeText(getContext(), "No paired devices found. Please pair your HC-05 in Bluetooth settings first", Toast.LENGTH_LONG).show();
            return;
        }

        // Create list of device names and addresses
        final ArrayList<String> deviceNames = new ArrayList<>();
        final ArrayList<String> deviceAddresses = new ArrayList<>();

        for (BluetoothDevice device : pairedDevices) {
            deviceNames.add(device.getName() + "\n" + device.getAddress());
            deviceAddresses.add(device.getAddress());
        }

        // Create dialog with list of paired devices
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select HC-05 Device");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, deviceNames);
        builder.setAdapter(adapter, (dialog, which) -> {
            String deviceAddress = deviceAddresses.get(which);
            String deviceName = deviceNames.get(which);
            connectToDevice(deviceAddress, deviceName);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void connectToDevice(String deviceAddress, String deviceName) {
        if (bluetoothManager.connectToDevice(deviceAddress)) {
            Toast.makeText(getContext(), "Connected to " + deviceName, Toast.LENGTH_SHORT).show();

            // Save the connected device info
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("device_address", deviceAddress);
            editor.putString("device_name", deviceName);
            editor.apply();

            updateBluetoothButtonText();
        } else {
            Toast.makeText(getContext(), "Failed to connect to " + deviceName, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete All Appliances")
                .setMessage("Are you sure you want to delete all appliances?")
                .setPositiveButton("Yes", (dialog, which) -> showFinalWarning())
                .setNegativeButton("No", null)
                .show();
    }

    private void showFinalWarning() {
        new AlertDialog.Builder(getContext())
                .setTitle("Final Warning")
                .setMessage("This action is irreversible. Do you really want to proceed?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteAllAppliances();
                    Toast.makeText(getContext(), "All appliances deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void toggleDarkMode() {
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            SharedPreferences pref = getActivity().getSharedPreferences("darkMode", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("darkModeToggle", false);
            editor.apply();

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            SharedPreferences pref = getActivity().getSharedPreferences("darkMode", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("darkModeToggle", true);
            editor.apply();
        }
    }
}
