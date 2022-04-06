package com.media.musicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.media.musicplayer.R;
import com.media.musicplayer.data_loader.SongLoader;
import com.media.musicplayer.fragment.ArtistFragment;
import com.media.musicplayer.fragment.AlbumFragment;
import com.media.musicplayer.fragment.SongFragment;
import com.media.musicplayer.model.Song;
import com.media.musicplayer.service.MusicService;
import com.media.musicplayer.share_pre.AppPre;
import static com.media.musicplayer.service.MusicService.ACTION_NEXT;
import static com.media.musicplayer.service.MusicService.ACTION_PAUSE_RESUME;
import static com.media.musicplayer.service.MusicService.ACTION_PLAY;
import static com.media.musicplayer.service.MusicService.ACTION_PREV;
import static com.media.musicplayer.service.MusicService.mediaPlayer;
import static com.media.musicplayer.service.MusicService.songListService;

public class MainActivity extends AppCompatActivity{
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ImageView ivImage, ivPlayPause;
    private TextView tvTitle, tvArtist;
    private ProgressBar progressNormal;
    private ConstraintLayout viewControl;
    private AppPre appPre;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        ivImage = findViewById(R.id.iv_image);
        tvTitle = findViewById(R.id.tv_title);
        tvArtist = findViewById(R.id.tv_artist);
        ivPlayPause = findViewById(R.id.iv_play_pause);
        viewControl = findViewById(R.id.view_control);
        progressNormal = findViewById(R.id.progress_normal);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Songs");
            } else if (position == 1) {
                tab.setText("Albums");
            } else {
                tab.setText("Artists");
            }

        });
        mediator.attach();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_activity"));
        appPre = AppPre.getInstance(this);
        tvTitle.setSelected(true);
        tvArtist.setSelected(true);

        ivPlayPause.setOnClickListener(v -> {
            sendActionToService(ACTION_PAUSE_RESUME);
        });

        viewControl.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PlayMusicActivity.class)));
    }

    private void initView(String action, Song song) {
        switch (action) {
            case ACTION_PLAY:
                loadSongInfo(song);
                setIconBtnPlayPause();
                initViewBottom();
                break;
            case ACTION_PAUSE_RESUME:
                loadSongInfo(song);
                setIconBtnPlayPause();
                initViewBottom();
                break;
            case ACTION_PREV:
                loadSongInfo(song);
                setIconBtnPlayPause();
                initViewBottom();
                break;
            case ACTION_NEXT:
                loadSongInfo(song);
                setIconBtnPlayPause();
                initViewBottom();
                break;
        }
    }

    private void loadSongInfo(Song song) {
        if (song != null){
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
                        progressNormal.setProgress(currentPosition);
                        progressNormal.setMax(mediaPlayer.getDuration() / 1000);
                    }
                    new Handler().postDelayed(this, 1000);
                }
            });
        }
    }

    private void initViewBottom(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            viewControl.setVisibility(View.VISIBLE);
            progressNormal.setVisibility(View.VISIBLE);
        }
    }

    private void setIconBtnPlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                ivPlayPause.setImageResource(R.drawable.ic_pause);
            } else {
                ivPlayPause.setImageResource(R.drawable.ic_play);
            }
        }
    }

    private void sendActionToService(String action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        startService(intent);
    }

    class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new SongFragment();
                case 1:
                    return new AlbumFragment();
                default:
                    return new ArtistFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Song song = songListService.get(appPre.getInt("position", 0));
            loadSongInfo(song);
            setIconBtnPlayPause();
            viewControl.setVisibility(View.VISIBLE);
            progressNormal.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}