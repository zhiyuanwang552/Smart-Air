package com.example.b07demosummer2024;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChildProfileSignInFragment extends Fragment {
    ArrayList<String> profiles = new ArrayList<>();;
    ArrayAdapter<String> adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_profile_signin, container, false);
        ImageButton LoginButton = view.findViewById(R.id.imageButton14);
        ImageButton BackButton = view.findViewById(R.id.imageButton15);
        Spinner spinner = view.findViewById(R.id.spinner);
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                profiles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        LoadProfileFromDatabase();


        SharedPreferences myPrefs = requireContext().getSharedPreferences("local_info", Context.MODE_PRIVATE);
        String userType = myPrefs.getString("loginType", null);

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(findProfileFragment(userType));
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedProfile = spinner.getSelectedItem().toString();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("loginType","child_profile").apply();
                DatabaseReference parentref = FirebaseDatabase.getInstance().
                        getReference("parents").child(uid).child("childProfile");
                parentref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot child : snapshot.getChildren()){
                            if(child.getValue(String.class).equals(selectedProfile)){
                                String child_uid = child.getKey();
                                editor.putString("curr_uid",child_uid).apply();
                                goToMainPage();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}

                });

            }
        });

        return view;
    }
    public void LoadProfileFromDatabase(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //TODO make sure the child profile reference is right
        DatabaseReference parentref = FirebaseDatabase.getInstance().getReference("parents").child(uid).child("childProfile");
        parentref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                profiles.clear();

                for(DataSnapshot child : snapshot.getChildren()){
                    profiles.add(child.getValue(String.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public Fragment findProfileFragment(String userType){
        switch (userType) {
            case "child_profile":
                return new ChildProfilePageFragment();
            case "parents":
                return new ParentProfilePageFragment();
            case "children":
                return new ChildProfilePageFragment();
            case "providers":
                return new ProviderProfilePageFragment();
            default:
                return null;
        }
    }
    public void goToMainPage(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
