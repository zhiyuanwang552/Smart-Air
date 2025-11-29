package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProviderCodeEntryFragment extends Fragment {

    private FirebaseDatabase db;
    private DatabaseReference codeRef;
    private DatabaseReference childIdRef;
    private DatabaseReference childRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_children_add, container, false);

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        Button returnButton = view.findViewById(R.id.returnButton);
        Button addConnectionButton = view.findViewById(R.id.addConnectionButton);
        EditText codeInput = view.findViewById(R.id.codeInput);

        addConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeEntered = codeInput.getText().toString().trim();
                attemptConnection(codeEntered);

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

    private void attemptConnection(String codeEntered) {
        codeRef = db.getReference("inviteCodes/" + codeEntered + "/expiryDate");
        codeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(long.class) != null) {
                    long millis = dataSnapshot.getValue(long.class);
                    long now =  System.currentTimeMillis();
                    if (millis > now) {
                        childIdRef = db.getReference("inviteCodes/" + codeEntered + "/childID");
                        childIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                String childIdentifier = datasnapshot.getValue(String.class);
                                childRef = db.getReference("children/" + childIdentifier + "/childName");
                                childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapShot) {
                                        String childName = dataSnapShot.getValue(String.class);
                                        ProviderInstance provider = new ProviderInstance("genericID", "providerABC");
                                        db.getReference("providers/" + "genericID" + "/connections").child(childIdentifier).setValue(childName);
                                        db.getReference("children/" + childIdentifier + "providers/" + "genericID").setValue(provider);
                                        getParentFragmentManager().popBackStack();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle possible errors
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle possible errors
                            }
                        });
                    }
                    else {
                        Toast.makeText(getContext(), "Code expired!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Code does not exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
