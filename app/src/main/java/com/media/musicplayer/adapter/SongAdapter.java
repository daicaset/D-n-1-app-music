package com.media.musicplayer.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.media.musicplayer.R;
import com.media.musicplayer.data_loader.SongLoader;
import com.media.musicplayer.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{
    private List<Song> songList;
    private Listener listener;

    public SongAdapter() {
    }

    public void setData(List<Song> songList){
        this.songList = songList;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onClickItem(View view, int position);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        if (song != null){
            byte[] img = SongLoader.getImageSong(song.getPath());
            if (img != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                holder.ivImage.setImageBitmap(bitmap);
            } else {
                holder.ivImage.setImageResource(R.drawable.ic_empty_music);
            }
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
        return this.songList == null ? 0 : this.songList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImage;
        private TextView tvTitle, tvArtist, tvDuration;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivImage = itemView.findViewById(R.id.iv_image);
            this.tvTitle = itemView.findViewById(R.id.tv_title);
            this.tvArtist = itemView.findViewById(R.id.tv_artist);
            tvDuration = itemView.findViewById(R.id.tv_duration);

        }
    }
}
