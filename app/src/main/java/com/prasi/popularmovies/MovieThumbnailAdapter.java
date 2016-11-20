package com.prasi.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieThumbnailAdapter extends RecyclerView.Adapter<MovieThumbnailAdapter.ViewHolder> {

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_POSTER_PATH = 1;

    private CursorAdapter mCursorAdapter;
    private Context mContext;
    private CallBack activity;

    public MovieThumbnailAdapter(CallBack callingActivity, Context context, Cursor c, int flags) {
        this.mCursorAdapter = new CursorAdapter(context,c,flags) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.movies_list_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                view.setTag(cursor.getLong(COL_MOVIE_ID));
                ViewHolder holder = new ViewHolder(view);
                Utility.loadImageInto(context, cursor.getString(COL_POSTER_PATH), null,holder.movieThumbnailView);
            }
        };
        this.mContext = context;
        this.activity = callingActivity;
    }

    public void loadCursor(Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.grid_thumbnail) ImageView movieThumbnailView;

        public View holderView;

        public ViewHolder(View view) {
            super(view);
            holderView = view;
            ButterKnife.bind(this,view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mCursorAdapter.newView(mContext,mCursorAdapter.getCursor(),parent);
        view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                long movieId = (long)v.getTag();
                MovieThumbnailAdapter.this.activity.onItemSelected(movieId);
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.holderView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }
}
