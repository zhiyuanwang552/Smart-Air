package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddNewMedicineFragment extends Fragment {
    private Button btSave;
    private Button btCancel;
    private Spinner spNewMedicineType;
    private TextInputLayout estimatePuffsInlayout;
    private TextInputLayout costInLayout;
    private TextInputLayout brandInLayout;
    private TextInputLayout shelfLifeInLayout;
    private TextInputEditText etPuffsInput;
    private TextInputEditText etBrandNameInput;
    private TextInputEditText etCostInput;
    private TextInputEditText etShelfLifeInput;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef;
    private String accountType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_inventory, container, false);
        accountType = getArguments().getString("accountType");

        spNewMedicineType = view.findViewById(R.id.spNewMedicineType);
        estimatePuffsInlayout = view.findViewById(R.id.estimatePuffsInLayout);
        costInLayout = view.findViewById(R.id.costInLayout);
        brandInLayout = view.findViewById(R.id.brandInLayout);
        shelfLifeInLayout = view.findViewById(R.id.shelfLifeInLayout);
        etPuffsInput = view.findViewById(R.id.etEstimatePuffsInput);
        etBrandNameInput = view.findViewById(R.id.etBrandNameInput);
        etCostInput = view.findViewById(R.id.etCostInput);
        etShelfLifeInput = view.findViewById(R.id.etShelfLifeInput);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.medicine_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNewMedicineType.setAdapter(adapter);

        return view;
    }

    void validateInputAndSave()
    {
        String medicineType = spNewMedicineType.getSelectedItem().toString();
        String estimatePuffs = etPuffsInput.getText().toString().trim();
        String cost = etCostInput.getText().toString().trim();
        String brandName = etBrandNameInput.getText().toString().trim();
        String shelfLife = etShelfLifeInput.getText().toString().trim();

        boolean isValid = true;
        if (estimatePuffs.isEmpty()) {
            estimatePuffsInlayout.setError("This Selection cannot be empty!");
            isValid = false;
        }
        else if (shelfLife.isEmpty()) {
            shelfLifeInLayout.setError("This Selection cannot be empty!");
            isValid = false;
        }
        else if (!cost.isEmpty()) {
            try
            {
                double costValue = Double.parseDouble(cost);
                if (costValue <= 0)
                {
                    costInLayout.setError("Cost must be positive!");
                    isValid = false;
                }
            }
            catch (NumberFormatException e) {
                costInLayout.setError("Not valid number!");
                isValid = false;
            }
        }

        try {
            int shelfLifeValue = Integer.parseInt(shelfLife);
            if (shelfLifeValue <= 0) {
                shelfLifeInLayout.setError("Shelf life must be positive!");
                isValid = false;
            }
        }
        catch (NumberFormatException e) {
            shelfLifeInLayout.setError("Not valid number!");
            isValid = false;
        }
        try
        {
            int estimatePuffsValue = Integer.parseInt(estimatePuffs);
            if (estimatePuffsValue <= 0) //suppose to be positive
            {
                estimatePuffsInlayout.setError("Used puffs must be positive!");
                isValid = false;
            }
        }
        catch (NumberFormatException e) {
            estimatePuffsInlayout.setError("Not valid number!");
            isValid = false;
        }

        if (isValid)
        {
            String parentUserId = getArguments().getString("parentUserId");
            dbRef = db.getReference("parentAccount").child(parentUserId).child("medicines");
            String newLogId = dbRef.push().getKey();
            long expireDate = System.currentTimeMillis() + 24 * 60 * 60 * Long.parseLong(shelfLife);
            long purchaseDate = System.currentTimeMillis();
            int estimatePuffsValue = Integer.parseInt(estimatePuffs);
            double costValue = Double.parseDouble(cost);

            Medicine newMedicine = new Medicine(newLogId, expireDate, purchaseDate,
                    medicineType, estimatePuffsValue, estimatePuffsValue, costValue, brandName);
            dbRef.child(newLogId).setValue(newMedicine)
                    .addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Saving Success!", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    void setupButtons()
    {
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputAndSave();
            }
        });
    }
}
