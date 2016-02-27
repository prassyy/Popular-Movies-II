package com.prasi.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by User on 20-02-2016.
 */
public class MovieThumbnailAdapter extends ArrayAdapter<MovieDetail> {

    List<MovieDetail> movieDetailList;
    Context mContext;

    public MovieThumbnailAdapter(Context context, List<MovieDetail> movieDetailList) {
        super(context,0, movieDetailList);
        this.movieDetailList = movieDetailList;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieDetail movieDetail = getItem(position);


        convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_movie_layout,parent,false);
        ImageView movieThumbnailView = (ImageView) convertView.findViewById(R.id.grid_thumbnail);

        Picasso.with(mContext).load(movieDetail.getPosterPath()).into(movieThumbnailView);
        return convertView;
    }
}
