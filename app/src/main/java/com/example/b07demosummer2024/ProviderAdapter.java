package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ChildViewHolder>{
    private List<ProviderInstance> providerList;

    public ProviderAdapter(List<ProviderInstance> providerList) {
        this.providerList = providerList;
    }

    public interface OnManagePermissionsClickListener {
        void onManageClick(ProviderInstance provider);
    }

    private OnManagePermissionsClickListener listener;
    public ProviderAdapter(List<ProviderInstance> providerList, OnManagePermissionsClickListener listener) {
        this.providerList = providerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_provider_adapter, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        ProviderInstance provider = providerList.get(position);
        holder.providerNameText.setText(provider.getProviderName());
        holder.providerNotesText.setText(provider.getNotes());
        holder.manageButton.setOnClickListener(view -> {
            if (listener != null) {
                listener.onManageClick(provider);
            }
        });
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView providerNameText, providerNotesText;
        Button manageButton;

        public ChildViewHolder(@NonNull View childView) {
            super(childView);
            providerNameText = childView.findViewById(R.id.providerNameText);
            providerNotesText = childView.findViewById(R.id.notes);
            manageButton = childView.findViewById(R.id.managePermissionsButton);

        }
    }
}