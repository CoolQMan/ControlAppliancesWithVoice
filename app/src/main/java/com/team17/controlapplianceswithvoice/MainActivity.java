package com.team17.controlapplianceswithvoice;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private BottomNavigationView bottomNavigationView;
    ApplianceDatabaseHelper dbHelper;
    RecyclerApplianceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestPermission();

        SharedPreferences pref = getSharedPreferences("darkMode", MODE_PRIVATE);
        boolean check = pref.getBoolean("darkModeToggle", false);

        if (check) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        adapter = new RecyclerApplianceAdapter(this, new DashboardFragment()::changeName);
        dbHelper = new ApplianceDatabaseHelper(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }

        // Set listener to handle bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Use if-else statements for item selection
            if (item.getItemId() == R.id.menu_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (item.getItemId() == R.id.menu_voice_command) {
                selectedFragment = new VoiceFragment();
            } else if (item.getItemId() == R.id.menu_settings) {
                selectedFragment = new SettingsFragment();
            }

            // Load the selected fragment
            return loadFragment(selectedFragment);
        });
        BluetoothManager bluetoothManager = BluetoothManager.getInstance(this);

        // Reconnect to last used device if available
        SharedPreferences sharedPreferences = getSharedPreferences("bluetooth_prefs", MODE_PRIVATE);
        String deviceAddress = sharedPreferences.getString("device_address", null);
        if (deviceAddress != null && bluetoothManager.isBluetoothEnabled()) {
            bluetoothManager.connectToDevice(deviceAddress);
        }
    }

    // Helper method to load fragment dynamically
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
            invalidateOptionsMenu(); // Refresh the menu
            return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() != R.id.menu_dashboard) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new DashboardFragment());
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.menu_dashboard);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        // Get current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        // Hide/show menu items based on current fragment
        if (currentFragment instanceof DashboardFragment) {
            menu.findItem(R.id.refresh_button).setVisible(true);
            menu.findItem(R.id.add_button).setVisible(true);
            menu.findItem(R.id.bluetooth_status).setVisible(true);
        } else {
            menu.findItem(R.id.refresh_button).setVisible(false);
            menu.findItem(R.id.add_button).setVisible(false);
            menu.findItem(R.id.bluetooth_status).setVisible(true);
        }

        return true;
    }


    //App bar buttons implementation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        DashboardFragment dashboardFragment = (DashboardFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (item.getItemId() == R.id.refresh_button) {
            dashboardFragment.loadFromDatabase();
            Toast.makeText(this, "Refresh clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.add_button) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View customView = inflater.inflate(R.layout.dialog_rename_appliance, null);
            builder.setView(customView); // Set the custom layout

            // Get references to the views in the custom layout
            TextView tv_title = customView.findViewById(R.id.tv_title);
            EditText etApplianceID = customView.findViewById(R.id.et_appliance_id);
            EditText etApplianceName = customView.findViewById(R.id.et_appliance_name);
            Button btn_cancel = customView.findViewById(R.id.btn_cancel);
            Button btn_ok = customView.findViewById(R.id.btn_ok);

            etApplianceID.setVisibility(View.VISIBLE);
            tv_title.setText("Add Appliance");

            AlertDialog dialog = builder.create();
            dialog.show();

            etApplianceName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (etApplianceName.getText().toString().trim().isEmpty() ||
                            etApplianceID.getText().toString().trim().isEmpty()) {
                        btn_ok.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        btn_ok.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
            });

            etApplianceID.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (etApplianceName.getText().toString().trim().isEmpty() ||
                            etApplianceID.getText().toString().trim().isEmpty()) {
                        btn_ok.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        btn_ok.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
            });

            btn_ok.setOnClickListener(v -> {
                String newName = etApplianceName.getText().toString().trim();
                int newID = Integer.parseInt(etApplianceID.getText().toString().trim());
                if (!newName.isEmpty()) {
                    if (!dbHelper.applianceExists(newName) && !dbHelper.applianceExists(newID)) {
                        dbHelper.addAppliance(new ApplianceModel(newID, newName, false));
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Name or ID already exists in database", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Name or ID cannot be empty", Toast.LENGTH_SHORT).show();
                }
                dashboardFragment.loadFromDatabase();
            });

            btn_cancel.setOnClickListener(v -> dialog.dismiss());

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect Bluetooth when app is closed
        BluetoothManager bluetoothManager = BluetoothManager.getInstance(this);
        bluetoothManager.disconnect();
    }
    public void invalidateOptionsMenu() {
        if (getSupportActionBar() != null) {
            supportInvalidateOptionsMenu();
        }
    }

}