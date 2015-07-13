package com.biryanistudio.spotifystreamer.Fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.biryanistudio.spotifystreamer.Adapter.TopArtistsCustomArrayAdapter;
import com.biryanistudio.spotifystreamer.DataHolder;
import com.biryanistudio.spotifystreamer.R;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sravan on 17-Jun-15.
 */
public class TopArtistsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private LocalBroadcastManager bm;
    private BroadcastReceiver artistsBroadcastReceiver;
    private TopTracksFragment topTracksFragment;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        topTracksFragment = new TopTracksFragment();

        View view = inflater.from(getActivity()).inflate(R.layout.fragment_artists, container, false);
        listView = (ListView)view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        if(savedInstanceState != null) {
            TopArtistsCustomArrayAdapter<Artist> adapter = new TopArtistsCustomArrayAdapter<>(getActivity(), R.layout.item_list, DataHolder.artistsList);
            listView.setAdapter(adapter);
        }

        final EditText editText = (EditText)view.findViewById(R.id.editText);
        Button button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSpotify(editText.getText().toString());
            }
        });

        doReceiver();

        return view;
    }

    public void onStop() {
        super.onStop();
        bm.unregisterReceiver(artistsBroadcastReceiver);
        Log.i("DATA", "Unregistering receiver #1");
    }

    private void doReceiver() {
        IntentFilter filter = new IntentFilter("com.biryanistudio.spotifystreamer.ARTISTS_FETCH_DONE");
        bm = LocalBroadcastManager.getInstance(getActivity());
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
        if(!DataHolder.twoPane)
            getFragmentManager().beginTransaction().replace(R.id.onePane, topTracksFragment).addToBackStack(null).commit();
        else
            getFragmentManager().beginTransaction().replace(R.id.twoPane, topTracksFragment).addToBackStack(null).commit();
    }

    private class ArtistsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("DATA", "Received broadcast");
            if(DataHolder.artistsList.size() != 0) {
                TopArtistsCustomArrayAdapter<Artist> adapter = new TopArtistsCustomArrayAdapter<>(context, R.layout.item_list, DataHolder.artistsList);
                listView.setAdapter(adapter);
            }
            else
                Toast.makeText(context, "No artists found!", Toast.LENGTH_SHORT).show();
        }
    }
}
