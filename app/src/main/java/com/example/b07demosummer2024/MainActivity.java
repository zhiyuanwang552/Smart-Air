package com.example.b07demosummer2024;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        SharedPreferences myPrefs = getSharedPreferences("local_info", MODE_PRIVATE);
        String userType = myPrefs.getString("loginType", null);
        String curr_uid = myPrefs.getString("curr_uid", null);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                handleNavigation(item.getItemId(), curr_uid);
                return true;
            }
        });

        if (savedInstanceState == null) {
            loadFragment(findProfileFragment(userType));
        }
    }

    public void handleNavigation(int itemId, String UID)
    {
        SharedPreferences myPrefs = getSharedPreferences("local_info", MODE_PRIVATE);
        String userType = myPrefs.getString("loginType", null);
        if ("parents".equals(userType) || "providers".equals(userType)) {
            Bundle bundle = new Bundle();
            bundle.putString("userType", userType);
            bundle.putString("userId", UID);
            bundle.putString("userName", UID);
            bundle.putString("parentUserId", UID);

            loadFragmentWithBundle(itemId, bundle);
        }
        else if ("children".equals(userType))
        {
            DatabaseReference dbRef= db.getReference("children").child(UID);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists()) {
                        String parentUserId = dataSnapshot.child("parentId").getValue(String.class);
                        String userName = dataSnapshot.child("childName").getValue(String.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("userType", "children");
                        bundle.putString("userId", UID);
                        bundle.putString("userName", userName);
                        bundle.putString("parentUserId", parentUserId);
                        loadFragmentWithBundle(itemId, bundle);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if ("child_profile".equals(userType))
        {
            String childProfileID = myPrefs.getString("curr_uid", null);
            DatabaseReference dbRef= db.getReference("children").child(childProfileID);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("childName").getValue(String.class);
                        String parentUserId = dataSnapshot.child("parentId").getValue(String.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("userType", "children");
                        bundle.putString("userId", childProfileID);
                        bundle.putString("userName", userName);
                        bundle.putString("parentUserId", parentUserId);
                        loadFragmentWithBundle(itemId, bundle);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void loadFragmentWithBundle(int itemId, Bundle bundle)
    {
        SharedPreferences myPrefs = getSharedPreferences("local_info", MODE_PRIVATE);
        String userType = myPrefs.getString("loginType", null);
        Fragment fragment;
        if (itemId == R.id.navigation_record)
        {
            fragment = new LogRecyclerViewFragment();
            fragment.setArguments(bundle);
        }
        else if (itemId == R.id.navigation_medicine_inventory)
        {
            if (!"parents".equals(userType))
            {
                Toast.makeText(this, "Only parents can access inventory!", Toast.LENGTH_SHORT).show();
                return;
            }
            fragment = new InventoryMenuFragment();
            fragment.setArguments(bundle);
        } else if (itemId == R.id.navigation_profile)
        {
            if ("parents".equals(userType)) {
                fragment = new ParentProfilePageFragment();
            }
            else if("children".equals(userType) || "child_profile".equals(userType))
            {
                fragment = new ChildProfilePageFragment();
            }
            else
            {
                fragment = new ProviderProfilePageFragment();
            }
        }
        else if (itemId == R.id.navigation_technical_guide)
        {
            fragment = new TechHelpFragment();
            fragment.setArguments(bundle);
        }
        else if (itemId == R.id.navigation_dashboard)
        {
            fragment = new ParentHomeActivity();
            if (!"parents".equals(userType))
            {
                Toast.makeText(this, "Only parents can access dashboard!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else
        {
            fragment = new UserAchievementFragment();
            fragment.setArguments(bundle);
            if ("parents".equals(userType))
            {
                Toast.makeText(this, "Only child can access achievement!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        loadFragment(fragment);
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}