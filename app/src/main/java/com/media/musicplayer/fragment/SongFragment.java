package com.media.musicplayer.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.media.musicplayer.R;
import com.media.musicplayer.activity.MainActivity;
import com.media.musicplayer.adapter.SongAdapter;
import com.media.musicplayer.data_loader.SongLoader;
import com.media.musicplayer.model.Song;
import com.media.musicplayer.service.MusicService;
import com.media.musicplayer.share_pre.AppPre;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.media.musicplayer.service.MusicService.ACTION_PLAY;
import static com.media.musicplayer.service.MusicService.songListService;

public class SongFragment extends Fragment implements SongAdapter.Listener{
    private RecyclerView recyclerView;
    private AppCompatImageView ivOption;
    private SearchView searchView;
    private SongAdapter adapter;
    private List<Song> songList;
    private AppPre appPre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        ivOption = view.findViewById(R.id.iv_option);
        searchView = view.findViewById(R.id.search_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SongAdapter();
        adapter.setListener(this);
        appPre = AppPre.getInstance(getActivity());
        initPermission();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0){
                    new LoadSongs(getActivity()).execute();
                } else {
                    String input = newText.toLowerCase();
                    List<Song> songListSearch = new ArrayList<>();
                    for (Song song : songList){
                        if (song.getTitle().toLowerCase().contains(input)){
                            songListSearch.add(song);
                        }
                    }
                    adapter.setData(songListSearch);
                    recyclerView.setAdapter(adapter);
                    songList.clear();
                    songList.addAll(songListSearch);
                }
                return true;
            }
        });

        ivOption.setOnClickListener(view1 -> {
            PopupMenu popupMenu = new PopupMenu(getActivity(), ivOption);
            popupMenu.getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.by_default:
                        appPre.putString("sorting","sortByDefault");
                        getActivity().recreate();
                        break;
                    case R.id.by_name:
                        appPre.putString("sorting","sortByName");
                        getActivity().recreate();
                        break;
                    case R.id.by_date:
                        appPre.putString("sorting","sortByDate");
                        getActivity().recreate();
                        break;
                    case R.id.by_size:
                        appPre.putString("sorting","sortBySize");
                        getActivity().recreate();
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
        return view;
    }

    private void initPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activityResult.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        } else {
            new LoadSongs(getActivity()).execute();
        }
    }

    ActivityResultLauncher<String[]> activityResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            if (result.containsKey("android.permission.WRITE_EXTERNAL_STORAGE") && result.get("android.permission.WRITE_EXTERNAL_STORAGE")) {
                new LoadSongs(getActivity()).execute();
                Toast.makeText(getActivity(), "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                activityResult.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            }
        }
    });


    class LoadSongs extends AsyncTask<Void, Void, List<Song>> {
        private Context context;

        public LoadSongs(Context context) {
            this.context = context;
        }

        @Override
        protected List<Song> doInBackground(Void... voids) {
            return SongLoader.getSongList(context);
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            songList = songs;
            if (songList.size() > 0) {
                ivOption.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
            } else{
                ivOption.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
            }
            adapter.setData(songList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onClickItem(View view, int position) {
        songListService = songList;
        appPre.putInt("position", position);
        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.setAction(ACTION_PLAY);
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new LoadSongs(getActivity()).execute();
        }
    }
}