package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import java.util.List;

public class InventoryMenuFragment extends Fragment
{
    private TextView tvRescueValue;
    private TextView tvControllerValue;
    private TextView tvControllerLastPurchase;
    private TextView tvRescueLastPurchase;
    private Button btCheckRescue;
    private Button btCheckController;
    private Button btAddNewMedicine;
    private List<Medicine> medicineList;
    private DatabaseReference dbRef;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_menu, container, false);

        tvRescueValue = view.findViewById(R.id.tvRescueValue);
        tvControllerValue = view.findViewById(R.id.tvControllerValue);
        tvControllerLastPurchase = view.findViewById(R.id.tvControllerLastPurchase);
        tvRescueLastPurchase = view.findViewById(R.id.tvRescueLastPurchase);
        btCheckRescue = view.findViewById(R.id.btCheckRescue);
        btCheckController = view.findViewById(R.id.btCheckController);
        btAddNewMedicine = view.findViewById(R.id.btAddNewMedicine);
        medicineList = new ArrayList<>();


        dbRef = db.getReference("parents").child(getArguments().getString("parentUserId")).child("medicines");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicineList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Medicine Med = snapshot.getValue(Medicine.class);
                    medicineList.add(Med);
                }
                changeText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
        setupButtons();
        changeText();

        return view;
    }

    private void changeText()
    {
        //default value for my text
        if (medicineList == null || medicineList.isEmpty())
        {
            tvRescueValue.setText("0");
            tvControllerValue.setText("0");
            tvRescueLastPurchase.setText("N/A");
            tvControllerLastPurchase.setText("N/A");
            return;
        }

        int numOfRescue = 0;
        int numController = 0;
        long lastPurchaseRescue = 0;
        long lastPurchaseController = 0;

        for (Medicine medicine : medicineList)
        {
            if (medicine.getMedicineType().equals("Rescue"))
            {
                numOfRescue++;
                if (lastPurchaseRescue < medicine.getPurchaseDate()) lastPurchaseRescue = medicine.getPurchaseDate();
            }
            else
            {
                numController++;
                if (lastPurchaseController < medicine.getPurchaseDate()) lastPurchaseController = medicine.getPurchaseDate();
            }
        }

        String formattedDateRescue, formattedDateController;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        if (lastPurchaseRescue == 0) formattedDateRescue = "N/A";
        else formattedDateRescue = dateFormat.format(new Date(lastPurchaseRescue));
        if (lastPurchaseController == 0) formattedDateController = "N/A";
        else formattedDateController = dateFormat.format(new Date(lastPurchaseController));

        tvRescueValue.setText(String.valueOf(numOfRescue));
        tvControllerValue.setText(String.valueOf(numController));
        tvRescueLastPurchase.setText(formattedDateRescue);
        tvControllerLastPurchase.setText(formattedDateController);
    }

    private void setupButtons()
    {
        btCheckRescue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new InventoryRecyclerViewFragment();
                Bundle oldbundle = getArguments();
                Bundle newbundle = new Bundle();
                newbundle.putAll(oldbundle);
                newbundle.putString("requiredMedicineType", "Rescue");
                fragment.setArguments(newbundle);
                loadFragmentReplace(fragment);
            }
        });
        btCheckController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new InventoryRecyclerViewFragment();
                Bundle oldbundle = getArguments();
                Bundle newbundle = new Bundle();
                newbundle.putAll(oldbundle);
                newbundle.putString("requiredMedicineType", "Controller");
                fragment.setArguments(newbundle);
                loadFragmentReplace(fragment);
            }
        });
        btAddNewMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AddNewMedicineFragment();
                Bundle bundle = getArguments();
                fragment.setArguments(bundle);
                loadFragmentAdd(fragment);
            }
        });;
    }

    private void loadFragmentReplace(Fragment fragment)
    {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadFragmentAdd(Fragment fragment)
    {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
