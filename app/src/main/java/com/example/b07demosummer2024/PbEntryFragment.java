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

public class PbEntryFragment extends Fragment {
    private EditText pbEntry;
    private Button returnButton;
    private Button pbEntryButton;
    private FirebaseDatabase db;
    private DatabaseReference childRef;
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_pb, container, false);

        pbEntry = view.findViewById(R.id.pbEntry);
        returnButton = view.findViewById(R.id.returnButton);
        pbEntryButton = view.findViewById(R.id.pbEntryButton);

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        pbEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pbEntered = pbEntry.getText().toString().trim();
                String childId = null;
                if (getArguments() != null) {
                    childId = getArguments().getString("childId");
                }
                logPbEntry(pbEntered, childId);
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

    private void logPbEntry(String pbEntry, String childId) {
        childRef = db.getReference("children/" + childId);
        childRef.child("personalBest").setValue(pbEntry);
    }
}
