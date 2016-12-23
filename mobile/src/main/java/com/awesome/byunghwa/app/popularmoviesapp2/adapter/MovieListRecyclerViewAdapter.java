/*
 * Copyright (C) 2015 Antonio Leiva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.awesome.byunghwa.app.popularmoviesapp2.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesome.byunghwa.app.popularmoviesapp2.R;
import com.awesome.byunghwa.app.popularmoviesapp2.data.PopularMoviesLoader;
import com.awesome.byunghwa.app.popularmoviesapp2.util.LogUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class MovieListRecyclerViewAdapter extends RecyclerView.Adapter<MovieListRecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = "RecyclerView Adapter";

    private Context mContext;

    private Cursor mCursor;
    private OnItemClickListener onItemClickListener;

    private String type;


    public MovieListRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(PopularMoviesLoader.Query._ID);
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LogUtil.log_i(TAG, "cursor is closed? " + mCursor.isClosed());
        if (!mCursor.isClosed()) {
            mCursor.moveToPosition(position);
            holder.text.setSelected(true); // this is required for the marquee effect
            holder.text.setText(mCursor.getString(PopularMoviesLoader.Query.ORIGINAL_TITLE));
            String urlNotComplete = mCursor.getString(PopularMoviesLoader.Query.POSTER_THUMBNAIL);
            String urlComplete = "http://image.tmdb.org/t/p/w185/" + urlNotComplete;

            // Empty strings are an error. They are usually the sign of a programming mistake or malformed
            // response. You should use something like Guava's Strings.emptyToNull if you want to allow empty
            // strings. We expect either null indicating the absence of a URL or a well-formed URI to load
            if (!urlComplete.isEmpty()) {
//                Picasso.with(holder.image.getContext()).load(urlComplete).placeholder(mContext.getResources().getDrawable(R.drawable.movie_placeholder)).into(holder.image);
                Picasso.with(holder.image.getContext()).load(urlComplete).placeholder(mContext.getResources().getDrawable(R.drawable.movie_placeholder)).into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) holder.image.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            public void onGenerated(Palette palette) {
                                holder.text.setBackgroundColor(palette.getLightMutedColor(holder.image.getContext().getResources().getColor(android.R.color.black)));
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            ViewCompat.setTransitionName(holder.image, String.valueOf(mCursor.getLong(PopularMoviesLoader.Query.MOVIE_ID)));
            LogUtil.log_i(TAG, "transition name: " + String.valueOf(mCursor.getLong(PopularMoviesLoader.Query.MOVIE_ID)));

            holder.itemView.setTag(mCursor.getLong(PopularMoviesLoader.Query.MOVIE_ID));
            LogUtil.log_i(TAG, "MovieListRecyclerViewAdapter onBindViewHolder : " + "position: " + position + ", " +
                    "original title: " + mCursor.getString(PopularMoviesLoader.Query.ORIGINAL_TITLE));
            LogUtil.log_i(TAG, "MovieListRecyclerViewAdapter ImageView Path: " + mCursor.getString(PopularMoviesLoader.Query.ORIGINAL_TITLE) + ": " + urlComplete);
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public void onClick(final View v) {
        // Give some time to the ripple to finish the effect
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.log_i(TAG, "MovieListRecyclerViewAdapter Clicked Item Id: " + v.getTag());
                    onItemClickListener.onItemClick(v, (Long) v.getTag(), getType());
                }
            }, 200);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.title_main);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, long clickedMovieId, String type);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        LogUtil.log_i(TAG, "Cursor Size: " + mCursor.getCount());
        //notifyDataSetChanged();
    }

}
