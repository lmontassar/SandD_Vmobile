package com.example.sandd_vmobile;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
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

        // add the service
        Intent intent = new Intent(getApplicationContext(),ServiceCheckAuction.class);


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
            startService(intent);
            loadFragment(new AuctionsFragment());
        } else {
            // User is not logged in, start LoginActivity
            stopService(intent);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with posting notifications
                postNotifications();
            } else {
                // Permission denied, handle the situation (e.g., show a message to the user)
                Log.e("mylog", "Permission denied to post notifications");
            }
        }
    }

    private void postNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "general_notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for general updates");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_gavel) // Replace with your app's notification icon
                .setContentTitle("Permission Granted")
                .setContentText("You have successfully granted the required permission!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // Dismiss notification when clicked

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }


}

