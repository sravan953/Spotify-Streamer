package com.biryanistudio.spotifystreamer.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.biryanistudio.spotifystreamer.R;
import com.biryanistudio.spotifystreamer.Fragment.TopTracksFragment;

/**
 * Created by Sravan on 23-May-15.
 */
public class ContainerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        if(savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(R.id.container, new TopTracksFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0)
            super.onBackPressed();
        else
            getFragmentManager().popBackStack();
    }
}
