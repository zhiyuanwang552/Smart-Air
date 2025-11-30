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
    private Spinner spinerMedicineId;
    private TextInputLayout puffsInlayout;
    private TextInputLayout descriptionInlayout;
    private TextInputEditText etPuffsInput;
    private TextInputEditText etDescriptionInput;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef;
    private String accountType;
    private ArrayAdapter<String> medicineIdAdapter;
    private List<String> medicineIdList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_log, container, false);
        accountType = getArguments().getString("accountType");
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        spinnerReflectionType = view.findViewById(R.id.spinnerReflectionType);
        spinerMedicineId = view.findViewById(R.id.spinnerMedicineId);
        puffsInlayout = view.findViewById(R.id.puffs_input_layout);
        descriptionInlayout = view.findViewById(R.id.description_input_layout);
        etPuffsInput = view.findViewById(R.id.etPuffsInput);
        etDescriptionInput = view.findViewById(R.id.etDescriptionInput);
        medicineIdList = new ArrayList<>();

        setupSpinners();
        fetchSpinnerOptionFromDatabase();
        setupButtons();
        validateAndSaveLog();


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
        spinerMedicineId.setAdapter(medicineIdAdapter);
    }


    void fetchSpinnerOptionFromDatabase()
    {
        dbRef = db.getReference("parentAccount").
                child(getArguments().getString("parentUserId")).child("medicine");
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
        String reflectionType = spinnerReflectionType.getSelectedItem().toString();
        String medicineId = spinerMedicineId.getSelectedItem().toString();
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
                if (puffsValue <= 0) { // 用量通常是正数
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
        if (isValid)
        {
            addLogToDataBase(puffs, description);
        }



    }

    private void addLogToDataBase(String puffs, String description)
    {
        String medicineId = spinerMedicineId.getSelectedItem().toString();

        MedicineTypeCallback callback = new MedicineTypeCallback()
        {
            @Override
            public void onCallback(String medicineType)
            {
                // 这个回调会在获取到 medicineType 后被执行
                if (medicineType == null) {
                    // 如果获取失败，可以给一个默认值或者提示错误
                    Toast.makeText(getContext(), "Could not find medicine type, saving with ID.", Toast.LENGTH_SHORT).show();
                    medicineType = medicineId; // 使用 medicineId 作为备用值
                }

                // --- 在回调内部继续执行保存操作 ---
                String reflectionType = spinnerReflectionType.getSelectedItem().toString();
                String userName = getArguments().getString("userName");
                String parentUserId = getArguments().getString("parentUserId");

                if (parentUserId == null || parentUserId.isEmpty()) {
                    Toast.makeText(getContext(), "Error: User ID is missing. Cannot save.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int puffsValue = Integer.parseInt(puffs);

                DatabaseReference userLogsRef = db.getReference("parentAccount")
                        .child(parentUserId).child("medicalLog");
                String newLogId = userLogsRef.push().getKey();

                long timestamp = System.currentTimeMillis();

                // 现在使用真实的 medicineType
                MedicalLog newLog = new MedicalLog(newLogId, GeneralLog.MedicalLogType, timestamp, description,
                        medicineId, reflectionType, puffsValue, userName, medicineType);

                if (newLogId != null) {
                    userLogsRef.child(newLogId).setValue(newLog)
                            .addOnSuccessListener(aVoid -> {
                                // 成功
                                Toast.makeText(getContext(), "Saving Success!", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().popBackStack();
                            })
                            .addOnFailureListener(e -> {
                                // 失败
                                Toast.makeText(getContext(), "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        };

        // 调用异步方法来获取 medicineType
        getMedicineTypeUsingId(medicineId, callback);
    }

    private void getMedicineTypeUsingId(String medicineId, final MedicineTypeCallback callback) {
        String parentUserId = getArguments().getString("parentUserId");
        if (parentUserId == null || parentUserId.isEmpty() || medicineId == null || medicineId.isEmpty())
        {
            callback.onCallback(null); // 返回null表示失败
            return;
        }

        // 构造指向特定药品信息的绝对路径
        DatabaseReference medicineRef = db.getReference("parentAccount")
                .child(parentUserId)
                .child("medicine")
                .child(medicineId);

        // 使用 addListenerForSingleValueEvent 进行一次性读取
        medicineRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 假设药品信息下有一个名为 "type" 的字段存储着药品类型
                    String medicineType = dataSnapshot.child("medicineType").getValue(String.class);
                    callback.onCallback(medicineType);
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
