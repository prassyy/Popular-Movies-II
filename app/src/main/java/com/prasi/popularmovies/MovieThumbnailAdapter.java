package com.prasi.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieThumbnailAdapter extends RecyclerView.Adapter<MovieThumbnailAdapter.ViewHolder> {

    final String TMDB_POSTER_BASEURL = "http://image.tmdb.org/t/p/";
    final String TMDB_IMAGE_SIZE = "w185/";

    private CursorAdapter mCursorAdapter;
    private Context mContext;

    public MovieThumbnailAdapter(Context context, Cursor c, int flags) {
        this.mCursorAdapter = new CursorAdapter(context,c,flags) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View view = LayoutInflater.from(context).inflate(R.layout.grid_movie_layout, parent, false);
                ViewHolder viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ViewHolder holder = (ViewHolder) view.getTag();
                String posterPath = Uri.parse(TMDB_POSTER_BASEURL).buildUpon()
                        .appendEncodedPath(TMDB_IMAGE_SIZE)
                        .appendEncodedPath(cursor.getString(MainActivityFragment.COL_POSTER_PATH))
                        .build().toString();

                Picasso.with(mContext)
                        .load(posterPath)
                        .placeholder(R.mipmap.clapboard)
                        .error(R.mipmap.clapboard)
                        .into(holder.movieThumbnailView);
                }
        };
        this.mContext = context;
    }

    public void loadCursor(Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.grid_thumbnail) ImageView movieThumbnailView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mCursorAdapter.newView(mContext,mCursorAdapter.getCursor(),parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.movieThumbnailView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }
}
