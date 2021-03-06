package com.biryanistudio.spotifystreamer.Fragment;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.biryanistudio.spotifystreamer.DataHolder;
import com.biryanistudio.spotifystreamer.PlayerService;
import com.biryanistudio.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;


public class PlayerDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private ImageView albumArt;
    private SeekBar seekBar;
    private TextView trackLengthText;
    private ImageButton previous;
    private ImageButton playPause;
    private ImageButton next;
    private TextView trackInfoText;
    private CountDownTimer timer;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver playingBroadcast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.biryanistudio.spotifystreamer.PLAYING");
        filter.addAction("com.biryanistudio.spotifystreamer.COMPLETED");
        broadcastManager.registerReceiver(playingBroadcast = new playingBroadcastReceiver(), filter);
        Log.i("DATA", "Registering receiver #3");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_fragment_player, container, false);

        albumArt = (ImageView)v.findViewById(R.id.imageView);
        seekBar = (SeekBar)v.findViewById(R.id.seekBar);
        trackLengthText = (TextView)v.findViewById(R.id.trackLengthText);
        previous = (ImageButton)v.findViewById(R.id.previous);
        playPause = (ImageButton)v.findViewById(R.id.play_pause);
        next = (ImageButton)v.findViewById(R.id.next);
        trackInfoText = (TextView)v.findViewById(R.id.trackInfoText);

        previous.setOnClickListener(this);
        playPause.setOnClickListener(this);
        next.setOnClickListener(this);

        setDisplayInfo();

        return v;
    }

    @Override
    public void onDestroy() {
        broadcastManager.unregisterReceiver(playingBroadcast);
        Log.i("DATA", "Unregistering receiver #3");
        super.onDestroy();
    }

    private void setDisplayInfo() {
        String imageURL = DataHolder.topTracksList.get(DataHolder.current).album.images.get(0).url;
        Picasso.with(getActivity()).load(imageURL).placeholder(R.mipmap.ic_launcher).into(albumArt);

        String trackInfo = DataHolder.topTracksList.get(DataHolder.current).name;
        trackInfo += " | "+ DataHolder.topTracksList.get(DataHolder.current).album.name;
        trackInfo += " by " + DataHolder.topTracksList.get(DataHolder.current).artists.get(0).name;
        trackInfoText.setText(trackInfo);

        seekBar.setMax(30);
        seekBar.setOnSeekBarChangeListener(this);
        if(PlayerService.player.isPlaying())
            startTimer(30000 - PlayerService.player.getCurrentPosition());
        else
            seekBar.setEnabled(false);
    }

    private void startTimer(long millisToCount) {
        timer = new CountDownTimer(millisToCount, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                long millis = 30000 - millisUntilFinished;
                int seconds = (int)TimeUnit.MILLISECONDS.toSeconds(millis);
                if(seconds > 9)
                    trackLengthText.setText("00:" + seconds + " / 00:30");
                else
                    trackLengthText.setText("00:0" + seconds + " / 00:30");
                seekBar.setProgress(seconds);
            }

            @Override
            public void onFinish() {}
        }.start();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.previous)
            btnPrev(v);
        else if(v.getId() == R.id.play_pause)
            btnPlayPause(v);
        else
            btnNext(v);
    }

    public void btnPrev(View v) {
        if(PlayerService.player.isPlaying()) {
            albumArt.setImageResource(R.mipmap.ic_launcher);
            trackInfoText.setText("... Loading ...");
            timer.cancel();
            seekBar.setProgress(0);
            trackLengthText.setText("00:00 / 00:30");

            playPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            if (DataHolder.current != 0)
                DataHolder.current--;
            else
                DataHolder.current = DataHolder.topTracksList.size() - 1;
            DataHolder.mediaURL = DataHolder.topTracksList.get(DataHolder.current).preview_url;
            setDisplayInfo();

            getActivity().stopService(DataHolder.playerServiceIntent);
            getActivity().startService(DataHolder.playerServiceIntent);
        }
    }

    public void btnPlayPause(View v) {
        if(PlayerService.player.isPlaying()) {
            PlayerService.player.pause();
            timer.cancel();
            playPause.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        }
        else {
            PlayerService.player.start();
            Log.i("DATA", ""+PlayerService.player.getCurrentPosition());
            startTimer(30000 - PlayerService.player.getCurrentPosition());
            playPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        }
    }

    public void btnNext(View v) {
        if(PlayerService.player.isPlaying()) {
            albumArt.setImageResource(R.mipmap.ic_launcher);
            trackInfoText.setText("... Loading ...");
            timer.cancel();
            seekBar.setProgress(0);
            trackLengthText.setText("00:00 / 00:30");

            playPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            if (DataHolder.current != DataHolder.topTracksList.size() - 1)
                DataHolder.current++;
            else
                DataHolder.current = 0;
            DataHolder.mediaURL = DataHolder.topTracksList.get(DataHolder.current).preview_url;
            setDisplayInfo();

            getActivity().stopService(DataHolder.playerServiceIntent);
            getActivity().startService(DataHolder.playerServiceIntent);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            timer.cancel();
            if(PlayerService.player.isPlaying())
                startTimer(30000 - (progress * 1000));
            PlayerService.player.seekTo(progress * 1000);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    public class playingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("DATA", "Received broadcast");
            if(intent.getAction() == "com.biryanistudio.spotifystreamer.PLAYING") {
                seekBar.setEnabled(true);
                startTimer(30000);
            }
            else
                startTimer(30000);
        }
    }
}