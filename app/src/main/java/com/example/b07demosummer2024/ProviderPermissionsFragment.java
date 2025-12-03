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

public class ProviderPermissionsFragment extends Fragment {
    private FirebaseDatabase db;
    private DatabaseReference permissionsRef;
    private DatabaseReference rescueLogRef;
    private DatabaseReference controllerAdherenceRef;
    private DatabaseReference symptomsRef;
    private DatabaseReference triggersRef;
    private DatabaseReference peakFlowRef;
    private DatabaseReference triageRef;
    private DatabaseReference summaryRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_provider_permissions, container, false);

        Switch rescueLog = view.findViewById(R.id.rescueLogSwitch);
        Switch controllerAdherence = view.findViewById(R.id.controllerAdherenceSwitch);
        Switch symptoms = view.findViewById(R.id.symptomsSwitch);
        Switch triggers = view.findViewById(R.id.triggersSwitch);
        Switch peakFlow = view.findViewById(R.id.peakFlowSwitch);
        Switch triageIncidents = view.findViewById(R.id.triageSwitch);
        Switch summaryChart = view.findViewById(R.id.summarySwitch);
        TextView providerName = view.findViewById(R.id.textView4);
        EditText notesInput = view.findViewById(R.id.notesInput);

        String identifier = null;
        String id = null;
        String name = null;
        String notes = null;

        if (getArguments() != null) {
            identifier = getArguments().getString("identifier");
            id = getArguments().getString("id");
            name = getArguments().getString("name");
            notes = getArguments().getString("notes");
        }

        Button deleteProviderButton = view.findViewById(R.id.deleteProviderButton);
        Button returnButton = view.findViewById(R.id.returnButton);
        providerName.setText(name);
        notesInput.setText(notes);

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");
        permissionsRef = db.getReference("children/" + identifier + "/" + "providers/" + id);
        rescueLogRef = permissionsRef.child("rescueLog");
        controllerAdherenceRef = permissionsRef.child("controllerAdherence");
        symptomsRef = permissionsRef.child("symptoms");
        triggersRef = permissionsRef.child("triggers");
        peakFlowRef = permissionsRef.child("peakFlow");
        triageRef = permissionsRef.child("triageIncidents");
        summaryRef = permissionsRef.child("summaryCharts");

        deleteProviderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProvider(getArguments().getString("id"), getArguments().getString("identifier"));
                getParentFragmentManager().popBackStack();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String changedNotes = notesInput.getText().toString().trim();
                permissionsRef.child("notes").setValue(changedNotes);
                getParentFragmentManager().popBackStack();
            }
        });

        setUpSwitch(rescueLogRef, rescueLog);
        setUpSwitch(controllerAdherenceRef, controllerAdherence);
        setUpSwitch(symptomsRef, symptoms);
        setUpSwitch(triggersRef, triggers);
        setUpSwitch(peakFlowRef, peakFlow);
        setUpSwitch(triageRef, triageIncidents);
        setUpSwitch(summaryRef, summaryChart);

        rescueLog.setOnCheckedChangeListener((buttonView, isChecked) -> {
            rescueLogRef.setValue(isChecked);
        });

        controllerAdherence.setOnCheckedChangeListener((buttonView, isChecked) -> {
            controllerAdherenceRef.setValue(isChecked);
        });

        symptoms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            symptomsRef.setValue(isChecked);
        });

        triggers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            triggersRef.setValue(isChecked);
        });

        peakFlow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            peakFlowRef.setValue(isChecked);
        });

        triageIncidents.setOnCheckedChangeListener((buttonView, isChecked) -> {
            triageRef.setValue(isChecked);
        });

        summaryChart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            summaryRef.setValue(isChecked);
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

    private void removeProvider(String id, String identifier) {
        db.getReference("children/" + identifier + "/providers/" + id).removeValue();
        db.getReference("providers/" + id + "/connections/" + identifier).removeValue();
    }

}
