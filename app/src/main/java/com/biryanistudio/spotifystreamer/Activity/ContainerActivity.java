package com.biryanistudio.spotifystreamer.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.FrameLayout;

import com.biryanistudio.spotifystreamer.DataHolder;
import com.biryanistudio.spotifystreamer.Fragment.TopTracksFragment;
import com.biryanistudio.spotifystreamer.R;

/**
 * Created by Sravan on 23-May-15.
 */
public class ContainerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        FrameLayout twoPane = (FrameLayout)findViewById(R.id.twoPane);
        if(twoPane == null)
            DataHolder.twoPane = false;
        else
            DataHolder.twoPane = true;

        getFragmentManager().beginTransaction().replace(R.id.onePane, new TopTracksFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0)
            super.onBackPressed();
        else
            getFragmentManager().popBackStack();
    }
}
