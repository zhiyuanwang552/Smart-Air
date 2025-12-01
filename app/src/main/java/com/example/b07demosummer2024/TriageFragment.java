package com.example.b07demosummer2024;

import static android.graphics.Color.parseColor;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;

public class TriageFragment extends Fragment {

    private FirebaseDatabase db;
    private DatabaseReference childRef;
    private CountDownTimer countDown;
    private TextView decisionCardText;
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private int currentCondition = 0;
    private int flagWeight = 0;
    private int pefWeight = 0;
    private int numRescueAttempts = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_triage, container, false);

        FirebaseUser user = myAuth.getCurrentUser();
        String childId = user.getUid();

        decisionCardText = view.findViewById(R.id.decisionCardText);
        EditText pefEntry = view.findViewById(R.id.pefEntry);
        EditText rescueAttempts = view.findViewById(R.id.rescueEntry);

        Button goodButton = view.findViewById(R.id.goodButton);
        Button okayButton = view.findViewById(R.id.okayButton);
        Button badButton = view.findViewById(R.id.badButton);
        ToggleButton difficultySpeakingButton = view.findViewById(R.id.difficultySpeakingButton);
        ToggleButton difficultyBreathingButton = view.findViewById(R.id.difficultyBreathingButton);
        ToggleButton chestIssueButton = view.findViewById(R.id.chestIssueButton);
        ToggleButton chestPainButton = view.findViewById(R.id.chestPainButton);
        ToggleButton blueGreyLipsNailsButton = view.findViewById(R.id.blueGreyLipsNailsButton);
        Button endSessionButton = view.findViewById(R.id.endSessionButton);

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        long now =  System.currentTimeMillis();
        AlertInstance triageAlert = new AlertInstance(now, "triageStart", "moderate");
        db.getReference("children/" + childId + "/alertHistory/" + now).setValue(triageAlert);

        countDown = new CountDownTimer(600000, 1000) {  // update every second
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                long now =  System.currentTimeMillis();
                AlertInstance triageEscalation = new AlertInstance(now, "triageEscalation", "severe");
                db.getReference("children/" + childId + "/alertHistory/" + now).setValue(triageEscalation);
                goodButton.setBackgroundColor(parseColor("#D7D7D7"));
                okayButton.setBackgroundColor(parseColor("#D7D7D7"));
                badButton.setBackgroundColor(parseColor("#E99F9F"));
                currentCondition = 6;
                decisionCardLogic();
            }
        }.start();

        decisionCardLogic();

        pefEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s.toString().isEmpty()) {
                        pefWeight = 0;
                        decisionCardLogic();
                    }
                    int pefValue = Integer.parseInt(s.toString());
                    String currentDate = java.time.LocalDate.now().toString();
                    childRef = db.getReference("children/" + childId + "/pefHistory/" + currentDate);
                    db.getReference("children/personalBest").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int personalBest;
                            if (dataSnapshot.getValue() == null){
                                Toast.makeText(getContext(), "No Personal Best Set", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                personalBest = Integer.parseInt(dataSnapshot.getValue(String.class));
                                if ((personalBest * 100) / pefValue >= 80){
                                    pefWeight = 0;
                                    decisionCardLogic();
                                }
                                else if ((personalBest * 100) / pefValue >= 50){
                                    pefWeight = 3;
                                    decisionCardLogic();
                                }
                                else {
                                    pefWeight = 7;
                                    decisionCardLogic();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle possible errors
                        }
                    });
                } catch (NumberFormatException e) {

                }
            }
        });

        rescueAttempts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    numRescueAttempts = 0;
                    decisionCardLogic();
                }
                try {
                    numRescueAttempts = Integer.parseInt(s.toString());
                    decisionCardLogic();
                } catch (NumberFormatException e) {

                }
            }
        });


        goodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodButton.setBackgroundColor(parseColor("#BBE7A8"));
                okayButton.setBackgroundColor(parseColor("#D7D7D7"));
                badButton.setBackgroundColor(parseColor("#D7D7D7"));
                currentCondition = 0;
                decisionCardLogic();
            }
        });

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodButton.setBackgroundColor(parseColor("#D7D7D7"));
                okayButton.setBackgroundColor(parseColor("#DEDA92"));
                badButton.setBackgroundColor(parseColor("#D7D7D7"));
                currentCondition = 3;
                decisionCardLogic();
            }
        });

        badButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now =  System.currentTimeMillis();
                AlertInstance buttonAlert = new AlertInstance(now, "badCondition", "severe");
                db.getReference("children/" + childId + "/alertHistory/" + now).setValue(buttonAlert);
                goodButton.setBackgroundColor(parseColor("#D7D7D7"));
                okayButton.setBackgroundColor(parseColor("#D7D7D7"));
                badButton.setBackgroundColor(parseColor("#E99F9F"));
                currentCondition = 6;
                decisionCardLogic();
            }
        });

        ColorStateList tintList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{-android.R.attr.state_checked}
                },
                new int[]{
                        parseColor("#D7D7D7"),
                        parseColor("#FFFFFF")
                }
        );

        difficultySpeakingButton.setBackgroundTintList(tintList);
        difficultyBreathingButton.setBackgroundTintList(tintList);
        chestIssueButton.setBackgroundTintList(tintList);
        chestPainButton.setBackgroundTintList(tintList);
        blueGreyLipsNailsButton.setBackgroundTintList(tintList);

        difficultySpeakingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    long now = System.currentTimeMillis();
                    AlertInstance flagAlert = new AlertInstance(now, "difficultySpeakingFlag", "severe");
                    db.getReference("children/" + childId + "/alertHistory/" + now).setValue(flagAlert);
                    flagWeight = flagWeight + 5;
                    decisionCardLogic();
                } else {
                    flagWeight = flagWeight - 5;
                    decisionCardLogic();
                }
            }
        });


        difficultyBreathingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    long now = System.currentTimeMillis();
                    AlertInstance flagAlert = new AlertInstance(now, "difficultyBreathingFlag", "severe");
                    db.getReference("children/" + childId + "/alertHistory/" + now).setValue(flagAlert);
                    flagWeight = flagWeight + 4;
                    decisionCardLogic();
                } else {
                    flagWeight = flagWeight - 4;
                    decisionCardLogic();
                }
            }
        });

        chestIssueButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    long now = System.currentTimeMillis();
                    AlertInstance flagAlert = new AlertInstance(now, "chestIssueFlag", "severe");
                    db.getReference("children/" + childId + "/alertHistory/" + now).setValue(flagAlert);
                    flagWeight = flagWeight + 4;
                    decisionCardLogic();
                } else {
                    flagWeight = flagWeight - 4;
                    decisionCardLogic();
                }
            }
        });

        chestPainButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    long now = System.currentTimeMillis();
                    AlertInstance flagAlert = new AlertInstance(now, "chestPainFlag", "severe");
                    db.getReference("children/" + childId + "/alertHistory/" + now).setValue(flagAlert);
                    flagWeight = flagWeight + 5;
                    decisionCardLogic();
                } else {
                    flagWeight = flagWeight - 5;
                    decisionCardLogic();
                }
            }
        });

        blueGreyLipsNailsButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    long now = System.currentTimeMillis();
                    AlertInstance flagAlert = new AlertInstance(now, "blueGreyLipsNailsFlag", "severe");
                    db.getReference("children/" + childId + "/alertHistory/" + now).setValue(flagAlert);
                    flagWeight = flagWeight + 5;
                    decisionCardLogic();
                } else {
                    flagWeight = flagWeight - 5;
                    decisionCardLogic();
                }
            }
        });

        endSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int decisionUrgency = flagWeight + currentCondition + pefWeight + numRescueAttempts*3;
                Boolean difficultySpeaking = difficultySpeakingButton.isChecked();
                Boolean difficultyBreathing = difficultyBreathingButton.isChecked();
                Boolean chestIssue = chestIssueButton.isChecked();
                Boolean chestPain = chestPainButton.isChecked();
                Boolean blueSkin = blueGreyLipsNailsButton.isChecked();
                String userResponse;
                String guidanceShown;
                if (currentCondition == 0){
                    userResponse = "good";
                }
                else if (currentCondition == 3){
                    userResponse = "okay";
                }
                else {
                    userResponse = "bad";
                }
                if (decisionUrgency < 3) {
                    guidanceShown = "noRedirection";
                }
                else if (decisionUrgency < 7) {
                    guidanceShown = "techniqueRedirection";
                }
                else {
                    guidanceShown = "emergencyRedirection";
                }
                String pefEntered = pefEntry.getText().toString().trim();
                long now =  System.currentTimeMillis();
                IncidentLogInstance incidentLog = new IncidentLogInstance(now, userResponse, guidanceShown, pefEntered, difficultySpeaking, difficultyBreathing, chestIssue, chestPain, blueSkin);
                db.getReference("children/" + childId + "/incidentLogHistory/" + now).setValue(incidentLog);
                AlertInstance triageAlert = new AlertInstance(now, "triageEnd", "moderate");
                db.getReference("children/" + childId + "/alertHistory/" + now).setValue(triageAlert);
                countDown.cancel();
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void decisionCardLogic() {
        int decisionUrgency = flagWeight + currentCondition + pefWeight + numRescueAttempts*3;
        Log.d("DEBUG", "decisionUrgency = " + decisionUrgency);
        if (decisionUrgency < 3) {
            decisionCardText.setText("Everything is okay! Enjoy your day!");
        } else if (decisionUrgency < 7) {
            decisionCardText.setText("Check out our technique helper.\nSeek help if symptoms persist for longer than 24 hours.");
        } else {
            decisionCardText.setText("Call emergency services NOW or seek emergency help!");
        }
    }

}
