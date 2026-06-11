package io.github.anshu7vyas.stocked;


import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

/**
 * Class represents the whole application. Owns app-wide setup such as the notification channel.
 */
public class StockedApp extends Application {

    public static final String CHANNEL_EXPIRY = "expiry_alerts";

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_EXPIRY,
                getString(R.string.channel_expiry_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(getString(R.string.channel_expiry_description));
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }
}
