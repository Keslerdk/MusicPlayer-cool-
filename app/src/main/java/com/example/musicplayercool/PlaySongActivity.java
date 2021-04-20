package com.example.musicplayercool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicplayercool.model.SongsRvModel;

import java.io.IOException;

public class PlaySongActivity extends AppCompatActivity {

    private static final String TAG = "PlaySongActivity";

    TextView songName;
    ImageView playBtn;

    SongsRvModel item;

    MediaPlayer mediaPlayer;
    private int resumePossition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        songName = findViewById(R.id.songName);
        playBtn = findViewById(R.id.playBtn);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        item = (SongsRvModel) bundle.getParcelable("items");

        initMediaPlayer();
        playMedia();

        songName.setText(item.getSongName());
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    pauseMedia();
                    playBtn.setImageResource(R.drawable.ic_play);
                } else {
                    resumeMedia();
                    playBtn.setImageResource(R.drawable.ic_pause);
                }
            }
        });

    }

    public void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(item.getData());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePossition = mediaPlayer.getCurrentPosition();
        }
    }

    public void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePossition);
            mediaPlayer.start();
        }
    }

}