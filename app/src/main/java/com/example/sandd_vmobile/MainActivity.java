package com.example.sandd_vmobile;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.sandd_vmobile.fragments.AuctionsFragment;
import com.example.sandd_vmobile.fragments.SellAuctionFragment;
import com.example.sandd_vmobile.fragments.ProfileFragment;
import com.example.sandd_vmobile.model.User;
import com.example.sandd_vmobile.util.UserSerializer;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_auctions) {
                loadFragment(new AuctionsFragment());
                return true;
            } else if (itemId == R.id.navigation_sell) {
                loadFragment(new SellAuctionFragment());
                return true;
            } else if (itemId == R.id.navigation_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });

        // Check if user is logged in
        User user = UserSerializer.loadUser(this);
        if (user != null) {
            // User is logged in, load AuctionsFragment
            loadFragment(new AuctionsFragment());
        } else {
            // User is not logged in, start LoginActivity
            startActivity(new Intent(this, LogInActivity.class));
            finish();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}

