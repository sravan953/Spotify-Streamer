package com.biryanistudio.spotifystreamer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.biryanistudio.spotifystreamer.DataHolder;
import com.biryanistudio.spotifystreamer.Fragment.TopArtistsFragment;
import com.biryanistudio.spotifystreamer.PlayerService;
import com.biryanistudio.spotifystreamer.R;


public class ArtistsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        if(findViewById(R.id.twoPane) == null)
            DataHolder.twoPane = false;
        else
            DataHolder.twoPane = true;

        if(savedInstanceState == null)
                getFragmentManager().beginTransaction().replace(R.id.onePane, new TopArtistsFragment()).commit();

        if(getFragmentManager().findFragmentByTag("dialog") != null)
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("dialog")).commit();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PlayerService.class));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()==1)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }
}