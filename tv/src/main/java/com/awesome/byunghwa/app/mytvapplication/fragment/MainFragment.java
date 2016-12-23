/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.awesome.byunghwa.app.mytvapplication.fragment;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.awesome.byunghwa.app.mytvapplication.R;
import com.awesome.byunghwa.app.mytvapplication.activity.BrowseErrorActivity;
import com.awesome.byunghwa.app.mytvapplication.activity.DetailsActivity;
import com.awesome.byunghwa.app.mytvapplication.data.Movie;
import com.awesome.byunghwa.app.mytvapplication.data.MoviesLoader;
import com.awesome.byunghwa.app.mytvapplication.data.UpdaterService;
import com.awesome.byunghwa.app.mytvapplication.presenter.CardPresenter;
import com.awesome.byunghwa.app.mytvapplication.util.LogUtil;
import com.awesome.byunghwa.app.mytvapplication.util.NetworkUtil;
import com.awesome.byunghwa.app.mytvapplication.util.PicassoBackgroundManagerTarget;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends BrowseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;

    private Drawable mDefaultBackground;
    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private final Handler mHandler = new Handler();
    private URI mBackgroundURI;
    Movie mMovie;
    CardPresenter mCardPresenter;

    private static final int MY_LOADER_ID_MOVIE_LIST_POPULARITY = 0;
    private static final int MY_LOADER_ID_MOVIE_LIST_VOTE_AVERAGE = 1;
    private static final int MY_LOADER_ID_MOVIE_LIST_FAVORITES = 2;

    public static final String KEY_SORT_ORDER = "com.awesome.byunghwa.app.mytvapplication.fragment.SORT_ORDER";

    public static final String VALUE_SORT_ORDER_POPULARITY = "popularity";
    public static final String VALUE_SORT_ORDER_VOTE_AVERAGE = "vote_average";
    public static final String VALUE_SORT_ORDER_FAVORITES = "favorites";

    private static final String[] sortOrderArrayList = {"Most Popular", "Top Rated", "My Favorite"};

    private ArrayObjectAdapter mRowsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.log_i(TAG, "onCreate gets called;;;");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.log_i(TAG, "onCreateView gets called;;;");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        LogUtil.log_i(TAG, "onResume gets called;;;");
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        if (getLoaderManager().getLoader(MY_LOADER_ID_MOVIE_LIST_POPULARITY) == null) {
            getLoaderManager().initLoader(MY_LOADER_ID_MOVIE_LIST_POPULARITY, null, MainFragment.this);
        } else {
            getLoaderManager().restartLoader(MY_LOADER_ID_MOVIE_LIST_POPULARITY, null, MainFragment.this);
        }
        if (getLoaderManager().getLoader(MY_LOADER_ID_MOVIE_LIST_VOTE_AVERAGE) == null) {
            getLoaderManager().initLoader(MY_LOADER_ID_MOVIE_LIST_VOTE_AVERAGE, null, MainFragment.this);
        } else {
            getLoaderManager().restartLoader(MY_LOADER_ID_MOVIE_LIST_VOTE_AVERAGE, null, MainFragment.this);
        }
        if (getLoaderManager().getLoader(MY_LOADER_ID_MOVIE_LIST_FAVORITES) == null) {
            getLoaderManager().initLoader(MY_LOADER_ID_MOVIE_LIST_FAVORITES, null, MainFragment.this);
        } else {
            getLoaderManager().restartLoader(MY_LOADER_ID_MOVIE_LIST_FAVORITES, null, MainFragment.this);
        }
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated gets called");
        super.onActivityCreated(savedInstanceState);

        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            refresh();
        }

        prepareBackgroundManager();

        setupUIElements();

        setupEventListeners();
    }

    private void refresh() {
        // fetch movie list ordered by popularity
        Intent popularityIntent = new Intent(getActivity(), UpdaterService.class);
        popularityIntent.putExtra(KEY_SORT_ORDER, VALUE_SORT_ORDER_POPULARITY);
        getActivity().startService(popularityIntent);

        // fetch movie list ordered by vote average
        Intent voteAverageIntent = new Intent(getActivity(), UpdaterService.class);
        voteAverageIntent.putExtra(KEY_SORT_ORDER, VALUE_SORT_ORDER_VOTE_AVERAGE);
        getActivity().startService(voteAverageIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    private void prepareBackgroundManager() {

        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        mDefaultBackground = getResources().getDrawable(R.drawable.default_background, null);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void setupEventListeners() {

        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                        .show();
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MY_LOADER_ID_MOVIE_LIST_POPULARITY:
                return MoviesLoader.newAllMoviesInstance(getActivity(), VALUE_SORT_ORDER_POPULARITY);
            case MY_LOADER_ID_MOVIE_LIST_VOTE_AVERAGE:
                return MoviesLoader.newAllMoviesInstance(getActivity(), VALUE_SORT_ORDER_VOTE_AVERAGE);
            case MY_LOADER_ID_MOVIE_LIST_FAVORITES:
                return MoviesLoader.newAllMoviesInstance(getActivity(), VALUE_SORT_ORDER_FAVORITES);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {
            case MY_LOADER_ID_MOVIE_LIST_POPULARITY:
                LogUtil.log_i(TAG, "onLoadFinished pop entry gets called...");
                LogUtil.log_i(TAG, "cursor id: " + cursor.toString());
                LogUtil.log_i(TAG, "cursor row count pop entry: " + cursor.getCount());

                if (cursor.moveToFirst()) {
                    LogUtil.log_i(TAG, "load pop rows gets called!");

                    mCardPresenter = new CardPresenter();

                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(mCardPresenter);

                    for (int j = 0; j < cursor.getCount(); j++) {
                        cursor.moveToPosition(j);
                        mMovie = new Movie();
                        mMovie.setTitle(cursor.getString(MoviesLoader.Query.ORIGINAL_TITLE));

                        String urlNotComplete = cursor.getString(MoviesLoader.Query.POSTER_THUMBNAIL);
                        String urlComplete = "http://image.tmdb.org/t/p/w185/" + urlNotComplete;

                        mMovie.setCardImageUrl(urlComplete);
                        mMovie.setDescription(cursor.getString(MoviesLoader.Query.OVERVIEW));
                        mMovie.setBackgroundImageUrl(urlComplete);
                        mMovie.setId(cursor.getLong(MoviesLoader.Query.MOVIE_ID));
                        listRowAdapter.add(mMovie);
                    }

                    long headerId = 0;

                    HeaderItem header = new HeaderItem(headerId, sortOrderArrayList[((int) headerId)]);
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));

                    setAdapter(mRowsAdapter);
                }
                getLoaderManager().destroyLoader(loader.getId());
                break;
            case MY_LOADER_ID_MOVIE_LIST_VOTE_AVERAGE:
                LogUtil.log_i(TAG, "onLoadFinished top rated entry gets called...");
                LogUtil.log_i(TAG, "cursor id: " + cursor.toString());
                LogUtil.log_i(TAG, "cursor row count top rated entry: " + cursor.getCount());

                if (cursor.moveToFirst()) {
                    LogUtil.log_i(TAG, "load top rated rows gets called!");

                    mCardPresenter = new CardPresenter();

                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(mCardPresenter);

                    for (int j = 0; j < cursor.getCount(); j++) {
                        cursor.moveToPosition(j);
                        mMovie = new Movie();
                        mMovie.setTitle(cursor.getString(MoviesLoader.Query.ORIGINAL_TITLE));

                        String urlNotComplete = cursor.getString(MoviesLoader.Query.POSTER_THUMBNAIL);
                        String urlComplete = "http://image.tmdb.org/t/p/w185/" + urlNotComplete;

                        mMovie.setCardImageUrl(urlComplete);
                        mMovie.setDescription(cursor.getString(MoviesLoader.Query.OVERVIEW));
                        mMovie.setBackgroundImageUrl(urlComplete);
                        mMovie.setId(cursor.getLong(MoviesLoader.Query.MOVIE_ID));
                        listRowAdapter.add(mMovie);
                    }

                    long headerId = 1;

                    HeaderItem header = new HeaderItem(headerId, sortOrderArrayList[((int) headerId)]);
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));

                    setAdapter(mRowsAdapter);
                }
                getLoaderManager().destroyLoader(loader.getId());
                break;
            case MY_LOADER_ID_MOVIE_LIST_FAVORITES:
                LogUtil.log_i(TAG, "onLoadFinished favorites entry gets called...");
                LogUtil.log_i(TAG, "cursor id: " + cursor.toString());
                LogUtil.log_i(TAG, "cursor row count fav entry: " + cursor.getCount());

                if (cursor.moveToFirst()) {
                    LogUtil.log_i(TAG, "load fav rated rows gets called!");

                    mCardPresenter = new CardPresenter();

                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(mCardPresenter);

                    for (int j = 0; j < cursor.getCount(); j++) {
                        cursor.moveToPosition(j);
                        mMovie = new Movie();
                        mMovie.setTitle(cursor.getString(MoviesLoader.Query.ORIGINAL_TITLE));

                        String urlNotComplete = cursor.getString(MoviesLoader.Query.POSTER_THUMBNAIL);
                        String urlComplete = "http://image.tmdb.org/t/p/w185/" + urlNotComplete;

                        mMovie.setCardImageUrl(urlComplete);
                        mMovie.setDescription(cursor.getString(MoviesLoader.Query.OVERVIEW));
                        mMovie.setBackgroundImageUrl(urlComplete);
                        mMovie.setId(cursor.getLong(MoviesLoader.Query.MOVIE_ID));
                        listRowAdapter.add(mMovie);
                    }

                    long headerId = 2;

                    HeaderItem header = new HeaderItem(headerId, sortOrderArrayList[((int) headerId)]);
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));

                    setAdapter(mRowsAdapter);

                }
                getLoaderManager().destroyLoader(loader.getId());
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            } else if (item instanceof String) {
                if (((String) item).contains(getString(R.string.error_fragment))) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                mBackgroundURI = ((Movie) item).getBackgroundImageURI();
                startBackgroundTimer();
            }

        }
    }

    protected void setDefaultBackground(Drawable background) {
        mDefaultBackground = background;
    }

    protected void setDefaultBackground(int resourceId) {
        mDefaultBackground = getResources().getDrawable(resourceId);
    }

    protected void updateBackground(Drawable drawable) {
        BackgroundManager.getInstance(getActivity()).setDrawable(drawable);
    }

    protected void clearBackground() {
        BackgroundManager.getInstance(getActivity()).setDrawable(mDefaultBackground);
    }

    protected void updateBackground(URI uri) {
        Picasso.with(getActivity())
                .load(uri.toString())
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .centerCrop()
                .error(mDefaultBackground)
                .into(mBackgroundTarget);
    }


    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI);
                    }
                }
            });

        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

}
