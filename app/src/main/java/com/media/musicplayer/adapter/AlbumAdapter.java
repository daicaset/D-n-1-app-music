package com.media.musicplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.media.musicplayer.R;
import com.media.musicplayer.data_loader.SongLoader;
import com.media.musicplayer.model.Album;
import com.media.musicplayer.model.Song;

import java.io.IOException;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>{
    private List<Album> albumList;
    private Listener listener;
    private Context context;

    public AlbumAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Album> albumList){
        this.albumList = albumList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albumList.get(position);
        if (album != null){
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, album.getId());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), albumArtUri);
                holder.ivImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            holder.tvTitle.setText(album.getAlbumName());
            holder.tvArtist.setText(album.getArtistName());
            holder.itemView.setOnClickListener(view -> {
                this.listener.onClickItem(view, position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.albumList == null ? 0 : this.albumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView ivImage;
        private AppCompatTextView tvTitle, tvArtist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
        }
    }
}
