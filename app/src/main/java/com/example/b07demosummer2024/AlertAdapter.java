package com.example.b07demosummer2024;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ChildViewHolder>{
    private List<AlertInstance> alertList;

    public AlertAdapter(List<AlertInstance> alertList) {
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_alert_adapter, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        AlertInstance alert = alertList.get(position);

        if (Objects.equals(alert.getAlertType(), "triageStart")){
            holder.alertText.setText("Child has started a triage .");
        }
        else if (Objects.equals(alert.getAlertType(), "triageEnd")){
            holder.alertText.setText("Child has ended their triage session.");
        }
        else if (Objects.equals(alert.getAlertType(), "triageEscalation")){
            holder.alertText.setText("Triage session has escalated.");
        }
        else if (Objects.equals(alert.getAlertType(), "redZone")){
            holder.alertText.setText("Child has logged a red zone.");
        }
        else if (Objects.equals(alert.getAlertType(), "badCondition")){
            holder.alertText.setText("Bad conditions logged in triage.");
        }
        else if (Objects.equals(alert.getAlertType(), "difficultyBreathingFlag")){
            holder.alertText.setText("Difficulty breathing logged in triage.");
        }
        else if (Objects.equals(alert.getAlertType(), "difficultySpeakingFlag")){
            holder.alertText.setText("Difficulty speaking logged in triage.");
        }
        else if (Objects.equals(alert.getAlertType(), "chestIssueFlag")){
            holder.alertText.setText("Chest retractions logged in triage.");
        }
        else if (Objects.equals(alert.getAlertType(), "chestPainFlag")){
            holder.alertText.setText("Chest pain logged in triage.");
        }
        else if (Objects.equals(alert.getAlertType(), "blueGreyLipsNailsFlag")){
            holder.alertText.setText("Discolored lips/nails logged in triage.");
        }
        else if (Objects.equals(alert.getAlertType(), "lowRescueInventory")){
            holder.alertText.setText("Running low on rescue medicine inventory.");
        }
        else if (Objects.equals(alert.getAlertType(), "lowRescueInventory")){
            holder.alertText.setText("Running low on control medicine inventory.");
        }

        Date date = new Date(alert.getTimeStamp());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        holder.alertDate.setText(sdf.format(date));

        if (Objects.equals(alert.getAlertSeverity(), "severe")){
            holder.alertLayout.setBackgroundResource(R.drawable.red_bg);
        }
        else {
            holder.alertLayout.setBackgroundResource(R.drawable.yellow_bg);
        }
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView alertText, alertDate;
        LinearLayout alertLayout;

        public ChildViewHolder(@NonNull View childView) {
            super(childView);
            alertText = childView.findViewById(R.id.alertText);
            alertDate = childView.findViewById(R.id.alertDate);
            alertLayout = childView.findViewById(R.id.alertLayout);
        }
    }
}