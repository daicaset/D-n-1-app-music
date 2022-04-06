package com.media.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.appbar.MaterialToolbar;
import com.media.musicplayer.R;
import com.media.musicplayer.adapter.AlbumSongAdapter;
import com.media.musicplayer.data_loader.SongLoader;
import com.media.musicplayer.model.Artist;
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

public class ArtistSongActivity extends AppCompatActivity implements AlbumSongAdapter.Listener{
    private MaterialToolbar toolBar;
    private AppCompatImageView ivImage, ivImageSmall, ivImageBottom, ivPlayPause;
    private AppCompatTextView tvTitle, tvArtist, tvTitleBottom, tvArtistBottom;
    private RecyclerView recyclerView;
    private ProgressBar progressNormal;
    private ConstraintLayout viewControl;
    private CardView viewImageBottom;
    private long artistId;
    private AlbumSongAdapter adapter;
    private List<Song> artistSongList;
    private Artist artist;
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
        setContentView(R.layout.activity_artist_song);
        toolBar = findViewById(R.id.tool_bar);
        ivImage = findViewById(R.id.iv_image);
        ivImageSmall = findViewById(R.id.iv_image_small);
        tvTitle = findViewById(R.id.tv_title);
        tvArtist = findViewById(R.id.tv_artist);
        recyclerView = findViewById(R.id.recycler_view);
        progressNormal = findViewById(R.id.progress_normal);
        viewControl = findViewById(R.id.view_control);
        viewImageBottom = findViewById(R.id.view_image_bottom);
        ivImageBottom = findViewById(R.id.iv_image_bottom);
        tvTitleBottom = findViewById(R.id.tv_title_bottom);
        tvArtistBottom = findViewById(R.id.tv_artist_bottom);
        ivPlayPause = findViewById(R.id.iv_play_pause);

        setSupportActionBar(toolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_activity"));

        artistId = getIntent().getLongExtra("artistId", 0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AlbumSongAdapter();
        adapter.setListener(this);
        artist = SongLoader.getArtist(this, artistId);
        artistSongList = new ArrayList<>();

        appPre = AppPre.getInstance(this);

        initView();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new LoadSongs(this).execute();
        }

        ivPlayPause.setOnClickListener(v -> {
            sendActionToService(ACTION_PAUSE_RESUME);
        });

        viewControl.setOnClickListener(view -> startActivity(new Intent(this, PlayMusicActivity.class)));
    }

    class LoadSongs extends AsyncTask<Void, Void, List<Song>> {
        private Context context;

        public LoadSongs (Context context){
            this.context = context;
        }

        @Override
        protected List<Song> doInBackground(Void... voids) {
            return SongLoader.getAllArtistSongs(context, artistId);
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            artistSongList = songs;
            adapter.setData(artistSongList);
            recyclerView.setAdapter(adapter);
        }
    }

    private void initView() {
        getSupportActionBar().setTitle(artist.getArtistName());

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Song song = songListService.get(appPre.getInt("position", 0));
            loadSongInfo(song);
            setIconBtnPlayPause();
            viewControl.setVisibility(View.VISIBLE);
            progressNormal.setVisibility(View.VISIBLE);
        }

        tvTitle.setText(artist.getArtistName());
        tvArtist.setText("Album: " + artist.getNumAlbum() + " - Bài hát: "+ artist.getNumSong());
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
                ivImageBottom.setImageBitmap(bitmap);
            } else {
                ivImageBottom.setImageResource(R.drawable.ic_empty_music);
            }
            tvTitleBottom.setText(song.getTitle());
            tvArtistBottom.setText(song.getArtist());

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

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new LoadSongs(this).execute();
        }
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

    @Override
    public void onClickItem(View view, int position) {
        songListService = artistSongList;
        appPre.putInt("position", position);
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(ACTION_PLAY);
        startService(intent);
    }
}