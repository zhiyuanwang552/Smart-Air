package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.time.format.DateTimeFormatter;


public class LogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GeneralLog> logList;
    private final int MEDICAL_LOG_VIEW_TYPE = 0;
    private final int INCIDENT_LOG_VIEW_TYPE = 1;
    public LogAdapter(List<GeneralLog> logList) {
        this.logList = logList;
    }


    @Override
    public int getItemViewType(int position)
    {
        return logList.get(position).getLogType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MEDICAL_LOG_VIEW_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medical_log_item, parent, false);
            return new LogAdapter.MedicalLogViewHolder(view);
        }
        else if (viewType == INCIDENT_LOG_VIEW_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            return new LogAdapter.MedicalLogViewHolder(view);
        }
        else
        {
            throw new IllegalArgumentException("Invalid view type");
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GeneralLog log = logList.get(position);
        if (holder.getItemViewType() == MEDICAL_LOG_VIEW_TYPE)
        {
            MedicalLogViewHolder medicalLogHolder = (MedicalLogViewHolder) holder;
            MedicalLog currentMedicallog = (MedicalLog)log;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = (currentMedicallog.getLogDate()).format(formatter);
            String reflectionInfo = "Reflection after treatment: feeling " + currentMedicallog.getReflection();
            String currentMedId = "" + currentMedicallog.getLogId();
            String puffsUsed = "" + currentMedicallog.getPuff();

            medicalLogHolder.textViewDate.setText(formattedDate);
            medicalLogHolder.textViewMedId.setText(currentMedId);
            medicalLogHolder.textViewReflection.setText(reflectionInfo);
            medicalLogHolder.textViewSymptoms.setText(log.getDescriptions());
        }
        else if (holder.getItemViewType() == INCIDENT_LOG_VIEW_TYPE)
        {
        }

    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public static class MedicalLogViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewMedId, textViewReflection, textViewNumberPuffs, textViewSymptoms;

        public MedicalLogViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewMedId = itemView.findViewById(R.id.textViewMedId);
            textViewReflection = itemView.findViewById(R.id.textViewReflection);
            textViewNumberPuffs = itemView.findViewById(R.id.textViewNumberPuffs);
            textViewSymptoms = itemView.findViewById(R.id.textViewSymptoms);
        }
    }

    public static class IncidentLogViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAuthor, textViewGenre, textViewDescription;

        public IncidentLogViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewGenre = itemView.findViewById(R.id.textViewGenre);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }

    }
}

