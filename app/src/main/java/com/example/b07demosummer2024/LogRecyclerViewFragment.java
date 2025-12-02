package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LogRecyclerViewFragment extends Fragment implements DeleteListener
{
    private RecyclerView recyclerView;
    private LogAdapter logAdapter;
    private List<MedicalLog> logList;
    private Spinner spSortByName;
    private Spinner spSortLogType;
    private ArrayAdapter<String> userNameAdapter;
    private List<String> childNameList;
    private String userType;
    private Button buttonAddLog;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");
    private DatabaseReference dbRef;


    public LogRecyclerViewFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_log_recycler_view, container, false);
        userType = getArguments().getString("userType");

        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.log_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        logList = new ArrayList<>();
        logAdapter = new LogAdapter(logList, this);
        recyclerView.setAdapter(logAdapter);

        // Initialize Spinners
        spSortByName = view.findViewById(R.id.spSortByName);
        spSortLogType = view.findViewById(R.id.spSortLogType);
        setupSpinners();

        buttonAddLog = view.findViewById(R.id.btNewLog);
        buttonAddLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (userType.equals("parents")) {
                    Toast.makeText(getContext(), "You cannot add a log as a parent!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = getArguments();
                Fragment fragment = new AddNewLogFragment();
                fragment.setArguments(bundle);
                loadFragmentAdd(fragment);
            }
        });

        this.fetchLogsFromDatabase();
        return view;
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> logTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.medicine_type_array, android.R.layout.simple_spinner_item);
        logTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSortLogType.setAdapter(logTypeAdapter);

        this.childNameList = new ArrayList<>();
        if (userType.equals("parents")) this.childNameList.add("All");
        else this.childNameList.add(getArguments().getString("userName"));    //if it is child, only show his name

        this.userNameAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, childNameList);
        userNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSortByName.setAdapter(userNameAdapter);


        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                filterLogs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spSortLogType.setOnItemSelectedListener(listener);
        spSortByName.setOnItemSelectedListener(listener);
    }

    private void filterLogs()
    {
        String selectedType = spSortLogType.getSelectedItem().toString();
        String selectedUser = spSortByName.getSelectedItem().toString();
        List<MedicalLog> filteredList = new ArrayList<>();

        for (MedicalLog currentLog : logList) {
            if (currentLog.getLinkedMedicineType().equals(selectedType)) {
                if (selectedUser.equals("All") || currentLog.getUserName().equals(selectedUser)) {
                    filteredList.add(currentLog);
                }
            }
        }
        logAdapter.setLogList(filteredList);
        logAdapter.notifyDataSetChanged();
    }

    private void fetchLogsFromDatabase()
    {
        dbRef = db.getReference("parents").
                child(getArguments().getString("parentUserId")).child("medicalLogs");

        dbRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                HashSet<String> childNameSet = new HashSet<>();
                logList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MedicalLog log = snapshot.getValue(MedicalLog.class);
                    logList.add(log);
                    childNameSet.add(log.getUserName());
                }

                if (userType.equals("parents")) updateUserNameSpinner(new ArrayList<>(childNameSet));
                logAdapter.notifyDataSetChanged();
                filterLogs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void updateUserNameSpinner(List<String> newUsers)
    {
        if (childNameList != null && userNameAdapter != null)
        {
            childNameList.clear();
            childNameList.add("All");
            childNameList.addAll(newUsers);
            userNameAdapter.notifyDataSetChanged();
        }
    }

    public void onDeleteClick(String logId)
    {
        if (logId == null || logId.isEmpty()) {
            Toast.makeText(getContext(), "Error: Log ID is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference logsRef;
        logsRef = db.getReference("parents").
                child(getArguments().getString("parentUserId")).child("medicalLogs");

        logsRef.child(logId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // 删除成功后给用户一个提示
                Toast.makeText(getContext(), "Log deleted successfully.", Toast.LENGTH_SHORT).show();
                // 注意：由于您使用了 addValueEventListener，当数据库中的数据被删除后，
                // onDataChange 会自动被触发，从而刷新您的UI。所以这里通常不需要手动从logList中移除项。
            }
        }).addOnFailureListener(e -> {
            // 删除失败时给用户一个提示
            Toast.makeText(getContext(), "Failed to delete log: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadFragmentAdd(Fragment fragment)
    {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //    private void fetchSpinnerOptionFromDatabase()
//    {
//        if (accountType.equals("child")) return;
//        dbRef = db.getReference("parentAccount").child(getArguments().getString("userId"))
//                .child("linkedChild");
//        dbRef.addValueEventListener(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                List<String> newNameList = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String childName = snapshot.getKey();
//                    newNameList.add(childName);
//                }
//
//                updateUserNameSpinner(newNameList);
//                filterLogs();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle possible errors
//            }
//        });
//    }

}