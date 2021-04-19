package com.example.musicplayercool.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SongsRvModel implements Parcelable {

    Bitmap img;
    String songName;
    String songArtist;
    String data;

    public SongsRvModel (Bitmap img,String songName, String songArtist, String data) {
        this.img = img;
        this.songName = songName;
        this.songArtist = songArtist;
        this.data = data;
    }

    protected SongsRvModel(Parcel in) {
        img = in.readParcelable(Bitmap.class.getClassLoader());
        songName = in.readString();
        songArtist = in.readString();
        data = in.readString();
    }

    public static final Creator<SongsRvModel> CREATOR = new Creator<SongsRvModel>() {
        @Override
        public SongsRvModel createFromParcel(Parcel in) {
            return new SongsRvModel(in);
        }

        @Override
        public SongsRvModel[] newArray(int size) {
            return new SongsRvModel[size];
        }
    };

    public Bitmap getImg() {
        return img;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getData() {
        return data;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(img, flags);
        dest.writeString(songName);
        dest.writeString(songArtist);
        dest.writeString(data);
    }
}
