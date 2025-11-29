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
import java.util.List;

public class ManageChildrenRemovalFragment extends Fragment {
    private Spinner childSpinner;
    private Button removalButton;
    private Button returnButton;
    private List<String> childNameList;
    private FirebaseDatabase db;
    private DatabaseReference childRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_children_removal, container, false);

        childSpinner = view.findViewById(R.id.childSpinner);
        returnButton = view.findViewById(R.id.returnButton);
        removalButton = view.findViewById(R.id.confirmRemovalButton);
        childNameList = new ArrayList<>();

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        childNameList.clear();
        childNameList = fetchChildrenNamesFromDatabase("childProfile");
        childNameList = fetchChildrenNamesFromDatabase("childAccount");
        childNameList.add("Select Child Name");

        // Set up the spinner with categories

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, childNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childSpinner.setAdapter(adapter);

        removalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteChildProfile("childProfile");
                deleteChildProfile("childAccount");
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

    private List<String> fetchChildrenNamesFromDatabase(String childType) { //get list of string attached to identifiers
        childRef = db.getReference("parents/genericParent/" + childType);
        childRef.addValueEventListener(new ValueEventListener() {
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

    private void deleteChildProfile(String childType) { //look for identifier, then delete corresponding address
        String childName = childSpinner.getSelectedItem().toString().toLowerCase();

        childRef = db.getReference("parents/genericParent/" + childType);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getValue(String.class);
                    String identifier = snapshot.getRef().getKey();
                    if (name != null && name.equalsIgnoreCase(childName)) {
                        db.getReference("children/" + identifier).removeValue();
                        snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Child removed", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().popBackStack();
                            } else {
                                Toast.makeText(getContext(), "Failed to remove child", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
