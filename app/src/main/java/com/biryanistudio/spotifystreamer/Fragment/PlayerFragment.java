package com.biryanistudio.spotifystreamer.Fragment;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.biryanistudio.spotifystreamer.Activity.ContainerActivity;
import com.biryanistudio.spotifystreamer.DataHolder;
import com.biryanistudio.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class PlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, View.OnClickListener {
    private MediaPlayer player;
    private ImageView albumArt;
    private SeekBar seekBar;
    private TextView trackLengthText;
    private ImageButton prev;
    private ImageButton playPause;
    private ImageButton next;
    private TextView trackInfoText;
    private CountDownTimer timer;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

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
                if (fromUser) {
                    timer.cancel();
                    startTimer(30000 - (progress * 1000));
                    player.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if(!ContainerActivity.alreadyPlaying)
            doMediaPlayer(DataHolder.mediaURL);
        setDisplayInfo();

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

    private void setDisplayInfo() {
        String imageURL = DataHolder.topTracksList.get(DataHolder.current).album.images.get(0).url;
        Picasso.with(getActivity()).load(imageURL).placeholder(R.mipmap.ic_launcher).into(albumArt);
        String trackInfo = DataHolder.topTracksList.get(DataHolder.current).name;
        trackInfo += " | "+ DataHolder.topTracksList.get(DataHolder.current).album.name;
        trackInfo += " by " + DataHolder.topTracksList.get(DataHolder.current).artists.get(0).name;
        trackInfoText.setText(trackInfo);
    }

    private void startTimer(long millis) {
        timer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int millis = (int)(30000-millisUntilFinished);
                if(millisUntilFinished>20000)
                    trackLengthText.setText("00:"+TimeUnit.MILLISECONDS.toSeconds(millis) + " / 00:30");
                else
                    trackLengthText.setText("0:"+TimeUnit.MILLISECONDS.toSeconds(millis) + " / 00:30");
                seekBar.setProgress((int)TimeUnit.MILLISECONDS.toSeconds(millis));
            }

            @Override
            public void onFinish() {}
        }.start();
    }

    private void clearPlayer() {
        player.stop();
        player.release();
        player = null;
    }

    @Override
    public void onPrepared(MediaPlayer p) {
        playPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);

        startTimer(30000);

        player.start();
        player.setOnPreparedListener(null);
        ContainerActivity.alreadyPlaying = true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        seekBar.setProgress(0);
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
                timer.cancel();
                playPause.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
            }
            else {
                player.start();
                startTimer(30000 - (long)player.getCurrentPosition());
                playPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            }
        } else if(id==R.id.previous) {
            albumArt.setImageResource(R.mipmap.ic_launcher);
            trackInfoText.setText("... Loading ...");
            timer.cancel();
            seekBar.setProgress(0);
            trackLengthText.setText("00:00 / 00:30");

            clearPlayer();

            playPause.setImageResource(R.drawable.ic_play_circle_outline_grey600_48dp);
            if(DataHolder.current != 0)
                DataHolder.current--;
            else
                DataHolder.current = DataHolder.topTracksList.size()-1;
            DataHolder.mediaURL = DataHolder.topTracksList.get(DataHolder.current).preview_url;
            doMediaPlayer(DataHolder.mediaURL);
            setDisplayInfo();
        } else {
            albumArt.setImageResource(R.mipmap.ic_launcher);
            trackInfoText.setText("... Loading ...");
            timer.cancel();
            seekBar.setProgress(0);
            trackLengthText.setText("00:00 / 00:30");

            clearPlayer();

            playPause.setImageResource(R.drawable.ic_play_circle_outline_grey600_48dp);
            if(DataHolder.current < DataHolder.topTracksList.size())
                DataHolder.current++;
            else
                DataHolder.current = 0;
            DataHolder.mediaURL = DataHolder.topTracksList.get(DataHolder.current).preview_url;
            doMediaPlayer(DataHolder.mediaURL);
            setDisplayInfo();
        }
    }
}