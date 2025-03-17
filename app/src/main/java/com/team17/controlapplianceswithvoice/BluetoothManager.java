package com.team17.controlapplianceswithvoice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {
    private static final String TAG = "BluetoothManager";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SPP UUID

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice hc05Device;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean isConnected = false;

    // Singleton instance
    private static BluetoothManager instance;

    public static synchronized BluetoothManager getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothManager(context);
        }
        return instance;
    }

    private BluetoothManager(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isBluetoothSupported() {
        return bluetoothAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public boolean isConnected() {
        return isConnected;
    }

    public Set<BluetoothDevice> getPairedDevices() {
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.getBondedDevices();
        }
        return null;
    }

    public boolean connectToDevice(String deviceAddress) {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth not supported");
            return false;
        }

        // Close existing connection if any
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing existing socket", e);
            }
        }

        try {
            hc05Device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            bluetoothSocket = hc05Device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            isConnected = true;
            Log.d(TAG, "Connected to " + deviceAddress);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to connect", e);
            isConnected = false;
            return false;
        }
    }

    public boolean sendCommand(int applianceId, boolean turnOn) {
        if (!isConnected || outputStream == null) {
            Log.e(TAG, "Not connected or output stream is null");
            return false;
        }

        try {
            // Format: "A1:ON" or "A1:OFF" where 1 is the appliance ID
            String command = "A" + applianceId + ":" + (turnOn ? "ON" : "OFF");
            outputStream.write(command.getBytes());
            Log.d(TAG, "Sent command: " + command);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error sending command", e);
            isConnected = false;
            return false;
        }
    }

    public void disconnect() {
        isConnected = false;
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing socket", e);
        }
    }
}
