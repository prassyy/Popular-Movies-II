package com.prasi.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prasi.popularmovies.api.MovieReview;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 13-11-2016.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder>{

    private List<MovieReview> reviews;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;

        @BindView(R.id.review_content) TextView reviewContent;
        @BindView(R.id.review_author) TextView reviewAuthor;
        @BindView(R.id.read_more) TextView readMoreIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.view = itemView;
        }
    }

    public MovieReviewsAdapter(List<MovieReview> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.reviews_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MovieReview review = reviews.get(position);

        holder.reviewAuthor.setText(mContext.getResources().getString(R.string.author,review.getReviewAuthor()));
        String reviewContent = review.getReviewContent();
        if(reviewContent.length() <= 500) {
            holder.reviewContent.setText(reviewContent);
            holder.readMoreIndicator.setVisibility(View.GONE);
        }
        else {
            holder.reviewContent.setText(reviewContent.substring(0, 499));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getReviewUrl()));
                    mContext.startActivity(browserIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
