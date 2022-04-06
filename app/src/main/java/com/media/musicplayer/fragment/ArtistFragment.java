package com.media.musicplayer.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.media.musicplayer.R;
import com.media.musicplayer.activity.ArtistSongActivity;
import com.media.musicplayer.adapter.ArtistAdapter;
import com.media.musicplayer.data_loader.SongLoader;
import com.media.musicplayer.model.Artist;

import java.util.List;

public class ArtistFragment extends Fragment implements ArtistAdapter.Listener{
    private RecyclerView recyclerView;
    private ArtistAdapter adapter;
    private List<Artist> artistList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ArtistAdapter(getActivity());
        adapter.setListener(this);
        initPermission();
        return view;
    }

    private void initPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new LoadArtists(getActivity()).execute();
        }
    }

    class LoadArtists extends AsyncTask<Void, Void, List<Artist>> {
        private Context context;

        public LoadArtists (Context context){
            this.context = context;
        }

        @Override
        protected List<Artist> doInBackground(Void... voids) {
            return SongLoader.getArtistList(context);
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            super.onPostExecute(artists);
            artistList = artists;
            adapter.setData(artistList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initPermission();
    }

    @Override
    public void onClickItem(View view, int position) {
        Artist artist = artistList.get(position);
        Intent intent = new Intent(getActivity(), ArtistSongActivity.class);
        intent.putExtra("artistId", artist.getId());
        startActivity(intent);
    }
}