package com.example.musicplayercool.model;

import android.graphics.Bitmap;

public class SongsRvModel {
    Bitmap img;
    String songName;
    String songArtist;

    public SongsRvModel (Bitmap img,String songName, String songArtist) {
        this.img = img;
        this.songName = songName;
        this.songArtist = songArtist;
    }

    public Bitmap getImg() {
        return img;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongArtist() {
        return songArtist;
    }
}
