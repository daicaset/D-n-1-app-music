package com.media.musicplayer.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.media.musicplayer.R;
import com.media.musicplayer.activity.AlbumSongActivity;
import com.media.musicplayer.adapter.AlbumAdapter;
import com.media.musicplayer.data_loader.SongLoader;
import com.media.musicplayer.model.Album;

import java.util.List;

public class AlbumFragment extends Fragment implements AlbumAdapter.Listener{
    private RecyclerView recyclerView;
    private AlbumAdapter adapter;
    private List<Album> albumList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new AlbumAdapter(getActivity());
        adapter.setListener(this);
        initPermission();
        return view;
    }

    private void initPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new LoadAlbums(getActivity()).execute();
        }
    }


    class LoadAlbums extends AsyncTask<Void, Void, List<Album>> {
        private Context context;

        public LoadAlbums (Context context){
            this.context = context;
        }

        @Override
        protected List<Album> doInBackground(Void... voids) {
            return SongLoader.getAlbumList(context);
        }

        @Override
        protected void onPostExecute(List<Album> albums) {
            super.onPostExecute(albums);
            albumList = albums;
            adapter.setData(albumList);
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
        Album album = albumList.get(position);
        Intent intent = new Intent(getActivity(), AlbumSongActivity.class);
        intent.putExtra("albumId", album.getId());
        startActivity(intent);
    }
}