package com.media.musicplayer.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
public class ChannelNotification extends Application {
    public static final String CHANNEL_ID = "channel_music_player";
    public static final String CHANNEL_NAME = "MediaMusicPlayer";

    @Override
    public void onCreate() {
        super.onCreate();
        onCreateChannelNofication();
    }

    private void onCreateChannelNofication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
