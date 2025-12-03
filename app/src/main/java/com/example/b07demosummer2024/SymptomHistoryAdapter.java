package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SymptomHistoryAdapter extends RecyclerView.Adapter<SymptomHistoryAdapter.Holder> {

    private List<DailyCheckInModel> list;

    public SymptomHistoryAdapter(List<DailyCheckInModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_symptom_history_item, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int i) {
        DailyCheckInModel m = list.get(i);

        h.date.setText("Date: " + m.timestamp);
        h.author.setText("Author: " + m.author);
        h.symptoms.setText("Symptoms: " + m.symptoms.toString());
        h.triggers.setText("Triggers: " + m.triggers.toString());
        h.notes.setText("Notes: " + m.notes);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView date, author, symptoms, triggers, notes;

        Holder(@NonNull View v) {
            super(v);
            date = v.findViewById(R.id.textViewDate);
            author = v.findViewById(R.id.textViewAuthor);
            symptoms = v.findViewById(R.id.textViewSymptoms);
            triggers = v.findViewById(R.id.textViewTriggers);
            notes = v.findViewById(R.id.textViewNotes);
        }
    }
}