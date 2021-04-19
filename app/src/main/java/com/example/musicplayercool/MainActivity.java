package com.example.musicplayercool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.example.musicplayercool.model.SongsRvModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    RecyclerView songsRv;
    SongsRvAdapter adapter;

    ArrayList<SongsRvModel> items = new ArrayList<>();
    List<String> songs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsRv = findViewById(R.id.songs_rv);

        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();


    }

    public ArrayList<SongsRvModel> getAllMusic() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Log.d(TAG, "getAllMusic: " + selection);
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);



        ArrayList<String> songsNames= new ArrayList<>();

        while (cursor.moveToNext()) {
            songs.add(cursor.getString(0) + "||" + cursor.getString(1) + "||"
                    + cursor.getString(2) + "||" + cursor.getString(3) + "||"
                    + cursor.getString(4) + "||" + cursor.getString(5));

            if (cursor.getString(4).endsWith(".mp3") || cursor.getString(4).endsWith(".wav")) {
                items.add(new SongsRvModel(getAlbumCover(cursor), cursor.getString(2),
                        cursor.getString(1)));
                songsNames.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                Log.d(TAG, "getAllMusic: "+cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            }

        }

        Log.d(TAG, "getAllMusic: " + songs);

        MediaPlayer player = new MediaPlayer();
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(songsNames.get(0));
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void displaySongs() {

        adapter = new SongsRvAdapter(getAllMusic());
        songsRv.setLayoutManager(new LinearLayoutManager(this));
        songsRv.setAdapter(adapter);

        adapter.setOnItemClickListener(new SongsRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(MainActivity.this, PlaySongActivity.class);
                startActivity(intent);


            }
        });
    }

    public Bitmap getAlbumCover(Cursor cursor) {
        int posColId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
        long songId = cursor.getLong(posColId);
        Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
        String[] dataColumn = {MediaStore.Audio.Media.DATA};
        Cursor coverCursor = getContentResolver().query(songUri, dataColumn, null,
                null, null);
        coverCursor.moveToFirst();
        int dataIndex = coverCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        String filePath = coverCursor.getString(dataIndex);
        coverCursor.close();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        byte[] coverBytes = retriever.getEmbeddedPicture();
        Bitmap songCover;
        if (coverBytes != null)
            songCover = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
        else
            songCover = null;

        return songCover;
    }

}