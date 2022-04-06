package com.media.musicplayer.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.media.musicplayer.service.MusicService;

public class MusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent intentService = new Intent(context, MusicService.class);
        intentService.setAction(action);
        context.startService(intentService);
    }
}