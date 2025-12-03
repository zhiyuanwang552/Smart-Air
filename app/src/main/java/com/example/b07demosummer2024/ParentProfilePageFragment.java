package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.Calendar;

public class ParentProfilePageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_profilepage, container, false);

        Button manageChildren = view.findViewById(R.id.manageChildrenButton);
        Button dailyCheckInBtn = view.findViewById(R.id.buttonDailyCheckIn);
        Button historyBtn = view.findViewById(R.id.buttonCheckInHistory);

        ImageButton signOut = view.findViewById(R.id.imageButton10);
        ImageButton childSignIn = view.findViewById(R.id.imageButton13);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        childSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new ChildProfileSignInFragment());
            }
        });

        manageChildren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new ManageChildrenFragment());
            }
        });

        dailyCheckInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new DailyCheckinFragment());
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SymptomHistoryFragment());
            }
        });

        checkDailyCheckInStatus();

        return view;
    }

    private void checkDailyCheckInStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String myUid = currentUser.getUid();

        FirebaseDatabase.getInstance()
                .getReference("daily_check_ins")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        boolean doneToday = false;

                        // Get today's date
                        Calendar today = Calendar.getInstance();
                        int y = today.get(Calendar.YEAR);
                        int m = today.get(Calendar.MONTH);
                        int d = today.get(Calendar.DAY_OF_MONTH);

                        for (DataSnapshot entry : snapshot.getChildren()) {

                            Object uidObj = entry.child("uid").getValue();
                            if (uidObj == null) continue;

                            if (!uidObj.toString().equals(myUid))
                                continue; // skip other user's entries

                            Object tsObj = entry.child("timestamp").getValue();
                            if (tsObj == null) continue;

                            long ts;
                            try {
                                if (tsObj instanceof Long) ts = (Long) tsObj;
                                else if (tsObj instanceof Double) ts = ((Double) tsObj).longValue();
                                else ts = Long.parseLong(tsObj.toString());
                            } catch (Exception e) {
                                continue;
                            }

                            Calendar entryCal = Calendar.getInstance();
                            entryCal.setTimeInMillis(ts);

                            if (entryCal.get(Calendar.YEAR) == y &&
                                    entryCal.get(Calendar.MONTH) == m &&
                                    entryCal.get(Calendar.DAY_OF_MONTH) == d) {

                                doneToday = true;
                                break;
                            }
                        }

                        if (!doneToday) {
                            Toast.makeText(getContext(),
                                    "Daily check-in not completed today!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to read daily check-ins.", Toast.LENGTH_SHORT).show();
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
