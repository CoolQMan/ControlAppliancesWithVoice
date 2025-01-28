package com.team17.controlapplianceswithvoice;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    ApplianceDatabaseHelper dbHelper;
    RecyclerApplianceAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


    //TODO: Finish this

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.refresh_button){
            Toast.makeText(this, "Refreshed status of all appliances", Toast.LENGTH_SHORT).show();
            adapter.arrayList = dbHelper.getAllAppliances();
            adapter.notifyDataSetChanged();
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }
}