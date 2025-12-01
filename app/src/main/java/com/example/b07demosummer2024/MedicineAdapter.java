package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<Medicine> medicineList;
    private DeleteListener deleteListener;

    public MedicineAdapter(List<Medicine> medicineList, DeleteListener listener) {
        this.medicineList = medicineList;
        this.deleteListener = listener;
    }

    public void setMedicineList(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineAdapter.MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medical_log_item, parent, false);
        return new MedicineAdapter.MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineAdapter.MedicineViewHolder holder, int position)
    {
        android.content.Context context = holder.itemView.getContext();
        String formattedExpireDate, formattedPurchaseDate, formattedCost,
                formattedPuffs, currentMedId, formattedBrandName;

        Medicine med = medicineList.get(position);

        formattedExpireDate = med.getFormattedDateTime(med.getExpireDate(), "yyyy-MM-dd");
        formattedExpireDate += " (" + med.getExpireState() + ")";
        currentMedId = med.getMedicineId();
        formattedPurchaseDate = med.getFormattedDateTime(med.getPurchaseDate(), "yyyy-MM-dd");

        if (med.getPrice() < 0) formattedCost = "N/A";
        else formattedCost = "$" + med.getPrice();

        if (med.getBrandName() == null || med.getBrandName().isEmpty()) formattedBrandName = "N/A";
        else formattedBrandName = med.getBrandName();

        if (med.getRemainingPuffs() <= 0) formattedPuffs = "Out Of Stack";
        else formattedPuffs = String.valueOf(med.getRemainingPuffs());

        holder.tvMedicineId.setText(currentMedId);
        holder.tvBrandNameValue.setText(formattedBrandName);
        holder.tvPuffsValue.setText(formattedPuffs);
        if ("Expired".equals(med.getExpireState()) || "Soon to Expire".equals(med.getExpireState())) {
            // Get warning red
            holder.tvExpireDate.setTextColor(ContextCompat.getColor(context, R.color.warning_red));
        }
        else {
            // Reset color
            holder.tvExpireDate.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        holder.tvExpireDate.setText(formattedExpireDate);

        if ("Out of Stack".equals(med.getInventoryState()) || "Low Inventory".equals(med.getInventoryState())) {
            holder.tvPurchaseDate.setTextColor(ContextCompat.getColor(context, R.color.warning_red));
        }
        else {
            holder.tvPurchaseDate.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        holder.tvPurchaseDate.setText(formattedPurchaseDate);
        holder.tvCostValue.setText(formattedCost);

        holder.btDeleteMed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (deleteListener != null) deleteListener.onDeleteClick(med.getMedicineId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineId, tvBrandNameValue, tvPuffsValue, tvPurchaseDate, tvExpireDate, tvCostValue;
        Button btDeleteMed;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineId = itemView.findViewById(R.id.tvMedicineId);
            tvBrandNameValue = itemView.findViewById(R.id.tvBrandNameValue);
            tvPuffsValue = itemView.findViewById(R.id.tvPuffsValue);
            tvPurchaseDate = itemView.findViewById(R.id.tvPurchaseDate);
            tvExpireDate = itemView.findViewById(R.id.tvExpireDate);
            tvCostValue = itemView.findViewById(R.id.tvCostValue);
            btDeleteMed = itemView.findViewById(R.id.btDeleteMed);
        }
    }

    public interface OnMedicineDeleteListener {
        public void onDeleteClick(String logId); // 点击时将传递logId
    }

}
