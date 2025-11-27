package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LogRecyclerViewFragment extends Fragment {

    private static final String ARG_LOG_TYPE = "log_type";
    private RecyclerView recyclerView;
    private LogAdapter logAdapter;
    private List<GeneralLog> Loglist;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    public LogRecyclerViewFragment(){}

    public static LogRecyclerViewFragment newInstance(String logType) {
        LogRecyclerViewFragment fragment = new LogRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOG_TYPE, logType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_item, container, false);

        recyclerView = view.findViewById(R.id.log_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Loglist = new ArrayList<>();
        LogAdapter logAdapter = new LogAdapter(Loglist);
        recyclerView.setAdapter(logAdapter);
        String currentLogType = "";
        if (getArguments() != null) {
            currentLogType = getArguments().getString(ARG_LOG_TYPE);
        }




        return view;
    }

//    private void fetchItemsFromDatabase(String category) {
//        itemsRef = db.getReference("categories/" + category);
//        itemsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                itemList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Item item = snapshot.getValue(Item.class);
//                    itemList.add(item);
//                }
//                itemAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle possible errors
//            }
//        });
//    }
}


