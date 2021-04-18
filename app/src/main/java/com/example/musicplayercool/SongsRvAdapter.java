package com.example.musicplayercool;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayercool.model.SongsRvModel;

import java.util.ArrayList;

public class SongsRvAdapter extends RecyclerView.Adapter<SongsRvAdapter.SongsViewHolder> {

    ArrayList<SongsRvModel> items;

    public SongsRvAdapter(ArrayList<SongsRvModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_rv_item, parent,
                false);
        return new SongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
        SongsRvModel current = items.get(position);

        holder.songImg.setImageBitmap(current.getImg());
        holder.songName.setText(current.getSongName());
        holder.songArtist.setText(current.getSongArtist());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SongsViewHolder extends RecyclerView.ViewHolder {
        ImageView songImg;
        TextView songName, songArtist;
        public SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            songImg = itemView.findViewById(R.id.song_img);
            songName = itemView.findViewById(R.id.song_name);
            songArtist = itemView.findViewById(R.id.song_artist);
        }
    }
}
