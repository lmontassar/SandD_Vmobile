package com.example.sandd_vmobile;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.sandd_vmobile.api.ApiService;
import com.example.sandd_vmobile.controllers.UserController;
import com.example.sandd_vmobile.model.User;
import com.example.sandd_vmobile.util.UserSerializer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Handler;

public class ServiceCheckAuction extends Service {
    private UserController userController;
    private Handler handler;
    private Runnable fetchNotificationsTask;

    public ServiceCheckAuction() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        userController = new UserController(this);
        handler = new Handler();

        fetchNotificationsTask = new Runnable() {
            @Override
            public void run() {
                fetchNotifications();
                handler.postDelayed(this, 10000); // Schedule again after 1 second
            }
        };

        handler.post(fetchNotificationsTask); // Start the task
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Ensures the service keeps running
    }

    private void fetchNotifications() {
        User user = UserSerializer.loadUser(this);
        if (user == null) {
            Log.e("mylog", "User is null, stopping fetch");
            return;
        }
        Long userId = user.getId();

        userController.getNotificationsByUser(userId, new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> notifications = response.body();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // Loop through each notification and show it
                    for (int i = 0; i < notifications.size(); i++) {
                        String notificationContent = notifications.get(i);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(ServiceCheckAuction.this, "general_notifications")
                                .setSmallIcon(R.drawable.ic_gavel)
                                .setContentTitle("New Auction Notification")
                                .setContentText(notificationContent)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true);
                        int notificationId = i + 1;
                        notificationManager.notify(notificationId, builder.build());
                        Log.e("mylog", "Notification created");
                    }
                } else {
                    Log.e("mylog", "Error in API response");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("mylog", "API call failed: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i("mylog", "Service destroyed");
        if (handler != null) {
            handler.removeCallbacks(fetchNotificationsTask);
        }
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "general_notifications";
            String channelName = "General Notifications";
            String channelDescription = "Notifications for auction updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
