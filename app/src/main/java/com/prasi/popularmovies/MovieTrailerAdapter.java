package com.prasi.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prasi.popularmovies.api.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 12-11-2016.
 */
public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.ViewHolder> {

    private List<MovieTrailer> trailers;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;

        @BindView(R.id.trailer_thumbnail) ImageView trailerThumbnail;
        @BindView(R.id.trailer_name) TextView trailerName;

        ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    public MovieTrailerAdapter(List<MovieTrailer> trailers) {
        this.trailers = trailers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MovieTrailer trailer = trailers.get(position);
        Picasso.with(mContext).load(R.mipmap.clapboard).into(holder.trailerThumbnail);
        holder.trailerName.setText(trailer.getTrailerName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //I have made the changes here as you suggested by skipping to set the package in the Intent call..
                //That way it gives the user the choice to decide the app
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch").buildUpon()
                        .appendQueryParameter("v", trailer.getYoutubeVideoKey()).build()));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(this.trailers != null)
            return trailers.size();
        return 0;
    }

    public void setData(List<MovieTrailer> trailers) {
        this.trailers = trailers;
    }
}
