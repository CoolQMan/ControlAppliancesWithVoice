package com.team17.controlapplianceswithvoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;

public class RecyclerApplianceAdapter extends RecyclerView.Adapter<RecyclerApplianceAdapter.ViewHolder> {
    Context context;
    ArrayList<ApplianceModel>arrayList;
    RecyclerApplianceAdapter(Context context){
        this.context = context;
        arrayList = new ArrayList<>();
        arrayList.add(new ApplianceModel("Light Bulb 1", false));
        arrayList.add(new ApplianceModel("Light Bulb 2", true));
        arrayList.add(new ApplianceModel("Fan 1", false));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_appliance, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.appliance_name.setText(arrayList.get(position).applianceName);
        holder.appliance_switch.setChecked(arrayList.get(position).status);
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

        }
    }


}
