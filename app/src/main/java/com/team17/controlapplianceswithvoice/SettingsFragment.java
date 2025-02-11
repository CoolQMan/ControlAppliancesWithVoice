package com.team17.controlapplianceswithvoice;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    private Button btnPair, btnDeleteAll, btnDarkMode;
    private ApplianceDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        dbHelper = new ApplianceDatabaseHelper(getContext());

        btnPair = view.findViewById(R.id.btn_pair);
        btnDeleteAll = view.findViewById(R.id.btn_delete_all);
        btnDarkMode = view.findViewById(R.id.btn_dark_mode);

        btnDeleteAll.setOnClickListener(v -> showDeleteConfirmation());
        btnDarkMode.setOnClickListener(v -> toggleDarkMode());

        return view;
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
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}
