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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddNewLogFragment extends Fragment
{
    private Button buttonSave;
    private Button buttonCancel;
    private Spinner spinnerReflectionType;
    private Spinner spinnerMedicineId;
    private TextInputLayout puffsInlayout;
    private TextInputLayout descriptionInlayout;
    private TextInputEditText etPuffsInput;
    private TextInputEditText etDescriptionInput;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");
    private DatabaseReference dbRef;
    private String accountType;
    private ArrayAdapter<String> medicineIdAdapter;
    private List<String> medicineIdList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_log, container, false);
        accountType = getArguments().getString("userType");
        buttonSave = view.findViewById(R.id.btInventorySave);
        buttonCancel = view.findViewById(R.id.btInventoryCancel);
        spinnerReflectionType = view.findViewById(R.id.spinnerReflectionType);
        spinnerMedicineId = view.findViewById(R.id.spinnerMedicineId);
        puffsInlayout = view.findViewById(R.id.estimatePuffsInLayout);
        descriptionInlayout = view.findViewById(R.id.description_input_layout);
        etPuffsInput = view.findViewById(R.id.etPuffsInput);
        etDescriptionInput = view.findViewById(R.id.etBrandNameInput);
        medicineIdList = new ArrayList<>();

        setupSpinners();
        fetchSpinnerOptionFromDatabase();
        setupButtons();

        return view;
    }

    void setupSpinners()
    {
        ArrayAdapter<CharSequence> reflectionTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.reflection_type_array, android.R.layout.simple_spinner_item);
        reflectionTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReflectionType.setAdapter(reflectionTypeAdapter);

        medicineIdAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, medicineIdList);
        medicineIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedicineId.setAdapter(medicineIdAdapter);
    }
    void fetchSpinnerOptionFromDatabase()
    {
        dbRef = db.getReference("parents").
                child(getArguments().getString("parentUserId")).child("medicines");

        dbRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                medicineIdList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String medicineId = snapshot.getKey();
                    medicineIdList.add(medicineId);
                }
                medicineIdAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    void setupButtons()
    {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                validateAndSaveLog();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

    }

    void validateAndSaveLog()
    {
        Object medicineIdObject = spinnerMedicineId.getSelectedItem();
        if (medicineIdObject == null) {
            Toast.makeText(getContext(), "Missing Medicine Id! Update your Inventory first!", Toast.LENGTH_SHORT).show();
            return;
        }
        String reflectionType = spinnerReflectionType.getSelectedItem().toString();
        String medicineId = medicineIdObject.toString();
        String puffs = etPuffsInput.getText().toString().trim();
        String description = etDescriptionInput.getText().toString().trim();

        boolean isValid = true;

        if (puffs.isEmpty())
        {
            puffsInlayout.setError("This Selection cannot be empty!");
            isValid = false;
        }
        else
        {
            try
            {
                int puffsValue = Integer.parseInt(puffs);
                if (puffsValue <= 0) { //Must be positive
                    puffsInlayout.setError("Used puffs must be positive!");
                    isValid = false;
                }
            }
            catch (NumberFormatException e)
            {
                puffsInlayout.setError("Not valid number!");
                isValid = false;
            }
        }

        if (isValid) addLogToDataBase(puffs, description);
    }

    private void addLogToDataBase(String puffs, String description)
    {
        String medicineId = spinnerMedicineId.getSelectedItem().toString();

        MedicineTypeCallback callback = new MedicineTypeCallback()
        {
            @Override
            public void onCallback(String medicineType)
            {
                if (medicineType == null) {
                    Toast.makeText(getContext(), "Could not find medicine type, saving with ID.", Toast.LENGTH_SHORT).show();
                    medicineType = "Controller";
                }

                String reflectionType = spinnerReflectionType.getSelectedItem().toString();
                String userName = getArguments().getString("userName");
                String parentUserId = getArguments().getString("parentUserId");

                if (parentUserId == null || parentUserId.isEmpty()) {
                    Toast.makeText(getContext(), "Error: User ID is missing. Cannot save.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int puffsValue = Integer.parseInt(puffs);

                DatabaseReference userLogsRef = db.getReference("parents")
                        .child(parentUserId).child("medicalLogs");
                String newLogId = userLogsRef.push().getKey();

                long timestamp = System.currentTimeMillis();
                // 现在使用真实的 medicineType
                MedicalLog newLog = new MedicalLog(newLogId, GeneralLog.MedicalLogType, timestamp, description,
                        medicineId, reflectionType, puffsValue, userName, medicineType);

                if (newLogId != null)
                {
                    OnSuccessListener successListener = new OnSuccessListener()
                    {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(getContext(), "Saving Success!", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        }
                    };
                    OnFailureListener failureListener = new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    };
                    userLogsRef.child(newLogId).setValue(newLog)
                            .addOnSuccessListener(successListener)
                            .addOnFailureListener(failureListener);
                }
            }
        };
        
        getMedicineTypeUsingId(medicineId, callback, Integer.parseInt(puffs));
    }

    private void getMedicineTypeUsingId(String medicineId, final MedicineTypeCallback callback, int puffsValue) {
        String parentUserId = getArguments().getString("parentUserId");
        if (parentUserId == null || parentUserId.isEmpty() || medicineId == null || medicineId.isEmpty())
        {
            callback.onCallback(null); // 返回null表示失败
            return;
        }

        //absolute path
        DatabaseReference medicineRef = db.getReference("parents")
                .child(parentUserId)
                .child("medicines")
                .child(medicineId);

        medicineRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Medicine linkedMed = dataSnapshot.getValue(Medicine.class);
                    int newRemainningPuffs = linkedMed.getRemainingPuffs() - puffsValue;
                    medicineRef.child("remainingPuffs").setValue(newRemainningPuffs);

                    callback.onCallback(linkedMed.getMedicineType());
                } else {
                    // 路径不存在
                    callback.onCallback(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 读取失败
                Toast.makeText(getContext(), "Failed to get medicine type: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onCallback(null);
            }
        });
    }
    public interface MedicineTypeCallback {
        void onCallback(String medicineType);
    }
}
