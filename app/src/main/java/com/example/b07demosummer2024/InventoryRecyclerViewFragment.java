package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class InventoryRecyclerViewFragment extends Fragment implements DeleteListener
{

    private String accountType;
    private RecyclerView InventoryRecyclerView;
    private MedicineAdapter medicineAdapter;
    private List<Medicine> medicineList;
    private List<String> brandNameList;
    private Spinner spSortByExpireState;
    private Spinner spSortByBrand;
    private ArrayAdapter<String> brandNameAdapter;
    private Button btExit;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance("https://b07-demo-summer-2024-default-rtdb.firebaseio.com/");
    private DatabaseReference dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_recycler_view, container, false);
        accountType = getArguments().getString("accountType");
        InventoryRecyclerView = view.findViewById(R.id.inventory_recyclerview);
        InventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        medicineList = new ArrayList<>();
        medicineAdapter = new MedicineAdapter(medicineList, this);
        InventoryRecyclerView.setAdapter(medicineAdapter);
        spSortByExpireState = view.findViewById(R.id.spSortByExpireState);
        spSortByBrand = view.findViewById(R.id.spSortByBrand);
        btExit = view.findViewById(R.id.exit_button);
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        setupSpinners();
        fetchMedicinesFromDatabase();

        return view;
    }

    private void fetchMedicinesFromDatabase()
    {
        dbRef = db.getReference("parents").child(getArguments().getString("parentUserId"))
                .child("medicines");

        dbRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                HashSet<String> brandNameSet = new HashSet<>();
                medicineList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Medicine med = snapshot.getValue(Medicine.class);
                    medicineList.add(med);
                    brandNameSet.add(med.getBrandName());
                }

                updateBrandNameSpinner(new ArrayList<>(brandNameSet));
                filterMedicine();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void setupSpinners()
    {
        ArrayAdapter<CharSequence> expireDateAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.expire_state_array, android.R.layout.simple_spinner_item);
        expireDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSortByExpireState.setAdapter(expireDateAdapter);

        brandNameList = new ArrayList<>();
        brandNameList.add("All");
        brandNameAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, brandNameList);
        brandNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSortByBrand.setAdapter(brandNameAdapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                filterMedicine();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spSortByExpireState.setOnItemSelectedListener(listener);
        spSortByBrand.setOnItemSelectedListener(listener);
    }


    private void filterMedicine()
    {
        String requiredMedicineType = getArguments().getString("requiredMedicineType");
        String selectedExpireState = spSortByExpireState.getSelectedItem().toString();
        String selectedBrand = spSortByBrand.getSelectedItem().toString();
        List<Medicine> filteredList = new ArrayList<>();
        for (Medicine currentMedicine : medicineList)
        {
            String currentBrand = currentMedicine.getBrandName();
            String currentExpireState = currentMedicine.getExpireState();
            String currentMedicineType = currentMedicine.getMedicineType();
            if (!currentMedicineType.equals(requiredMedicineType)) continue;
            if (selectedBrand.equals("All") || selectedBrand.equals(currentBrand))
            {
                if (selectedExpireState.equals("All") || currentExpireState.equals(selectedExpireState))
                {
                    filteredList.add(currentMedicine);
                }
            }
        }
        medicineAdapter.setMedicineList(filteredList);
        medicineAdapter.notifyDataSetChanged();
    }

    public void updateBrandNameSpinner(List<String> newBrands)
    {
        if (brandNameList != null && brandNameAdapter != null)
        {
            brandNameList.clear();
            brandNameList.add("All");
            brandNameList.addAll(newBrands);
            brandNameAdapter.notifyDataSetChanged();
        }
    }

    public void onDeleteClick(String medicineId)
    {
        if (medicineId == null || medicineId.isEmpty()) {
            Toast.makeText(getContext(), "Error: Log ID is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference medRef;
        medRef = db.getReference("parents").
                child(getArguments().getString("parentUserId")).child("medicines");


        medRef.child(medicineId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Medicine deleted successfully.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to delete Medicine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

}
