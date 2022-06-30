package com.example.xmum_bus_app.Fragments.Booking;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.xmum_bus_app.LoginActivity;
import com.example.xmum_bus_app.R;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;

public class BookingAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Check the day of the week
        String day = LocalDate.now().getDayOfWeek().name();
        String dayName = intent.getStringExtra("day");
        // Pop up notification if the booking is on today
        if (day.equals(dayName)){
            String id = "my_channel_id";
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = manager.getNotificationChannel(id);
                if (channel == null){
                    channel = new NotificationChannel(id, "Channel Title", NotificationManager.IMPORTANCE_HIGH);
                    //configure notification channel
                    channel.setDescription("[Channel description]");
                    channel.enableVibration(true);
                    channel.setVibrationPattern(new long[]{100, 1000, 200, 340});
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    manager.createNotificationChannel(channel);
                }
            }
            Intent notificationIntent = new Intent(context, LoginActivity.class);  // Go to Login page when clicked
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
            }else {
                pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                    .setSmallIcon(R.drawable.bus_marker)
                    .setContentTitle(context.getString(R.string.bookingAlarmTitle))
                    .setContentText(context.getString(R.string.bookingAlarmContent))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{100, 1000, 200, 340})
                    .setAutoCancel(false)   // swipe to dismiss
                    .setTicker("Notification");
            builder.setContentIntent(pendingIntent);
            NotificationManagerCompat m = NotificationManagerCompat.from(context);
            //id to generate new notification in list notifications menu
            m.notify(new Random().nextInt(), builder.build());
        }
    }
}
