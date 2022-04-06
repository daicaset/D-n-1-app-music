package com.media.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.media.musicplayer.R;
import com.media.musicplayer.adapter.SongAdapter;
import com.media.musicplayer.model.Song;
import com.media.musicplayer.service.MusicService;
import com.media.musicplayer.share_pre.AppPre;

import java.util.List;

import static com.media.musicplayer.service.MusicService.ACTION_PLAY;
import static com.media.musicplayer.service.MusicService.songListService;

public class ListPlayActivity extends AppCompatActivity implements SongAdapter.Listener{
    private MaterialToolbar toolBar;
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private AppPre appPre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_play);
        toolBar = findViewById(R.id.tool_bar);
        recyclerView = findViewById(R.id.recycler_view);

        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Waiting List");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongAdapter();
        adapter.setListener(this);
        appPre = AppPre.getInstance(this);

        adapter.setData(songListService);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setData(songListService);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClickItem(View view, int position) {
        appPre.putInt("position", position);
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(ACTION_PLAY);
        startService(intent);
    }
}