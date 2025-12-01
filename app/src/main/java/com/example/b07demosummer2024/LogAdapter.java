package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class LogAdapter extends RecyclerView.Adapter<LogAdapter.MedicalLogViewHolder> {
    private List<MedicalLog> logList;
    private DeleteListener deleteListener;

    public LogAdapter(List<MedicalLog> logList, DeleteListener listener) {
        this.logList = logList;
        this.deleteListener = listener;
    }

    public void setLogList(List<MedicalLog> logList) {
        this.logList = logList;
    }

    @NonNull
    @Override
    public LogAdapter.MedicalLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medical_log_item, parent, false);
        return new LogAdapter.MedicalLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalLogViewHolder holder, int position) {
        MedicalLog log = logList.get(position);
        String formattedDate = log.getFormattedDateTime("yyyy-MM-dd HH:mm:ss a");
        String reflectionInfo = "Reflection after treatment: feeling " + log.getReflection();
        String currentMedId = log.getLogId();
        String puffsUsed = "" + log.getPuff();

        holder.textViewDate.setText(formattedDate);
        holder.textViewMedId.setText(currentMedId);
        holder.textViewReflection.setText(reflectionInfo);
        holder.textViewSymptoms.setText(log.getDescriptions());
        holder.textViewNumberPuffs.setText(puffsUsed);

        holder.deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (deleteListener != null) deleteListener.onDeleteClick(log.getLogId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public static class MedicalLogViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewMedId, textViewReflection, textViewNumberPuffs, textViewSymptoms;
        Button deleteButton;

        public MedicalLogViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewMedId = itemView.findViewById(R.id.textViewMedId);
            textViewReflection = itemView.findViewById(R.id.textViewReflection);
            textViewNumberPuffs = itemView.findViewById(R.id.textViewNumberPuffs);
            textViewSymptoms = itemView.findViewById(R.id.textViewSymptoms);
            deleteButton = itemView.findViewById(R.id.buttonDeleteLog);

        }
    }


}

