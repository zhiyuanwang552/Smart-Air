package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageScheduleFragment extends Fragment {
    private FirebaseDatabase db;
    private DatabaseReference scheduleRef;
    private DatabaseReference mondayRef;
    private DatabaseReference tuesdayRef;
    private DatabaseReference wednesdayRef;
    private DatabaseReference thursdayRef;
    private DatabaseReference fridayRef;
    private DatabaseReference saturdayRef;
    private DatabaseReference sundayRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_schedule, container, false);

        Switch monday = view.findViewById(R.id.mondaySwitch);
        Switch tuesday = view.findViewById(R.id.tuesdaySwitch);
        Switch wednesday = view.findViewById(R.id.wednesdaySwitch);
        Switch thursday = view.findViewById(R.id.thursdaySwitch);
        Switch friday = view.findViewById(R.id.fridaySwitch);
        Switch saturday = view.findViewById(R.id.saturdaySwitch);
        Switch sunday = view.findViewById(R.id.sundaySwitch);
        Button returnButton = view.findViewById(R.id.returnButton);

        String childId = null;

        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");
        scheduleRef = db.getReference("children/" + childId + "/medicineSchedule");
        mondayRef = scheduleRef.child("monday");
        tuesdayRef = scheduleRef.child("tuesday");
        wednesdayRef = scheduleRef.child("wednesday");
        thursdayRef = scheduleRef.child("thursday");
        fridayRef = scheduleRef.child("friday");
        saturdayRef = scheduleRef.child("saturday");
        sundayRef = scheduleRef.child("sunday");

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        setUpSwitch(mondayRef, monday);
        setUpSwitch(tuesdayRef, tuesday);
        setUpSwitch(wednesdayRef, wednesday);
        setUpSwitch(thursdayRef, thursday);
        setUpSwitch(fridayRef, friday);
        setUpSwitch(saturdayRef, saturday);
        setUpSwitch(sundayRef, sunday);

        monday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mondayRef.setValue(isChecked);
        });

        tuesday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tuesdayRef.setValue(isChecked);
        });

        wednesday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            wednesdayRef.setValue(isChecked);
        });

        thursday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            thursdayRef.setValue(isChecked);
        });

        friday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fridayRef.setValue(isChecked);
        });

        saturday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saturdayRef.setValue(isChecked);
        });

        sunday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sundayRef.setValue(isChecked);
        });

        return view;
    }

    private void setUpSwitch(DatabaseReference switchRef, Switch switchWidget) {
        switchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean state = snapshot.getValue(Boolean.class);
                if (state != null) {
                    switchWidget.setChecked(state);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}
