package com.example.musicplayercool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.musicplayercool.model.SongsRvModel;

public class PlaySongActivity extends AppCompatActivity {

    private static final String TAG = "PlaySongActivity";

    TextView songName;
    ImageButton playBtn;

    SongsRvModel item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        songName = findViewById(R.id.songName);
        playBtn = findViewById(R.id.playBtn);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        item =(SongsRvModel) bundle.getParcelable("items");

        MediaPlayerService mediaPlayerService = new MediaPlayerService(item.getData());
        mediaPlayerService.initMediaPlayer();
        mediaPlayerService.playMedia();

        songName.setText(item.getSongName());
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerService.pauseMedia();
            }
        });

    }
}