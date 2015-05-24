package com.biryanistudio.spotifystreamer.Fragment;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.biryanistudio.spotifystreamer.DataHolder;
import com.biryanistudio.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class PlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, View.OnClickListener {
    private MediaPlayer player;
    private ImageView albumArt;
    private SeekBar seekBar;
    private TextView trackLengthText;
    private ImageButton prev;
    private ImageButton playPause;
    private ImageButton next;
    private TextView trackInfoText;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(R.layout.fragment_player, container, false);

        albumArt = (ImageView)view.findViewById(R.id.imageView);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
        trackLengthText = (TextView)view.findViewById(R.id.trackLengthText);
        prev = (ImageButton)view.findViewById(R.id.previous);
        playPause = (ImageButton)view.findViewById(R.id.play_pause);
        next = (ImageButton)view.findViewById(R.id.next);
        trackInfoText = (TextView)view.findViewById(R.id.trackInfoText);
        prev.setOnClickListener(this);
        playPause.setOnClickListener(this);
        next.setOnClickListener(this);

        seekBar.setMax(30);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    player.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        doMediaPlayer(DataHolder.mediaURL);

        return view;
    }

    private void doMediaPlayer(String mediaURL) {
        try {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(mediaURL);
            player.prepareAsync();
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        super.onPause();
        try {
            player.stop();
            player.release();
            player = null;
        } catch(Exception e) {}
    }

    @Override
    public void onPrepared(MediaPlayer p) {
        playPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        String imageURL = DataHolder.topTracksList.get(DataHolder.current).album.images.get(0).url;
        Picasso.with(getActivity()).load(imageURL).placeholder(R.mipmap.ic_launcher).into(albumArt);
        String trackInfo = DataHolder.topTracksList.get(DataHolder.current).name;
        trackInfo += " | "+ DataHolder.topTracksList.get(DataHolder.current).album.name;
        trackInfo += " by " + DataHolder.topTracksList.get(DataHolder.current).artists.get(0).name;
        trackInfoText.setText(trackInfo);

        final Handler mHandler = new Handler();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    seekBar.setProgress(player.getCurrentPosition() / 1000);
                    int seconds = player.getCurrentPosition() / 1000;
                    if(seconds>=10)
                        trackLengthText.setText("00:"+seconds + " / 00:30");
                    else
                        trackLengthText.setText("00:0"+seconds + " / 00:30");
                } else if (player == null) {
                    seekBar.setProgress(0);
                    trackLengthText.setText("00:00 / 00:30");
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        player.start();
        player.setOnPreparedListener(null);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        seekBar.setProgress(0);
        trackLengthText.setText("00:00 / 00:30");
        playPause.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        player.seekTo(0);
        player.setOnCompletionListener(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.play_pause) {
            if(player.isPlaying()) {
                player.pause();
                playPause.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
            }
            else {
                player.start();
                playPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            }
        } else if(id==R.id.previous) {
            albumArt.setImageResource(R.mipmap.ic_launcher);
            trackInfoText.setText("...");
            seekBar.setProgress(0);
            trackLengthText.setText("00:00 / 00:30");

            player.stop();
            player.release();
            player = null;
            playPause.setImageResource(R.drawable.ic_play_circle_outline_grey600_48dp);
            DataHolder.current--;
            DataHolder.mediaURL = DataHolder.topTracksList.get(DataHolder.current).preview_url;
            doMediaPlayer(DataHolder.mediaURL);
        } else {
            albumArt.setImageResource(R.mipmap.ic_launcher);
            trackInfoText.setText("...");
            seekBar.setProgress(0);
            trackLengthText.setText("00:00 / 00:30");

            player.stop();
            player.release();
            player = null;
            playPause.setImageResource(R.drawable.ic_play_circle_outline_grey600_48dp);
            DataHolder.current++;
            DataHolder.mediaURL = DataHolder.topTracksList.get(DataHolder.current).preview_url;
            doMediaPlayer(DataHolder.mediaURL);
        }
    }
}