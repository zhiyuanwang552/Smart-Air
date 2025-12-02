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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        SharedPreferences myPrefs = getSharedPreferences("local_info", MODE_PRIVATE);
        String userType = myPrefs.getString("loginType", null);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                Fragment selectedFragment = null;
                Bundle bundle = new Bundle();
                bundle.putString("userType", "parents");
                bundle.putString("userName", "testUser");
                bundle.putString("parentUserId", "testParent");

                int itemId = item.getItemId();

                if (itemId == R.id.navigation_profile)
                {
                    selectedFragment = findProfileFragment(userType);
                }
                else if (itemId == R.id.navigation_record)
                {
                    selectedFragment = new LogRecyclerViewFragment();
                }
                else if (itemId == R.id.navigation_dashboard)
                {
                    selectedFragment = new InventoryMenuFragment();
                }
                    // If a fragment was selected, load it
                if (selectedFragment != null) {
                    selectedFragment.setArguments(bundle);
                    loadFragment(selectedFragment);
                    return true;
                }

                return false;
            }
        });

        if (savedInstanceState == null) {
            loadFragment(findProfileFragment(userType));
        }
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