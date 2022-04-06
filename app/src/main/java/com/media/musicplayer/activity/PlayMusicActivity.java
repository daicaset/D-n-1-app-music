package com.media.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.media.musicplayer.R;
import com.media.musicplayer.data_loader.SongLoader;
import com.media.musicplayer.model.Song;
import com.media.musicplayer.service.MusicService;
import com.media.musicplayer.share_pre.AppPre;

import java.util.ArrayList;
import java.util.List;

import static com.media.musicplayer.service.MusicService.ACTION_NEXT;
import static com.media.musicplayer.service.MusicService.ACTION_PAUSE_RESUME;
import static com.media.musicplayer.service.MusicService.ACTION_PLAY;
import static com.media.musicplayer.service.MusicService.ACTION_PREV;
import static com.media.musicplayer.service.MusicService.mediaPlayer;
import static com.media.musicplayer.service.MusicService.songListService;

public class PlayMusicActivity extends AppCompatActivity {
    private MaterialToolbar toolBar;
    private AppCompatImageView ivImage, ivShuffle, ivRepeat, ivPrev, ivPlayPause, ivNext;
    private AppCompatTextView tvTitle, tvArtist, tvDurationStart, tvDurationEnd;
    private AppCompatSeekBar seekBar;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Song song = (Song) bundle.get("song");
                String action = bundle.getString("action");
                initView(action, song);
            }
        }
    };
    private AppPre appPre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        toolBar = findViewById(R.id.tool_bar);
        ivImage = findViewById(R.id.iv_image);
        tvTitle = findViewById(R.id.tv_title);
        tvArtist = findViewById(R.id.tv_artist);
        ivShuffle = findViewById(R.id.iv_shuffle);
        ivRepeat = findViewById(R.id.iv_repeat);
        seekBar = findViewById(R.id.seek_bar);
        tvDurationStart = findViewById(R.id.tv_duration_start);
        tvDurationEnd = findViewById(R.id.tv_duration_end);
        ivPrev = findViewById(R.id.iv_prev);
        ivPlayPause = findViewById(R.id.iv_play_pause);
        ivNext = findViewById(R.id.iv_next);

        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_activity"));

        appPre = AppPre.getInstance(this);
        tvTitle.setSelected(true);
        tvArtist.setSelected(true);
        Song song = songListService.get(appPre.getInt("position", 0));
        loadSongInfo(song);
        setIconBtnPlayPause();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (appPre.getBoolean("shuffle", false)){
            ivShuffle.setImageResource(R.drawable.ic_shuffle_on);
        } else{
            ivShuffle.setImageResource(R.drawable.ic_shuffle);
        }

        if (appPre.getBoolean("repeat", false)){
            ivRepeat.setImageResource(R.drawable.ic_repeat_on);
        } else{
            ivRepeat.setImageResource(R.drawable.ic_repeat);
        }

        ivShuffle.setOnClickListener(view -> {
            if (appPre.getBoolean("shuffle", false)) {
                appPre.putBoolean("shuffle", false);
                ivShuffle.setImageResource(R.drawable.ic_shuffle);
            } else {
                appPre.putBoolean("shuffle", true);
                ivShuffle.setImageResource(R.drawable.ic_shuffle_on);
            }
        });

        ivRepeat.setOnClickListener(view -> {
            if (appPre.getBoolean("repeat", false)) {
                appPre.putBoolean("repeat", false);
                ivRepeat.setImageResource(R.drawable.ic_repeat);
            } else {
                appPre.putBoolean("repeat", true);
                ivRepeat.setImageResource(R.drawable.ic_repeat_on);
            }
        });

        ivPrev.setOnClickListener(view -> {
            sendActionToService(ACTION_PREV);
        });

        ivPlayPause.setOnClickListener(view -> {
            sendActionToService(ACTION_PAUSE_RESUME);
        });

        ivNext.setOnClickListener(view -> {
            sendActionToService(ACTION_NEXT);
        });
    }

    private void initView(String action, Song song) {
        switch (action) {
            case ACTION_PLAY:
                loadSongInfo(song);
                setIconBtnPlayPause();
                break;
            case ACTION_PAUSE_RESUME:
                loadSongInfo(song);
                setIconBtnPlayPause();
                break;
            case ACTION_PREV:
                loadSongInfo(song);
                setIconBtnPlayPause();
                break;
            case ACTION_NEXT:
                loadSongInfo(song);
                setIconBtnPlayPause();
                break;
        }
    }

    private void sendActionToService(String action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        startService(intent);
    }

    private void loadSongInfo(Song song) {
        if (song != null) {
            byte[] img = SongLoader.getImageSong(song.getPath());
            if (img != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                ivImage.setImageBitmap(bitmap);
            } else {
                ivImage.setImageResource(R.drawable.ic_empty_music);
            }
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getArtist());

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(currentPosition);
                        seekBar.setMax(mediaPlayer.getDuration() / 1000);
                        tvDurationStart.setText(SongLoader.formattedTime(currentPosition));

                    }
                    new Handler().postDelayed(this, 1000);
                }
            });

            int durationEnd = Integer.parseInt(String.valueOf(song.getDuration())) / 1000;
            tvDurationEnd.setText(SongLoader.formattedTime(durationEnd));
        }
    }

    private void setIconBtnPlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                ivPlayPause.setImageResource(R.drawable.ic_pause_circle);
            } else {
                ivPlayPause.setImageResource(R.drawable.ic_play_circle);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player_list, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_list);
        menuItem.setOnMenuItemClickListener(menuItem1 -> {
            Intent intent = new Intent(this, ListPlayActivity.class);
            startActivity(intent);
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}