package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private final int MEDICAL_LOG_VIEW_TYPE = 0;
    private final int INVENTORY_LOG_VIEW_TYPE = 1;
    private final int INCIDENT_LOG_VIEW_TYPE = 2;
    public LogAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }


    @Override
    public int getItemViewType(int position)
    {
        if (position == MEDICAL_LOG_VIEW_TYPE)
        {
            return MEDICAL_LOG_VIEW_TYPE;
        }
        else if (position == INVENTORY_LOG_VIEW_TYPE)
        {
            return INVENTORY_LOG_VIEW_TYPE;
        }
        else return INCIDENT_LOG_VIEW_TYPE;
    }

    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        if (viewType == MEDICAL_LOG_VIEW_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medical_log_item, parent, false);
            return new ItemAdapter.ItemViewHolder(view);
        }
        else if (viewType == INVENTORY_LOG_VIEW_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_log_item, parent, false);
        }
        else if (viewType == INCIDENT_LOG_VIEW_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        }

        return new ItemAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewAuthor.setText(item.getAuthor());
        holder.textViewGenre.setText(item.getGenre());
        holder.textViewDescription.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class MedicalLogViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAuthor, textViewGenre, textViewDescription;

        public MedicalLogViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewGenre = itemView.findViewById(R.id.textViewGenre);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }

    public static class InventoryLogViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAuthor, textViewGenre, textViewDescription;

        public InventoryLogViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewGenre = itemView.findViewById(R.id.textViewGenre);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }

    }
}

