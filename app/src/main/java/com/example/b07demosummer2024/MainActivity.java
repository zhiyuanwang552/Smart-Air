package com.example.b07demosummer2024;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://b07-demo-summer-2024-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = db.getReference("testDemo");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                Fragment selectedFragment = null;
                Bundle bundle = new Bundle();
                bundle.putString("userType", "parent");
                bundle.putString("userName", "testUser");
                bundle.putString("parentUserId", "testParent");

                int itemId = item.getItemId();

                if (itemId == R.id.navigation_profile)
                {
                    selectedFragment = new HomeFragment();
                }
                else if (itemId == R.id.navigation_record)
                {
                    selectedFragment = new LogRecyclerViewFragment();
                }
                else if (itemId == R.id.navigation_dashboard)
                {
                    selectedFragment = new InventoryMenuFragment();
                }
                else if (itemId == R.id.navigation_medicine_inventory)
                {
                    selectedFragment = new AddItemFragment();
                }
                else if (itemId == R.id.navigation_technical_guide)
                {
                    selectedFragment = new AddItemFragment();
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
//        myRef.setValue("B07 Demo!");
        myRef.child("movies").setValue("B07 Demo!");

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}