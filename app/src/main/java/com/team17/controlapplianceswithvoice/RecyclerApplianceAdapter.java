package com.team17.controlapplianceswithvoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;

public class RecyclerApplianceAdapter extends RecyclerView.Adapter<RecyclerApplianceAdapter.ViewHolder> {
    Context context;
    private OnItemLongClickListener longClickListener;
    ApplianceDatabaseHelper databaseHelper;
    ArrayList<ApplianceModel>arrayList;
    RecyclerApplianceAdapter(Context context, OnItemLongClickListener longClickListener){
        this.context = context;
        databaseHelper = new ApplianceDatabaseHelper(context);
        this.longClickListener = longClickListener;
        arrayList = databaseHelper.getAllAppliances();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_appliance, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.appliance_name.setText(arrayList.get(position).getApplianceName());
        holder.appliance_switch.setChecked(arrayList.get(position).getStatus());
        MaterialSwitch appliance_switch = holder.appliance_switch;
        appliance_switch.setOnClickListener(v -> {
            boolean state = appliance_switch.isChecked();
            arrayList.get(position).setStatus(state);
            databaseHelper.toggleApplianceStatus(arrayList.get(position).getApplianceId());

            // Send command via Bluetooth
            BluetoothManager bluetoothManager = BluetoothManager.getInstance(context);
            if (bluetoothManager.isConnected()) {
                // Format: "A1:ON" or "A1:OFF"
                String command = "A" + arrayList.get(position).getApplianceId() + ":" + (state ? "ON" : "OFF");
                boolean commandSent = bluetoothManager.sendCommand(arrayList.get(position).getApplianceId(), state);

                if (!commandSent) {
                    Toast.makeText(context, "Failed to send Bluetooth command", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Not connected to Bluetooth device", Toast.LENGTH_SHORT).show();
                // Optional: revert switch if not connected
                // appliance_switch.setChecked(!state);
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView appliance_name;
        MaterialSwitch appliance_switch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            appliance_name = itemView.findViewById(R.id.appliance_name);
            appliance_switch = itemView.findViewById(R.id.appliance_switch);

            itemView.setOnLongClickListener(v -> {
                if(longClickListener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        longClickListener.onItemLongClick(position);
                        return true;
                    }
                }
                return false;
            });
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}