package com.example.b07demosummer2024;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ChildProfilePageFragment extends Fragment {

    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db;
    private TextView zoneDisplay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_profilepage, container, false);

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        String childId;
        SharedPreferences prefs = requireContext().getSharedPreferences("local_info", Context.MODE_PRIVATE);
        if (Objects.equals(prefs.getString("loginType", null), "childProfile")){
            childId = prefs.getString("curr_uid", null);
        } else {
            FirebaseUser user = myAuth.getCurrentUser();
            childId = user.getUid();
        }

        Button pefEntry = view.findViewById(R.id.setPefButton);
        Button startTriage = view.findViewById(R.id.startTriageButton);
        ImageButton signOut = view.findViewById(R.id.imageButton5);
        zoneDisplay = view.findViewById(R.id.zoneDisplay);
        setZoneDisplay(childId);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        pefEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new PefEntryFragment());
            }
        });

        startTriage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new TriageFragment());
            }
        });
        return view;
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
                if (dataSnapshot.getValue() == null){
                    zoneDisplay.setText("N/A");
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
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
