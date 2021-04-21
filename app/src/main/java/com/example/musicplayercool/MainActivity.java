package com.example.musicplayercool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.musicplayercool.model.SongsRvModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ImageView playBtn, previousBtn, nextBtn;

    RecyclerView songsRv;
    SongsRvAdapter adapter;

    ArrayList<SongsRvModel> items = new ArrayList<>();
    List<String> songs = new ArrayList<String>();

    ConstraintLayout cBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;

    private MediaPlayerService player;
    boolean serviceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsRv = findViewById(R.id.songs_rv);
        playBtn = findViewById(R.id.playBtn);
        previousBtn = findViewById(R.id.previousBtn);
        nextBtn = findViewById(R.id.nextBtn);

        cBottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(cBottomSheet);


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


        while (cursor.moveToNext()) {
            songs.add(cursor.getString(0) + "||" + cursor.getString(1) + "||"
                    + cursor.getString(2) + "||" + cursor.getString(3) + "||"
                    + cursor.getString(4) + "||" + cursor.getString(5));

            if (cursor.getString(4).endsWith(".mp3") || cursor.getString(4).endsWith(".wav")) {
                items.add(new SongsRvModel(getAlbumCover(cursor), cursor.getString(2),
                        cursor.getString(1),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))));
            }

        }

        Log.d(TAG, "getAllMusic: " + songs);

        return items;
    }

    public void displaySongs() {

        adapter = new SongsRvAdapter(getAllMusic());
        songsRv.setLayoutManager(new LinearLayoutManager(this));
        songsRv.setAdapter(adapter);

        adapter.setOnItemClickListener(new SongsRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                Intent intent = new Intent(MainActivity.this, PlaySongActivity.class);
////                Intent intent = new Intent(MainActivity.this, MediaPlayerService.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("items", items.get(position));
//                intent.putExtras(bundle);
//                startActivity(intent);

                playAudio(items.get(position).getData());

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playBtn.setImageResource(R.drawable.ic_play);
                        Log.d(TAG, "onClick: here");
                    }
                });

//                MediaPlayer player = new MediaPlayer();
//                try {
//                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                    player.setDataSource(items.get(position).getData());
//                    player.prepare();
//                    player.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


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


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
    }
}