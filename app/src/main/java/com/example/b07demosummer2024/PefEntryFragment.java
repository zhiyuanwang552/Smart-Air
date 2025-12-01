package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PefEntryFragment extends Fragment {
    private EditText pefEntry;
    private EditText pefEntryOptional;
    private Button returnButton;
    private Button pefEntryButton;
    private FirebaseDatabase db;
    private DatabaseReference childRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pef_entry, container, false);

        pefEntry = view.findViewById(R.id.pefEntry);
        pefEntryOptional = view.findViewById(R.id.pefEntryOptional);
        returnButton = view.findViewById(R.id.returnButton);
        pefEntryButton = view.findViewById(R.id.pefEntryButton);

        String pefEntered = pefEntry.getText().toString().trim();
        String pefEnteredOptional = pefEntryOptional.getText().toString().trim();

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        pefEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pefEnteredOptional.isEmpty()){
                    logPefEntry(pefEntered);
                }
                else {
                    logPefEntry(pefEntered, pefEnteredOptional);
                }
                getParentFragmentManager().popBackStack();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void logPefEntry(String pefEntered) {
        String currentDate = java.time.LocalDate.now().toString();
        childRef = db.getReference("children/genericChild/pefHistory/" + currentDate);
        childRef.child("pefEntry").setValue(pefEntered);
        childRef.child("pefEntryOptional").setValue("");
        db.getReference("children/personalBest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int personalBest;
                if (dataSnapshot.getValue() == null){
                    db.getReference("children/personalBest").setValue(pefEntered);
                    personalBest = Integer.parseInt(pefEntered);
                }
                else {
                    personalBest = Integer.parseInt(dataSnapshot.getValue(String.class));
                }
                if ((personalBest * 100) / Integer.parseInt(pefEntered) >= 80){
                    db.getReference("children/genericChild/pefHistory/" + currentDate + "zone").setValue("green");
                }
                else if ((personalBest * 100) / Integer.parseInt(pefEntered) >= 50){
                    db.getReference("children/genericChild/pefHistory/" + currentDate + "zone").setValue("yellow");
                }
                else {
                    db.getReference("children/genericChild/pefHistory/" + currentDate + "zone").setValue("red");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void logPefEntry(String pefEntered, String pefEnteredOptional) {
        String currentDate = java.time.LocalDate.now().toString();
        childRef = db.getReference("children/genericChild/pefHistory/" + currentDate);
        childRef.child("pefEntry").setValue(pefEntered);
        childRef.child("pefEntryOptional").setValue(pefEnteredOptional);
        db.getReference("children/personalBest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int personalBest;
                if (dataSnapshot.getValue() == null){
                    db.getReference("children/personalBest").setValue(pefEnteredOptional);
                    personalBest = Integer.parseInt(pefEnteredOptional);
                }
                else {
                    personalBest = Integer.parseInt(dataSnapshot.getValue(String.class));
                }
                if ((personalBest * 100) / Integer.parseInt(pefEnteredOptional) >= 80){
                    db.getReference("children/genericChild/pefHistory/" + currentDate + "zone").setValue("green");
                }
                else if ((personalBest * 100) / Integer.parseInt(pefEnteredOptional) >= 50){
                    db.getReference("children/genericChild/pefHistory/" + currentDate + "zone").setValue("yellow");
                }
                else {
                    db.getReference("children/genericChild/pefHistory/" + currentDate + "zone").setValue("red");
                    long now =  System.currentTimeMillis();
                    AlertInstance zoneAlert = new AlertInstance(now, "redZone", "moderate");
                    db.getReference("children/genericChild/alertHistory/" + now).setValue(zoneAlert);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
