package com.example.b07demosummer2024;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class UserAchievementFragment extends Fragment
{
    private ProgressBar pbPlannedController;
    private ProgressBar pbTechComplete;
    private TextView tvPlannedControllerDaysValue;
    private TextView tvTechniqueCompletedDaysValue;
    private ImageView ivPrefectController;
    private ImageView ivLowRescue;
    private ImageView ivHighTech;
    private String userType;

    private List<MedicalLog> controllerList;
    private List<MedicalLog> rescueList;

    private final FirebaseDatabase db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");
    private DatabaseReference dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_user_achievement, container, false);
        pbPlannedController = view.findViewById(R.id.pbPlannedController);
        pbTechComplete = view.findViewById(R.id.pbTechComplete);
        tvPlannedControllerDaysValue = view.findViewById(R.id.tvPlannedControllerDaysValue);
        tvTechniqueCompletedDaysValue = view.findViewById(R.id.tvTechniqueCompletedDaysValue);
        ivPrefectController = view.findViewById(R.id.ivPrefectController);
        ivLowRescue = view.findViewById(R.id.ivLowRescue);
        ivHighTech = view.findViewById(R.id.ivHighTech);
        userType = getArguments().getString("userType");
        controllerList = new ArrayList<>();
        rescueList = new ArrayList<>();

        dbRef = db.getReference("parents").child(getArguments().getString("parentUserId")).child("medicalLogs");
        dbRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                controllerList.clear();
                rescueList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    MedicalLog log = snapshot.getValue(MedicalLog.class);
                    if ((log.getUserName()).equals(getArguments().getString("userName")))
                    {
                        if (log.getLinkedMedicineType().equals("Controller")) controllerList.add(log);
                        else rescueList.add(log);
                    }
                }
                setLogRelated();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });

        return view;
    }

    private int getConsecutiveDate(long[] timeStampList)
    {
        if (timeStampList == null || timeStampList.length == 0) {
            return 0;
        }

        Set<Long> uniqueDays = new HashSet<>();
        for (long timestamp : timeStampList) {
            uniqueDays.add(TimeUnit.MILLISECONDS.toDays(timestamp));
        }

        long currentDay = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());

        if (!uniqueDays.contains(currentDay)) {
            return 0;
        }

        int consecutiveCount = 1;
        long dayToCheck = currentDay - 1;
        while (uniqueDays.contains(dayToCheck)) {
            consecutiveCount++;
            dayToCheck--;
        }

        return consecutiveCount;
    }

    private int getLogCountLastMonth(long[] timeStampList)
    {
        long initialTime = System.currentTimeMillis();
        int count = 0;
        for (long currentTimeStamp : timeStampList)
        {
            if (currentTimeStamp >= initialTime - (long)24 * 60 * 60 * 1000 * 30) count++;
        }
        return count;
    }

    private int getLongestConsecutive(long[] timeStampList)
    {
        if (timeStampList == null || timeStampList.length == 0) {
            return 0;
        }

        Set<Long> uniqueDays = new HashSet<>();
        for (long timestamp : timeStampList) {
            uniqueDays.add(TimeUnit.MILLISECONDS.toDays(timestamp));
        }

        int longestStreak = 1;

        for (long day : uniqueDays) {
            if (!uniqueDays.contains(day - 1)) {
                int currentStreak = 1;
                long currentDay = day + 1;
                while (uniqueDays.contains(currentDay)) {
                    currentStreak++;
                    currentDay++;
                }
                longestStreak = Math.max(longestStreak, currentStreak);
            }
        }
        return longestStreak;
    }

    private void setLogRelated()
    {
        long[] plannedControllerTimeStampList = new long[controllerList.size()];
        long[] plannedRescueTimeStampList = new long[rescueList.size()];

        for (int i = 0; i < rescueList.size(); i++)
        {
            plannedRescueTimeStampList[i] = rescueList.get(i).getLogDate();
        }
        for (int i = 0; i < controllerList.size(); i++)
        {
            plannedControllerTimeStampList[i] = controllerList.get(i).getLogDate();
        }

        int consecutiveController = getConsecutiveDate(plannedControllerTimeStampList);
        int totalRescue = getLogCountLastMonth(plannedRescueTimeStampList);

        tvPlannedControllerDaysValue.setText(String.valueOf(consecutiveController));
        if (totalRescue <= 4)
        {
            ivLowRescue.setAlpha(1.0f);
        }
        else ivLowRescue.setAlpha(0.3f);
        if (getLongestConsecutive(plannedControllerTimeStampList) >= 7)
        {
            ivPrefectController.setAlpha(1.0f);
        }
        else ivPrefectController.setAlpha(0.3f);
        pbPlannedController.setProgress(consecutiveController);

    }


}
