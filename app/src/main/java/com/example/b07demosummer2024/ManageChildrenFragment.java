package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageChildrenFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChildrenScrollableAdapter childAdapter;
    private List<ManageChildrenScrollableFragment> childList;
    private FirebaseDatabase db;
    private DatabaseReference parentRef;
    private DatabaseReference childRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_manage_children, container, false);

        Button addChild = view.findViewById(R.id.addChildButton);
        Button removeChild = view.findViewById(R.id.button7);
        recyclerView = view.findViewById(R.id.childrenRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        childList = new ArrayList<>();
        childAdapter = new ChildrenScrollableAdapter(childList, child -> {
            ManageProvidersFragment MPF = new ManageProvidersFragment();
            Bundle args = new Bundle();
            if (child.getUsername() == null) {
                args.putString("identifier", child.getId());
            }
            else{
                args.putString("identifier", child.getUsername());
            }
            MPF.setArguments(args);
            loadFragment(MPF);
        });
        recyclerView.setAdapter(childAdapter);

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        fetchChildrenFromDatabase("childProfile");
        fetchChildrenFromDatabase("childAccount");

        addChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ManageChildrenAddFragment());
            }
        });

        removeChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ManageChildrenRemovalFragment());
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

    private void fetchChildrenFromDatabase(String childType) {
        parentRef = db.getReference("parents/genericParent/" + childType);
        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String identifier = snapshot.getRef().getKey();
                    childRef = db.getReference("children/" + identifier);
                    childRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                            ManageChildrenScrollableFragment child = datasnapshot.getValue(ManageChildrenScrollableFragment.class);
                            childList.add(child);
                            childAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle possible errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
