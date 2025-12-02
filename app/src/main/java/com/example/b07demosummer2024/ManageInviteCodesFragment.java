package com.example.b07demosummer2024;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManageInviteCodesFragment extends Fragment {
    private FirebaseDatabase db;
    private DatabaseReference codeRef;
    private DatabaseReference childRef;
    private CountDownTimer countDown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_invite_codes, container, false);

        String identifier = null;
        if (getArguments() != null) {
            identifier = getArguments().getString("identifier");
        }

        Button returnButton = view.findViewById(R.id.returnButton);
        Button generateInviteButton = view.findViewById(R.id.generateInviteButton);
        TextView inviteCodeText = view.findViewById(R.id.inviteCodeText);
        TextView expiryText = view.findViewById(R.id.expiryText);
        Button revokeButton = view.findViewById(R.id.revokeButton);
        Button copyButton = view.findViewById(R.id.copyButton);
        CardView codeCard = view.findViewById(R.id.formCard);

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        childRef = db.getReference("children/" + identifier + "/activeCode");
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    //do things
                    codeCard.setVisibility(View.VISIBLE);
                    db.getReference("inviteCodes/" + dataSnapshot.getValue(String.class) + "/expiryDate").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                            long millis = datasnapshot.getValue(long.class);
                            String inviteCode = dataSnapshot.getValue(String.class);

                            long now =  System.currentTimeMillis();
                            long timeLeft = millis - now;
                            countDown = new CountDownTimer(timeLeft, 1000) {  // update every second
                                public void onTick(long millisUntilFinished) {
                                    long seconds = millisUntilFinished / 1000;
                                    long minutes = seconds / 60;
                                    long hours = minutes / 60;
                                    long days = hours / 24;

                                    seconds = seconds % 60;
                                    minutes = minutes % 60;
                                    hours = hours % 24;

                                    String countdownText = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
                                    expiryText.setText(countdownText);
                                }

                                public void onFinish() {
                                    expiryText.setText("Expired");
                                }
                            }.start();

                            inviteCodeText.setText(inviteCode);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //dig in database to see if child has invite code
        //if has, make block visible, set up timer+invite code text

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        generateInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inviteCodeText.getText().toString().trim().isEmpty()) {
                    countDown.cancel();
                    revokeCode(inviteCodeText.getText().toString().trim(), getArguments().getString("identifier"));
                }
                generateInviteCode(getArguments().getString("identifier"), inviteCodeText, expiryText);
                codeCard.setVisibility(View.VISIBLE);
            }
        });

        revokeButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               revokeCode(inviteCodeText.getText().toString().trim(), getArguments().getString("identifier"));
               countDown.cancel();
               codeCard.setVisibility(View.GONE);
           }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String copyText = inviteCodeText.getText().toString().trim();
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Invite Code", copyText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Invite code copied!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void generateInviteCode(String identifier, TextView inviteCodeText, TextView expiryText) {
        codeRef = db.getReference("inviteCodes/");
        String inviteCode = codeRef.push().getKey();
        //codeRef.child(inviteCode).setValue(identifier);

        Instant timestamp = Instant.now().plus(7, ChronoUnit.DAYS);
        long millis = timestamp.toEpochMilli();

        db.getReference("inviteCodes/" + inviteCode + "/childID").setValue(identifier);
        db.getReference("inviteCodes/" + inviteCode + "/expiryDate").setValue(millis);
        db.getReference("children/" + identifier + "/activeCode").setValue(inviteCode);

        inviteCodeText.setText(inviteCode);
        long now =  System.currentTimeMillis();
        long timeLeft = millis - now;

        countDown = new CountDownTimer(timeLeft, 1000) {  // update every second
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                seconds = seconds % 60;
                minutes = minutes % 60;
                hours = hours % 24;

                String countdownText = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
                expiryText.setText(countdownText);
            }

            public void onFinish() {
                expiryText.setText("Expired");
            }
        }.start();

    }


    private void revokeCode(String inviteCode, String identifier) {
        db.getReference("inviteCodes/" + inviteCode).removeValue();
        db.getReference("children/" + identifier + "/activeCode").removeValue();
    }

}
