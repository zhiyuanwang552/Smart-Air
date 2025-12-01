package com.example.b07demosummer2024;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ParentHomeActivity extends Fragment {
    private TextView zoneDisplay;
    private Spinner childSpinner;
    private Button setPbButton;
    private Button exportButton;
    private AlertAdapter alertAdapter;
    private RecyclerView recyclerView;
    private List<String> childNameList;
    private FirebaseDatabase db;
    private DatabaseReference childRef;
    private List<AlertInstance> alertList;

    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_home, container, false);

        FirebaseUser user = myAuth.getCurrentUser();
        String parentId = user.getUid();
        setPbButton = view.findViewById(R.id.setPbButton);
        zoneDisplay = view.findViewById(R.id.zoneDisplay);
        childSpinner = view.findViewById(R.id.childSpinner);
        childNameList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_alerts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        childNameList.clear();
        childNameList = fetchChildrenNamesFromDatabase("childProfile");
        childNameList = fetchChildrenNamesFromDatabase("childAccount");
        childNameList.add("Select Child Name");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, childNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childSpinner.setAdapter(adapter);

        alertList = new ArrayList<>();
        alertAdapter = new AlertAdapter(alertList);
        recyclerView.setAdapter(alertAdapter);

        reloadPage("blank");

        childSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String childName = parent.getItemAtPosition(position).toString();
                if (childName.equals("Select Child Name")){
                    reloadPage("blank");
                    return;
                }
                db.getReference("parents/" + parentId + "/childProfile").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.getValue(String.class);
                            String childId = snapshot.getRef().getKey();
                            if (name != null && name.equalsIgnoreCase(childName)) {
                                reloadPage(childId);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                db.getReference("parents/" + parentId + "/childAccount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.getValue(String.class);
                            String childId = snapshot.getRef().getKey();
                            if (name != null && name.equalsIgnoreCase(childName)) {
                                reloadPage(childId);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    private List<String> fetchChildrenNamesFromDatabase(String childType) { //get list of string attached to identifiers
        FirebaseUser user = myAuth.getCurrentUser();
        String parentId = user.getUid();
        childRef = db.getReference("parents/" + parentId + "/" + childType);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String childName = snapshot.getValue(String.class);
                    childNameList.add(childName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

        return childNameList;
    }
    private void reloadPage(String childId){
        if (childId.equals("blank")){
            // hide things, set defaults.
            return;
        }
        setZoneDisplay(childId);
        fetchAlertsFromDatabase(childId);
        setPbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PbEntryFragment PEF = new PbEntryFragment();
                Bundle args = new Bundle();
                args.putString("childId", childId);
                PEF.setArguments(args);
                loadFragment(PEF);
            }
        });
    }

    private void fetchAlertsFromDatabase(String childId) {
        childRef = db.getReference("children/" + childId + "/alertHistory");
        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alertList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AlertInstance alert = snapshot.getValue(AlertInstance.class);
                    alertList.add(alert);
                }
                alertAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
    private void setZoneDisplay(String childId) {
        String currentDate = java.time.LocalDate.now().toString();
        db.getReference("children/" + childId + "/pefHistory/" + currentDate + "/zone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.equals(dataSnapshot.getValue(String.class), "green")){
                    //do green things
                    zoneDisplay.setTextColor(Color.parseColor("#006400"));
                }
                else if (Objects.equals(dataSnapshot.getValue(String.class), "yellow")){
                    zoneDisplay.setTextColor(Color.parseColor("#8B8000"));
                }
                else if (Objects.equals(dataSnapshot.getValue(String.class), "red")){
                    zoneDisplay.setTextColor(Color.parseColor("#8B0000"));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
        db.getReference("children/" + childId + "/pefHistory/" + currentDate + "/pefEntry").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null || dataSnapshot.getValue(String.class).isEmpty()){
                    zoneDisplay.setText("Not yet Logged");
                }
                else {
                    zoneDisplay.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
        db.getReference("children/" + childId + "/pefHistory/" + currentDate + "/pefEntryOptional").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    if (!dataSnapshot.getValue(String.class).isEmpty()){
                        zoneDisplay.setText(dataSnapshot.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
