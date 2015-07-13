package com.biryanistudio.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Sravan on 21-Jun-15.
 */
public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    public static MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DATA", "Player service started");
        try {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(DataHolder.mediaURL);
            player.prepareAsync();
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
            player.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Service.START_STICKY;
    }

    public void onDestroy() {
        player.stop();
        player.release();
        player = null;
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setOnPreparedListener(null);
        mp.start();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.sendBroadcast(new Intent("com.biryanistudio.spotifystreamer.PLAYING"));
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.sendBroadcast(new Intent("com.biryanistudio.spotifystreamer.COMPLETED"));
    }
}