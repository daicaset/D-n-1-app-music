package com.media.musicplayer.data_loader;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import com.media.musicplayer.model.Album;
import com.media.musicplayer.model.Artist;
import com.media.musicplayer.model.Song;
import com.media.musicplayer.share_pre.AppPre;

import java.util.ArrayList;
import java.util.List;

public class SongLoader {
    public static List<Song> getSongList(Context context) {
        AppPre appPre = AppPre.getInstance(context);
        List<Song> songList = new ArrayList<>();
        String[] projection = {MediaStore.Audio.Genres.Members._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST};
        String sortOder = appPre.getString("sorting","sortByDefault");
        String order = null;
        switch (sortOder){
            case "sortByDefault":
                order = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                break;
            case "sortByName":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " asc";
                break;
            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED + " asc";
                break;
            case "sortBySize":
                order = MediaStore.MediaColumns.SIZE+" DESC";
                break;
        }
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, order);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setId(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Genres.Members._ID))));
                song.setPath(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DATA))));
                song.setTitle(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));
                song.setDuration(cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                song.setAlbum(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))));
                song.setArtist(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))));
                songList.add(song);
            }
            cursor.close();
        }
        return songList;
    }

    public static List<Album> getAlbumList(Context context) {
        List<Album> albumList = new ArrayList<>();
        String[] projection = new String[]{MediaStore.Audio.AlbumColumns.ALBUM_ID,
                MediaStore.Audio.AlbumColumns.ALBUM,
                MediaStore.Audio.AlbumColumns.ARTIST_ID,
                MediaStore.Audio.AlbumColumns.ARTIST,
                MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS,
                MediaStore.Audio.AlbumColumns.FIRST_YEAR};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Album album = new Album();
                album.setId(cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ID))));
                album.setAlbumName(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM))));
                album.setArtistId(cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST_ID))));
                album.setArtistName(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST))));
                album.setNumSong(cursor.getInt((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS))));
                album.setYear(cursor.getInt((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR))));
                albumList.add(album);
            }
            cursor.close();
        }
        return albumList;
    }

    public static Album getAlbum(Context context, long idd) {
        Album album = new Album();
        String[] projection = new String[]{MediaStore.Audio.AlbumColumns.ALBUM_ID,
                MediaStore.Audio.AlbumColumns.ALBUM,
                MediaStore.Audio.AlbumColumns.ARTIST_ID,
                MediaStore.Audio.AlbumColumns.ARTIST,
                MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS,
                MediaStore.Audio.AlbumColumns.FIRST_YEAR};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, "_id=?", new String[]{String.valueOf(idd)}, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                album.setId(cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ID))));
                album.setAlbumName(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM))));
                album.setArtistId(cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST_ID))));
                album.setArtistName(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST))));
                album.setNumSong(cursor.getInt((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS))));
                album.setYear(cursor.getInt((cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR))));
            }
            cursor.close();
        }
        return album;
    }

    public static List<Song> getAllAlbumSongs(Context context, long id) {
        List<Song> songList = new ArrayList<>();
        String[] projection = {MediaStore.Audio.Genres.Members._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST};
        String selection = "is_music=1 and title !='' and album_id=" + id;
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setId(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Genres.Members._ID))));
                song.setPath(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DATA))));
                song.setTitle(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));
                song.setDuration(cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                song.setAlbum(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))));
                song.setArtist(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))));
                songList.add(song);
            }
            cursor.close();
        }
        return songList;
    }

    public static List<Artist> getArtistList(Context context) {
        List<Artist> artistList = new ArrayList<>();
        String[] projection = new String[]{MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS,
                MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Artist artist = new Artist();
                artist.setId(cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID))));
                artist.setArtistName(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST))));
                artist.setNumAlbum(cursor.getInt((cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS))));
                artist.setNumSong(cursor.getInt((cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS))));
                artistList.add(artist);
            }
            cursor.close();
        }

        return artistList;
    }

    public static Artist getArtist(Context context, long id) {
        Artist artist = new Artist();
        String[] projection = new String[]{MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS,
                MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, "_id=?", new String[]{String.valueOf(id)}, MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                artist.setId(cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID))));
                artist.setArtistName(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST))));
                artist.setNumAlbum(cursor.getInt((cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS))));
                artist.setNumSong(cursor.getInt((cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS))));
            }
            cursor.close();
        }
        return artist;
    }

    public static List<Song> getAllArtistSongs(Context context, long id){
        List<Song> songList = new ArrayList<>();
        String[] projection = {MediaStore.Audio.Genres.Members._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST};
        String selection = "is_music=1 and title !='' and artist_id="+id;
        Cursor cursor =context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setId(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Genres.Members._ID))));
                song.setPath(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DATA))));
                song.setTitle(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));
                song.setDuration(cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                song.setAlbum(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))));
                song.setArtist(cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))));
                songList.add(song);
            }
            cursor.close();
        }
        return songList;
    }

    public static byte[] getImageSong(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] img = retriever.getEmbeddedPicture();
        retriever.release();
        return img;
    }

    public static String formattedTime(int duration) {
        String secconds = String.valueOf(duration % 60);
        String minutes = String.valueOf(duration / 60);
        String totalOut = minutes + ":" + secconds;
        String totalNew = minutes + ":" + "0" + secconds;
        if (secconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }
}
