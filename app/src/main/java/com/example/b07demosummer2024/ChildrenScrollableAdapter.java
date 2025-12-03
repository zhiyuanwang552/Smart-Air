package com.example.b07demosummer2024;

import android.util.Log;
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

public class ChildrenScrollableAdapter extends RecyclerView.Adapter<ChildrenScrollableAdapter.ChildViewHolder> {
    private List<ManageChildrenScrollableFragment> childList;

    public ChildrenScrollableAdapter(List<ManageChildrenScrollableFragment> childList) {
        this.childList = childList;
    }

    public interface OnManageProvidersClickListener {
        void onManageClick(ManageChildrenScrollableFragment child);
    }

    private OnManageProvidersClickListener listener;
    public ChildrenScrollableAdapter(List<ManageChildrenScrollableFragment> childList, OnManageProvidersClickListener listener) {
        this.childList = childList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_children_scrollable_adapter, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        ManageChildrenScrollableFragment child = childList.get(position);
        holder.childNameText.setText(child.getChildName());
        holder.childDobText.setText(child.getBirthMonth() + "/" + child.getBirthDay() + "/" + child.getBirthYear());
        holder.childNotesText.setText(child.getNotes());
        holder.manageButton.setOnClickListener(view -> {
            if (listener != null) {
                listener.onManageClick(child);
            }
        });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView childNameText, childDobText, childNotesText;
        Button manageButton;

        public ChildViewHolder(@NonNull View childView) {
            super(childView);
            childNameText = childView.findViewById(R.id.childNameText);
            childDobText = childView.findViewById(R.id.childDobText);
            childNotesText = childView.findViewById(R.id.childNotesText);
            manageButton  = childView.findViewById(R.id.manageProvidersButton);

        }
    }
}
