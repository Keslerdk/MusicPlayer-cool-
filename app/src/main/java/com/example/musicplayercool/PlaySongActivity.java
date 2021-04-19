package com.example.musicplayercool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.musicplayercool.model.SongsRvModel;

import java.util.ArrayList;

public class PlaySongActivity extends AppCompatActivity {

    private static final String TAG = "PlaySongActivity";

    TextView songName;

    SongsRvModel items;
    int possition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        songName = findViewById(R.id.songName);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        items =(SongsRvModel) bundle.getParcelable("items");
        Log.d(TAG, "onCreate: "+items);
        Log.d(TAG, "onCreate: "+possition+1);
        possition = bundle.getInt("possition");

        songName.setText(items.getSongName());

    }
}