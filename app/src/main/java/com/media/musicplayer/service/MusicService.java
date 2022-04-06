package com.media.musicplayer.service;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.media.musicplayer.R;
import com.media.musicplayer.activity.MainActivity;
import com.media.musicplayer.broadcast_receiver.MusicReceiver;
import com.media.musicplayer.data_loader.SongLoader;
import com.media.musicplayer.model.Song;
import com.media.musicplayer.share_pre.AppPre;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.media.musicplayer.notification.ChannelNotification.CHANNEL_ID;


public class MusicService extends Service implements MediaPlayer.OnCompletionListener{
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREV = "action_prev";
    public static final String ACTION_PAUSE_RESUME = "action_pause_resume";
    public static List<Song> songListService = new ArrayList<>();
    public static MediaPlayer mediaPlayer;
    private AppPre appPre;

    @Override
    public void onCreate() {
        super.onCreate();
        appPre = AppPre.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initActionMusic(intent.getAction());
        return START_NOT_STICKY;
    }

    private void initActionMusic(String action) {
        switch (action) {
            case ACTION_PLAY:
                playMusic();
                sendNotificationMedia(R.drawable.ic_pause);
                sendActionToActivity(ACTION_PLAY);
            break;
            case ACTION_PAUSE_RESUME:
                if (mediaPlayer.isPlaying()){
                    pauseMusic();
                    sendNotificationMedia(R.drawable.ic_play);
                } else{
                    resumeMusic();
                    sendNotificationMedia(R.drawable.ic_pause);
                }
                sendActionToActivity(ACTION_PAUSE_RESUME);
                break;
            case ACTION_NEXT:
                nextMusic();
                sendNotificationMedia(R.drawable.ic_pause);
                sendActionToActivity(ACTION_NEXT);
                break;
            case ACTION_PREV:
                prevMusic();
                sendNotificationMedia(R.drawable.ic_pause);
                sendActionToActivity(ACTION_PREV);
                break;
        }
    }

    private void playMusic() {
        int position = appPre.getInt("position", 0);
        Song song = songListService.get(position);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, Uri.parse(song.getPath()));
        } else {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(song.getPath()));
        }
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.start();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void prevMusic() {
        int position = appPre.getInt("position", 0);
        position = shuffleMusic(position);
        if (appPre.getBoolean("repeat", false)){
            position = appPre.getInt("position", 0);
            appPre.putInt("position", position);
        } else{
            if (position == 0){
                position = songListService.size()-1;
                appPre.putInt("position", position);
            } else {
                position--;
                appPre.putInt("position", position);
            }
        }
        playMusic();
    }

    private void nextMusic() {
        int position = appPre.getInt("position", 0);
        position = shuffleMusic(position);
        if (appPre.getBoolean("repeat", false)){
            position = appPre.getInt("position", 0);
            appPre.putInt("position", position);
        } else{
            if (position == songListService.size()-1){
                position = 0;
                appPre.putInt("position", position);
            } else {
                position++;
                appPre.putInt("position", position);
            }
        }
        playMusic();
    }

    private int shuffleMusic(int position){
        if (appPre.getBoolean("shuffle", false) && !appPre.getBoolean("repeat", false)){
            position = new Random().nextInt(songListService.size()-1);
        }
        return position;
    }

    private void sendNotificationMedia(int playButton) {
        Song song = songListService.get(appPre.getInt("position", 0));
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, CHANNEL_ID);
        mediaSessionCompat.setActive(true);
        mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, song.getTitle())
                .putString(MediaMetadata.METADATA_KEY_ARTIST, song.getArtist())
                .build());
        byte[] img = SongLoader.getImageSong(song.getPath());
        Bitmap bitmap;
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_empty_music);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_audiotrack)
                .setLargeIcon(bitmap)
                .setContentTitle(song.getTitle())
                .setContentText(song.getArtist())
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_previous, "Prev", getPendingIntent(this, ACTION_PREV))
                .addAction(playButton, "PaseResum", getPendingIntent(this, ACTION_PAUSE_RESUME))
                .addAction(R.drawable.ic_next, "Next", getPendingIntent(this, ACTION_NEXT))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2));
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, String action){
        Intent intent = new Intent(this, MusicReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendActionToActivity(String action){
        Song song = songListService.get(appPre.getInt("position", 0));
        Intent intent = new Intent("send_data_activity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", song);
        bundle.putString("action", action);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextMusic();
        sendNotificationMedia(R.drawable.ic_pause);
        sendActionToActivity(ACTION_NEXT);
    }
}