package com.example.b07demosummer2024;

import static android.graphics.Color.parseColor;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ManageChildrenAddFragment extends Fragment {
    private EditText childNameInput, birthMonthInput, birthDayInput, birthYearInput, notesInput, usernameInput, passwordInput, confirmPasswordInput;
    private String childType = "childAccount";

    private FirebaseDatabase db;
    private DatabaseReference parentRef;
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_children_add, container, false);

        childNameInput = view.findViewById(R.id.childNameInput);
        birthMonthInput = view.findViewById(R.id.birthMonthInput);
        birthDayInput = view.findViewById(R.id.birthDayInput);
        birthYearInput = view.findViewById(R.id.birthYearInput);
        notesInput = view.findViewById(R.id.notesInput);
        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);

        Button returnButton = view.findViewById(R.id.returnButton);
        Button createButton = view.findViewById(R.id.createButton);
        Button profileButton = view.findViewById(R.id.profileButton);
        Button childAccountButton = view.findViewById(R.id.childAccountButton);
        View childAccountSection = view.findViewById(R.id.childAccountSection);

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChild();
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileButton.setBackgroundColor(parseColor("#D7D7D7"));
                childAccountButton.setBackgroundColor(parseColor("#FFFFFFFF"));
                childAccountSection.setVisibility(View.GONE);
                childType = "childProfile";
            }
        });

        childAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileButton.setBackgroundColor(parseColor("#FFFFFFFF"));
                childAccountButton.setBackgroundColor(parseColor("#D7D7D7"));
                childAccountSection.setVisibility(View.VISIBLE);
                childType = "childAccount";
            }
        });

        return view;

    }

    private void createChild() {
        String childName = childNameInput.getText().toString().trim();
        String birthMonth = birthMonthInput.getText().toString().trim();
        String birthDay = birthDayInput.getText().toString().trim();
        String birthYear = birthYearInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();
        String username = "";
        String password = "";
        String confirmPassword;
        FirebaseUser user = myAuth.getCurrentUser();
        String parentId = user.getUid();

        if (childName.isEmpty() || birthMonth.isEmpty() || birthDay.isEmpty() || birthYear.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (childType.equals("childAccount")) {
            username = usernameInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();
            confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
                return;
            }

            db.getReference("parents/" + parentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String parentEmail = snapshot.child("email").getValue(String.class);
                    String parentPassword = snapshot.child("password").getValue(String.class);
                    String childName = childNameInput.getText().toString().trim();
                    String birthMonth = birthMonthInput.getText().toString().trim();
                    String birthDay = birthDayInput.getText().toString().trim();
                    String birthYear = birthYearInput.getText().toString().trim();
                    String notes = notesInput.getText().toString().trim();
                    String parentId = user.getUid();
                    String username = usernameInput.getText().toString().trim();
                    String password = passwordInput.getText().toString().trim();
                    myAuth.signOut();

                    myAuth.createUserWithEmailAndPassword(username + "@gmail.com", password).addOnCompleteListener(task -> {
                        if(!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getContext(), "Username taken!", Toast.LENGTH_SHORT).show();
                                myAuth.signInWithEmailAndPassword(parentEmail, parentPassword);
                                return;
                            }
                        }
                        if(task.isSuccessful()) {
                            FirebaseUser user = myAuth.getCurrentUser();
                            if (user == null) return;
                            String uid = user.getUid();
                            ManageChildrenScrollableFragment child;
                            child = new ManageChildrenScrollableFragment(childName, birthMonth, birthDay, birthYear, notes, username, password, parentId);
                            db.getReference("children/").child(uid).setValue(child)
                                    .addOnCompleteListener(writeTask -> {
                                        db.getReference("children/" + uid).child("userType").setValue("children");
                                        myAuth.signOut();
                                        myAuth.signInWithEmailAndPassword(parentEmail, parentPassword);
                                        Toast.makeText(getContext(), "Child account created!", Toast.LENGTH_SHORT).show();
                                        getParentFragmentManager().popBackStack();
                                    });
                            parentRef = db.getReference("parents/" + parentId + "/childAccount");
                            parentRef.child(uid).setValue(childName);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                }
            });

        }

        if (childType.equals("childProfile")){
            String id = db.getReference("children").push().getKey();
            ManageChildrenScrollableFragment child = new ManageChildrenScrollableFragment(id, childName, birthMonth, birthDay, birthYear, notes, parentId);
            db.getReference("children").child(id).setValue(child);
            db.getReference("children/" + id).child("userType").setValue("children");
            parentRef = db.getReference("parents/" + parentId + "/childProfile");
            parentRef.child(id).setValue(childName);
            Toast.makeText(getContext(), "Child profile created!", Toast.LENGTH_SHORT).show();
        }

    }

}