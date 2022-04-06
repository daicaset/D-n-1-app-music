package com.media.musicplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.media.musicplayer.R;
import com.media.musicplayer.model.Artist;

import java.io.IOException;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder>{
    private List<Artist> artistList;
    private Listener listener;
    private Context context;

    public ArtistAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Artist> artistList){
        this.artistList = artistList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        if (artist != null){
//            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
//            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, artist.getIdAlbum());
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), albumArtUri);
//                holder.ivImage.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            holder.tvTitle.setText(artist.getArtistName());
            holder.tvContent.setText("Album: " + artist.getNumAlbum() + " - Bài hát: "+ artist.getNumSong());
            holder.itemView.setOnClickListener(view -> {
                this.listener.onClickItem(view, position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.artistList == null ? 0 : this.artistList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView ivImage;
        private AppCompatTextView tvTitle, tvContent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);

        }
    }
}
