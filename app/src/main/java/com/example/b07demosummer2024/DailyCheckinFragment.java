package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DailyCheckinFragment extends Fragment {

    private MaterialCheckBox checkBoxNightWaking, checkBoxActivityLimits, checkBoxCoughWheeze, checkBoxNone;
    private MaterialCheckBox checkBoxTriggerExercise, checkBoxTriggerColdAir, checkBoxTriggerDustPets,
            checkBoxTriggerSmoke, checkBoxTriggerIllness, checkBoxTriggerOdors;
    private RadioGroup radioGroupAuthor;
    private TextInputEditText editTextNotes;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_check_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/").getReference("daily_check_ins");

        // Initialize Views
        // Symptoms
        checkBoxNightWaking = view.findViewById(R.id.checkBoxNightWaking);
        checkBoxActivityLimits = view.findViewById(R.id.checkBoxActivityLimits);
        checkBoxCoughWheeze = view.findViewById(R.id.checkBoxCoughWheeze);
        checkBoxNone = view.findViewById(R.id.checkBoxNone);

        // Triggers
        checkBoxTriggerExercise = view.findViewById(R.id.checkBoxTriggerExercise);
        checkBoxTriggerColdAir = view.findViewById(R.id.checkBoxTriggerColdAir);
        checkBoxTriggerDustPets = view.findViewById(R.id.checkBoxTriggerDustPets);
        checkBoxTriggerSmoke = view.findViewById(R.id.checkBoxTriggerSmoke);
        checkBoxTriggerIllness = view.findViewById(R.id.checkBoxTriggerIllness);
        checkBoxTriggerOdors = view.findViewById(R.id.checkBoxTriggerOdors);

        // Author
        radioGroupAuthor = view.findViewById(R.id.radioGroupAuthor);

        // Note
        editTextNotes = view.findViewById(R.id.editTextNotes);

        // Button
        Button buttonSaveCheckIn = view.findViewById(R.id.buttonSaveCheckIn);

        // Logic for the "None" checkbox
        checkBoxNone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxNightWaking.setEnabled(false);
                checkBoxActivityLimits.setEnabled(false);
                checkBoxCoughWheeze.setEnabled(false);
                checkBoxTriggerExercise.setEnabled(false);
                checkBoxTriggerColdAir.setEnabled(false);
                checkBoxTriggerDustPets.setEnabled(false);
                checkBoxTriggerSmoke.setEnabled(false);
                checkBoxTriggerIllness.setEnabled(false);
                checkBoxTriggerOdors.setEnabled(false);

                checkBoxNightWaking.setChecked(false);
                checkBoxActivityLimits.setChecked(false);
                checkBoxCoughWheeze.setChecked(false);
                checkBoxTriggerExercise.setChecked(false);
                checkBoxTriggerColdAir.setChecked(false);
                checkBoxTriggerDustPets.setChecked(false);
                checkBoxTriggerSmoke.setChecked(false);
                checkBoxTriggerIllness.setChecked(false);
                checkBoxTriggerOdors.setChecked(false);
            } else {
                checkBoxNightWaking.setEnabled(true);
                checkBoxActivityLimits.setEnabled(true);
                checkBoxCoughWheeze.setEnabled(true);
                checkBoxTriggerExercise.setEnabled(true);
                checkBoxTriggerColdAir.setEnabled(true);
                checkBoxTriggerDustPets.setEnabled(true);
                checkBoxTriggerSmoke.setEnabled(true);
                checkBoxTriggerIllness.setEnabled(true);
                checkBoxTriggerOdors.setEnabled(true);
            }
        });

        // Save button click listener
        buttonSaveCheckIn.setOnClickListener(v -> saveCheckInData());
    }

    private void saveCheckInData() {
        // Map to store the data
        Map<String, Object> checkInData = new HashMap<>();

        // Symptoms
        Map<String, Boolean> symptoms = new HashMap<>();
        symptoms.put("night_waking", checkBoxNightWaking.isChecked());
        symptoms.put("activity_limits", checkBoxActivityLimits.isChecked());
        symptoms.put("cough_wheeze", checkBoxCoughWheeze.isChecked());
        symptoms.put("none", checkBoxNone.isChecked());
        checkInData.put("symptoms", symptoms);

        // Triggers
        Map<String, Boolean> triggers = new HashMap<>();
        triggers.put("exercise", checkBoxTriggerExercise.isChecked());
        triggers.put("cold_air", checkBoxTriggerColdAir.isChecked());
        triggers.put("dust_pets", checkBoxTriggerDustPets.isChecked());
        triggers.put("smoke", checkBoxTriggerSmoke.isChecked());
        triggers.put("illness", checkBoxTriggerIllness.isChecked());
        triggers.put("odors", checkBoxTriggerOdors.isChecked());
        checkInData.put("triggers", triggers);

        // Author
        int selectedAuthorId = radioGroupAuthor.getCheckedRadioButtonId();
        if (selectedAuthorId != -1) {
            assert getView() != null;
            RadioButton selectedRadioButton = getView().findViewById(selectedAuthorId);
            checkInData.put("author", selectedRadioButton.getText().toString());
        } else {
            checkInData.put("author", "Not specified");
        }

        // Notes
        checkInData.put("notes", Objects.requireNonNull(editTextNotes.getText()).toString());

        // Timestamp
        checkInData.put("timestamp", System.currentTimeMillis());

        // Save to Firebase
        String checkInId = databaseReference.push().getKey();
        if (checkInId != null) {
            databaseReference.child(checkInId).setValue(checkInData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Check-in saved successfully!", Toast.LENGTH_SHORT).show();
                    // Optionally, navigate back or clear the form
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save check-in.", Toast.LENGTH_SHORT).show());
        }
    }
}
