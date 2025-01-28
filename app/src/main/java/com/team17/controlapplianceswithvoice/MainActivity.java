package com.team17.controlapplianceswithvoice;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

        adapter = new RecyclerApplianceAdapter(this, new DashboardFragment()::changeName);
        dbHelper = new ApplianceDatabaseHelper(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }

        // Set listener to handle bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // Use if-else statements for item selection
                if (item.getItemId() == R.id.menu_dashboard) {
                    selectedFragment = new DashboardFragment();
                } else if (item.getItemId() == R.id.menu_voice_command) {
                    selectedFragment = new VoiceFragment();
                } else if (item.getItemId() == R.id.menu_logs) {
                    selectedFragment = new ConsoleFragment();
                } else if (item.getItemId() == R.id.menu_settings) {
                    selectedFragment = new SettingsFragment();
                }

                // Load the selected fragment
                return loadFragment(selectedFragment);
            }
        });
    }

    // Helper method to load fragment dynamically
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(bottomNavigationView.getSelectedItemId() != R.id.menu_dashboard){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new DashboardFragment());
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.menu_dashboard);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    //refresh button implementation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.refresh_button) {
            adapter.arrayList = dbHelper.getAllAppliances();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Refresh clicked", Toast.LENGTH_SHORT).show();
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
}