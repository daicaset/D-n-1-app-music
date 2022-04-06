package com.media.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.media.musicplayer.R;
import com.media.musicplayer.data_loader.SongLoader;
import com.media.musicplayer.model.Song;

import java.util.List;

public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.ViewHolder> {
    private List<Song> songList;
    private Listener listener;

    public AlbumSongAdapter() {
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onClickItem(View view, int position);
    }

    public void setData(List<Song> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumSongAdapter.ViewHolder holder, int position) {
        Song song = songList.get(position);
        if (song != null) {
            holder.tvStt.setText("" + (position + 1));
            holder.tvTitle.setText(song.getTitle());
            holder.tvArtist.setText(song.getArtist());
            int duration = Integer.parseInt(String.valueOf(song.getDuration())) / 1000;
            holder.tvDuration.setText(SongLoader.formattedTime(duration));
            holder.itemView.setOnClickListener(view -> {
                this.listener.onClickItem(view, position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return songList == null ? 0 : songList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView tvStt, tvTitle, tvArtist, tvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tv_stt);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
            tvDuration = itemView.findViewById(R.id.tv_duration);
        }
    }
}
