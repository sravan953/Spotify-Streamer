package com.biryanistudio.spotifystreamer.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.biryanistudio.spotifystreamer.Adapter.ArtistsCustomArrayAdapter;
import com.biryanistudio.spotifystreamer.DataHolder;
import com.biryanistudio.spotifystreamer.R;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ArtistsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private List<Artist> artistsList;
    private LocalBroadcastManager bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

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

    private void doReceiver() {
        IntentFilter filter = new IntentFilter("com.biryanistudio.spotifystreamer.ARTISTS_FETCH_DONE");
        bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(new ArtistsBroadcastReceiver(), filter);
    }

    private void doSpotify(String query) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();
        service.searchArtists(query, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                if (response.getStatus() == 200) {
                    artistsList = artistsPager.artists.items;
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
        DataHolder.artistID = artistsList.get(position).id;
        Intent i = new Intent(this, ContainerActivity.class);
        startActivity(i);
    }

    private class ArtistsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("DATA", "Received broadcast");
            if(artistsList.size() != 0) {
                ArtistsCustomArrayAdapter<Artist> adapter = new ArtistsCustomArrayAdapter<>(context, R.layout.item_list, artistsList);
                listView.setAdapter(adapter);
            }
            else
                Toast.makeText(context, "No artists found!", Toast.LENGTH_SHORT).show();
        }
    }
}