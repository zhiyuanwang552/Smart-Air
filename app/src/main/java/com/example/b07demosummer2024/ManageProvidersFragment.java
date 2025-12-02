package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

public class ManageProvidersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProviderAdapter providerAdapter;
    private List<ProviderInstance> providerList;
    private FirebaseDatabase db;
    private DatabaseReference childRef;
    private DatabaseReference providerRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_providers, container, false);

        String identifier = null;

        if (getArguments() != null) {
            identifier = getArguments().getString("identifier");
        }
        Button returnButton = view.findViewById(R.id.returnButton);
        Button inviteCodeButton = view.findViewById(R.id.manageInviteCodesButton);
        recyclerView = view.findViewById(R.id.childrenRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        providerList = new ArrayList<>();
        providerAdapter = new ProviderAdapter(providerList, provider -> {
            ProviderPermissionsFragment PPF = new ProviderPermissionsFragment();
            Bundle args = new Bundle();
            args.putString("identifier", getArguments().getString("identifier"));
            args.putString("id", provider.getId());
            args.putString("name", provider.getProviderName());

            args.putString("notes", provider.getNotes());
            PPF.setArguments(args);
            loadFragment(PPF);
        });
        recyclerView.setAdapter(providerAdapter);

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        fetchProvidersFromDatabase(identifier);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        inviteCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageInviteCodesFragment MICF = new ManageInviteCodesFragment();
                Bundle args = new Bundle();
                args.putString("identifier", getArguments().getString("identifier"));
                MICF.setArguments(args);
                loadFragment(MICF);
            }
        });

        return view;
    }

    private void fetchProvidersFromDatabase(String identifier) {
         childRef = db.getReference("children/" + identifier + "/" + "providers");
         childRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                providerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ProviderInstance provider = snapshot.getValue(ProviderInstance.class);
                    providerList.add(provider);
                }
                providerAdapter.notifyDataSetChanged();
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
