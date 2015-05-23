package com.biryanistudio.spotifystreamer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biryanistudio.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Sravan on 23-May-15.
 */
public class ArtistsCustomArrayAdapter<E> extends ArrayAdapter {
    private List<Artist> artists;
    private int resource;
    private Context context;

    public ArtistsCustomArrayAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        artists = objects;
        this.resource = resource;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        TextView artistName = (TextView)convertView.findViewById(R.id.textView);
        artistName.setText(artists.get(position).name);

        ImageView artistImage = (ImageView)convertView.findViewById(R.id.imageView);
        List<Image> images = artists.get(position).images;
        if(images.size() > 0) {
            int tryImage = images.size() - 2;
            try {
                Picasso.with(context).load(images.get(tryImage).url).placeholder(R.mipmap.ic_launcher).into(artistImage);
            } catch (Exception e) {
                Picasso.with(context).load(images.get(0).url).placeholder(R.mipmap.ic_launcher).into(artistImage);
            }
        }
        else
            Picasso.with(context).load(R.mipmap.ic_launcher).into(artistImage);
        return convertView;
    }

    public int getCount() {
        return artists.size();
    }
}
