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
import android.widget.ListView;
import android.widget.Toast;

import com.biryanistudio.spotifystreamer.Adapter.TopTracksCustomArrayAdapter;
import com.biryanistudio.spotifystreamer.DataHolder;
import com.biryanistudio.spotifystreamer.R;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sravan on 23-May-15.
 */
public class TopTracksFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private LocalBroadcastManager bm;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(R.layout.fragment_top_tracks, container, false);
        listView = (ListView)view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        doReceiver();
        doSpotify(DataHolder.artistID);
        return view;
    }

    private void doReceiver() {
        IntentFilter filter = new IntentFilter("com.biryanistudio.spotifystreamer.TOP_TRACKS_FETCH_DONE");
        bm = LocalBroadcastManager.getInstance(getActivity());
        bm.registerReceiver(new TopTracksBroadcastReceiver(), filter);
    }

    private void doSpotify(String artistID) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("country", "US");

        service.getArtistTopTrack(artistID, map, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                if(response.getStatus() == 200) {
                    DataHolder.topTracksList = tracks.tracks;
                    Intent i = new Intent("com.biryanistudio.spotifystreamer.TOP_TRACKS_FETCH_DONE");
                    bm.sendBroadcast(i);
                    Log.i("DATA", "Sent broadcast");
                }
            }

            @Override
            public void failure(RetrofitError error) {}
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DataHolder.mediaURL = DataHolder.topTracksList.get(position).preview_url;
        DataHolder.current = position;
        getFragmentManager().beginTransaction().replace(R.id.container, new PlayerFragment())
                .setTransition(android.R.anim.fade_in).addToBackStack(null).commit();
        Log.i("DATA", "Media URL - " + DataHolder.mediaURL);
    }

    private class TopTracksBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("DATA", "Received broadcast");
            if(DataHolder.topTracksList.size() != 0) {
                TopTracksCustomArrayAdapter<Artist> adapter = new TopTracksCustomArrayAdapter<>(context, R.layout.item_list, DataHolder.topTracksList);
                listView.setAdapter(adapter);
            }
            else
                Toast.makeText(context, "No artists found!", Toast.LENGTH_SHORT).show();
        }
    }
}
