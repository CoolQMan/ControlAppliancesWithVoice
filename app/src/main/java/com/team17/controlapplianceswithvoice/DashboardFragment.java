package com.team17.controlapplianceswithvoice;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class DashboardFragment extends Fragment {
    RecyclerView recyclerView;
    Context context;
    ApplianceDatabaseHelper dbHelper;
    RecyclerApplianceAdapter adapter;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        context = getContext();

        dbHelper = new ApplianceDatabaseHelper(context);
        adapter = new RecyclerApplianceAdapter(context, this::changeName);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void changeName(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.dialog_rename_appliance, null);
        builder.setView(customView); // Set the custom layout

        // Get references to the views in the custom layout
        EditText etApplianceID = customView.findViewById(R.id.et_appliance_id);
        EditText etApplianceName = customView.findViewById(R.id.et_appliance_name);
        Button btn_cancel = customView.findViewById(R.id.btn_cancel);
        Button btn_ok = customView.findViewById(R.id.btn_ok);
        Button btn_delete = customView.findViewById(R.id.btn_delete);

        etApplianceID.setVisibility(View.GONE);
        btn_delete.setVisibility(View.VISIBLE);

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
                if(etApplianceName.getText().toString().trim().isEmpty()){
                    btn_ok.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else{
                    btn_ok.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
        // Set up the OK button
        btn_ok.setOnClickListener(v -> {
            String newName = etApplianceName.getText().toString().trim();
            if (!newName.isEmpty()) {
                int id = dbHelper.getAllAppliances().get(position).getApplianceId();
                if(dbHelper.editApplianceName(id, newName) == -1){
                    Toast.makeText(context, "Appliance with same name already exists", Toast.LENGTH_SHORT).show();
                } else{
                    adapter.arrayList = dbHelper.getAllAppliances();
                    adapter.notifyItemChanged(position); // Refresh the item in RecyclerView
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        // Set up the Cancel button
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.removeAppliance(dbHelper.getAllAppliances().get(position).getApplianceId());
                adapter.arrayList = dbHelper.getAllAppliances();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    public void loadFromDatabase(){
        adapter.arrayList = dbHelper.getAllAppliances();
        adapter.notifyDataSetChanged();
    }

}