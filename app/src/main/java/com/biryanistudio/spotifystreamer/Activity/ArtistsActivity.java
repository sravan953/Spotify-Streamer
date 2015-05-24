package com.biryanistudio.spotifystreamer.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.biryanistudio.spotifystreamer.Adapter.ArtistsCustomArrayAdapter;
import com.biryanistudio.spotifystreamer.DataHolder;
import com.biryanistudio.spotifystreamer.R;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ArtistsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private LocalBroadcastManager bm;
    private BroadcastReceiver artistsBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        if(savedInstanceState != null) {
            ArtistsCustomArrayAdapter<Artist> adapter = new ArtistsCustomArrayAdapter<>(this, R.layout.item_list, DataHolder.artistsList);
            listView.setAdapter(adapter);
        }

        final EditText editText = (EditText)findViewById(R.id.editText);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSpotify(editText.getText().toString());
            }
        });

        doReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bm.unregisterReceiver(artistsBroadcastReceiver);
        Log.i("DATA", "Unregistering receiver #1");
    }

    private void doReceiver() {
        IntentFilter filter = new IntentFilter("com.biryanistudio.spotifystreamer.ARTISTS_FETCH_DONE");
        bm = LocalBroadcastManager.getInstance(this);
        artistsBroadcastReceiver = new ArtistsBroadcastReceiver();
        bm.registerReceiver(artistsBroadcastReceiver, filter);
        Log.i("DATA", "Registering receiver #1");
    }

    private void doSpotify(String query) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();
        service.searchArtists(query, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                if (response.getStatus() == 200) {
                    DataHolder.artistsList = artistsPager.artists.items;
                    Intent i = new Intent("com.biryanistudio.spotifystreamer.ARTISTS_FETCH_DONE");
                    bm.sendBroadcast(i);
                    Log.i("DATA", "Sent broadcast");
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DataHolder.artistID = DataHolder.artistsList.get(position).id;
        Intent i = new Intent(this, ContainerActivity.class);
        startActivity(i);
    }

    private class ArtistsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("DATA", "Received broadcast");
            if(DataHolder.artistsList.size() != 0) {
                ArtistsCustomArrayAdapter<Artist> adapter = new ArtistsCustomArrayAdapter<>(context, R.layout.item_list, DataHolder.artistsList);
                listView.setAdapter(adapter);
            }
            else
                Toast.makeText(context, "No artists found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String shareText = "Check out Spotify Streamer on https://github.com/sravan953/Spotify-Streamer";
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(shareIntent);

        return super.onCreateOptionsMenu(menu);
    }
}