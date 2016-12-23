package com.awesome.byunghwa.app.mytvapplication.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.awesome.byunghwa.app.mytvapplication.R;
import com.awesome.byunghwa.app.mytvapplication.activity.DetailsActivity;
import com.awesome.byunghwa.app.mytvapplication.activity.PlaybackOverlayActivity;
import com.awesome.byunghwa.app.mytvapplication.data.ItemsContract;
import com.awesome.byunghwa.app.mytvapplication.data.Movie;
import com.awesome.byunghwa.app.mytvapplication.presenter.CardPresenter;
import com.awesome.byunghwa.app.mytvapplication.presenter.DetailsDescriptionPresenter;
import com.awesome.byunghwa.app.mytvapplication.util.LogUtil;
import com.awesome.byunghwa.app.mytvapplication.util.PicassoBackgroundManagerTarget;
import com.awesome.byunghwa.app.mytvapplication.util.RemoteEndpointUtil;
import com.awesome.byunghwa.app.mytvapplication.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoDetailsFragment extends DetailsFragment {
    private static final String TAG = "VideoDetailsFragment";

    //private static final int ACTION_WATCH_TRAILER = 1;
    private static final int ACTION_SAVE_TO_FAVORITE = 1;
    private static final int ACTION_REMOVE_FROM_FAVORITE = 2;
    private static final int ACTION_RENT = 3;
    private static final int ACTION_BUY = 4;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private static final int MY_LOADER_ID_MOVIE_DETAIL = 0;

    private static final String MOVIE = "Movie";

    private Movie mSelectedMovie;

    private Drawable mDefaultBackground;
    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;
    private DetailsOverviewRowPresenter mDorPresenter;
    private DetailRowBuilderTask mDetailRowBuilderTask;

    private ArrayList<String> trailerNameList;
    private ArrayObjectAdapter adapter;

    private boolean liked;
    private DetailsOverviewRow row;
    private Action favoriteAction;
    private Action removeFromFavoriteAction;

    //private long clickedMovieId;

    //https://img.youtube.com/vi/VIDEO_ID/default.jpg

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        mSelectedMovie = (Movie) getActivity().getIntent().getSerializableExtra(MOVIE);
        //clickedMovieId = getActivity().getIntent().getLongExtra("clicked_id", 0);

        if (mSelectedMovie.getId() > 0) {
            // first contact content provider to see if this movie has already been favorited. if yes, then set fab color to pressed
            CheckIfAlreadyFavoritedAsyncTask checkIfAlreadyFavoritedAsyncTask = new CheckIfAlreadyFavoritedAsyncTask();
            checkIfAlreadyFavoritedAsyncTask.execute(mSelectedMovie.getId());
        }
//        if (clickedMovieId > 0) {
//            // first contact content provider to see if this movie has already been favorited. if yes, then set fab color to pressed
//            CheckIfAlreadyFavoritedAsyncTask checkIfAlreadyFavoritedAsyncTask = new CheckIfAlreadyFavoritedAsyncTask();
//            checkIfAlreadyFavoritedAsyncTask.execute(clickedMovieId);
//        }

        // init loader
        //getLoaderManager().initLoader(MY_LOADER_ID_MOVIE_DETAIL, null, this);

        mDorPresenter =
                new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter());

        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        mDetailRowBuilderTask = (DetailRowBuilderTask) new DetailRowBuilderTask().execute(mSelectedMovie);
        mDorPresenter.setSharedElementEnterTransition(getActivity(),
                DetailsActivity.SHARED_ELEMENT_NAME);

        updateBackground(mSelectedMovie.getBackgroundImageURI());
        setOnItemViewClickedListener(new ItemViewClickedListener());

        fetchTrailorLists(mSelectedMovie.getId());
    }

    @Override
    public void onStop() {
        mDetailRowBuilderTask.cancel(true);
        super.onStop();
    }

    private void fetchTrailorLists(long clickedId) {
        // start an asynctask to fetch trailor list from server
        FetchTrailerListAsyncTask fetchTrailerListAsyncTask = new FetchTrailerListAsyncTask();
        fetchTrailerListAsyncTask.execute(clickedId);
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return MoviesLoader.newInstanceForItemId(getActivity(), null, 0);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mDetailRowBuilderTask = (DetailRowBuilderTask) new DetailRowBuilderTask().execute(mSelectedMovie);
//        mDorPresenter.setSharedElementEnterTransition(getActivity(),
//                DetailsActivity.SHARED_ELEMENT_NAME);
//
//        updateBackground(mSelectedMovie.getBackgroundImageURI());
//        setOnItemViewClickedListener(new ItemViewClickedListener());
//
//        fetchTrailorLists(mSelectedMovie.getId());
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }

    private class DetailRowBuilderTask extends AsyncTask<Movie, Integer, DetailsOverviewRow> {
        @Override
        protected DetailsOverviewRow doInBackground(Movie... movies) {
            mSelectedMovie = movies[0];

            row = new DetailsOverviewRow(mSelectedMovie);
            try {
                Bitmap poster = Picasso.with(getActivity())
                        .load(mSelectedMovie.getCardImageUrl())
                        .resize(Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH),
                                Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT))
                        .centerCrop()
                        .get();
                row.setImageBitmap(getActivity(), poster);
            } catch (IOException e) {
            }

            if (!liked) {
                favoriteAction = new Action(ACTION_SAVE_TO_FAVORITE, getResources().getString(
                        R.string.save_to_favorite_1), getResources().getString(R.string.save_to_favorite_2));
                row.addAction(favoriteAction);
            } else {
                removeFromFavoriteAction = new Action(ACTION_REMOVE_FROM_FAVORITE, getResources().getString(
                        R.string.remove_from_favorite_1), getResources().getString(R.string.remove_from_favorite_2));
                row.addAction(removeFromFavoriteAction);
            }
            row.addAction(new Action(ACTION_RENT, getResources().getString(R.string.rent_1),
                    getResources().getString(R.string.rent_2)));
            row.addAction(new Action(ACTION_BUY, getResources().getString(R.string.buy_1),
                    getResources().getString(R.string.buy_2)));

            return row;
        }

        @Override
        protected void onPostExecute(DetailsOverviewRow detailRow) {
            ClassPresenterSelector ps = new ClassPresenterSelector();
            // set detail background and style
            mDorPresenter.setBackgroundColor(getResources().getColor(R.color.detail_background));
            mDorPresenter.setStyleLarge(true);
            mDorPresenter.setOnActionClickedListener(new OnActionClickedListener() {
                @Override
                public void onActionClicked(Action action) {
                    if (action.getId() == ACTION_SAVE_TO_FAVORITE) {
                        favorites();
                    }else if (action.getId() == ACTION_REMOVE_FROM_FAVORITE) {
                        favorites();
                    } else{
                        Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ps.addClassPresenter(DetailsOverviewRow.class, mDorPresenter);
            ps.addClassPresenter(ListRow.class,
                    new ListRowPresenter());

            adapter = new ArrayObjectAdapter(ps);
            adapter.add(detailRow);
        }

    }

//    private class DetailRowBuilderTask extends AsyncTask<Cursor, Integer, DetailsOverviewRow> {
//
//        Cursor cursor;
//
//        @Override
//        protected DetailsOverviewRow doInBackground(Cursor... cursors) {
//            cursor = cursors[0];
//
//            row = new DetailsOverviewRow(null);
//            try {
//                Bitmap poster = Picasso.with(getActivity())
//                        .load(cursor.getString(cursor.getColumnIndex()))
//                        .resize(Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH),
//                                Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT))
//                        .centerCrop()
//                        .get();
//                row.setImageBitmap(getActivity(), poster);
//            } catch (IOException e) {
//            }
//
//            if (!liked) {
//                favoriteAction = new Action(ACTION_SAVE_TO_FAVORITE, getResources().getString(
//                        R.string.save_to_favorite_1), getResources().getString(R.string.save_to_favorite_2));
//                row.addAction(favoriteAction);
//            } else {
//                removeFromFavoriteAction = new Action(ACTION_REMOVE_FROM_FAVORITE, getResources().getString(
//                        R.string.remove_from_favorite_1), getResources().getString(R.string.remove_from_favorite_2));
//                row.addAction(removeFromFavoriteAction);
//            }
//            row.addAction(new Action(ACTION_RENT, getResources().getString(R.string.rent_1),
//                    getResources().getString(R.string.rent_2)));
//            row.addAction(new Action(ACTION_BUY, getResources().getString(R.string.buy_1),
//                    getResources().getString(R.string.buy_2)));
//
//            return row;
//        }
//
//        @Override
//        protected void onPostExecute(DetailsOverviewRow detailRow) {
//            ClassPresenterSelector ps = new ClassPresenterSelector();
//            // set detail background and style
//            mDorPresenter.setBackgroundColor(getResources().getColor(R.color.detail_background));
//            mDorPresenter.setStyleLarge(true);
//            mDorPresenter.setOnActionClickedListener(new OnActionClickedListener() {
//                @Override
//                public void onActionClicked(Action action) {
//                    if (action.getId() == ACTION_SAVE_TO_FAVORITE) {
//                        favorites();
//                    }else if (action.getId() == ACTION_REMOVE_FROM_FAVORITE) {
//                        favorites();
//                    } else{
//                        Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//            ps.addClassPresenter(DetailsOverviewRow.class, mDorPresenter);
//            ps.addClassPresenter(ListRow.class,
//                    new ListRowPresenter());
//
//            adapter = new ArrayObjectAdapter(ps);
//            adapter.add(detailRow);
//        }
//
//    }

    private String buildTrailerUrl(String key) {
        return String.format("http://img.youtube.com/vi/%1$s/0.jpg", key);
    }

    private void favorites() {
        FavoriteAsyncTask favoriteAsyncTask = new FavoriteAsyncTask();
        favoriteAsyncTask.execute(mSelectedMovie.getId());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Movie) {

                Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
                intent.putExtra(getResources().getString(R.string.movie), mSelectedMovie);
                intent.putExtra(getResources().getString(R.string.should_start), true);
                startActivity(intent);
            }
        }
    }

    protected void updateBackground(URI uri) {
        Log.d(TAG, "uri" + uri);
        Log.d(TAG, "metrics" + mMetrics.toString());
        Picasso.with(getActivity())
                .load(uri.toString())
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .error(mDefaultBackground)
                .into(mBackgroundTarget);
    }

    class FavoriteAsyncTask extends AsyncTask<Long, Void, Integer> {

        @Override
        protected Integer doInBackground(Long... params) {
            long movieId = params[0];
            ContentValues values = new ContentValues();
            int resultId;
            LogUtil.log_i(TAG, "FavoriteAsyncTask movieid: " + movieId);
            values.put(ItemsContract.MOVIE_ID, movieId);
            LogUtil.log_i(TAG, "FavoriteAsyncTask original title: " + mSelectedMovie.getTitle());
            values.put(ItemsContract.ORIGINAL_TITLE, mSelectedMovie.getTitle());
            LogUtil.log_i(TAG, "FavoriteAsyncTask overview: " + mSelectedMovie.getDescription());
            values.put(ItemsContract.OVERVIEW, mSelectedMovie.getDescription());
            //LogUtil.log_i(TAG, "FavoriteAsyncTask vote average: " + mCursor.getString(MoviesLoader.Query.VOTE_AVERAGE));
            //values.put(ItemsContract.VOTE_AVERAGE, mCursor.getString(MoviesLoader.Query.VOTE_AVERAGE));
            LogUtil.log_i(TAG, "FavoriteAsyncTask poster thumbnail: " + mSelectedMovie.getCardImageUrl());
            values.put(ItemsContract.POSTER_THUMBNAIL, mSelectedMovie.getCardImageUrl());
            //LogUtil.log_i(TAG, "FavoriteAsyncTask release date: " + mCursor.getString(MoviesLoader.Query.RELEASE_DATE));
            //values.put(ItemsContract.RELEASE_DATE, mCursor.getString(MoviesLoader.Query.RELEASE_DATE));

            if (!liked) {
                Uri newUri = getActivity().getContentResolver().insert(ItemsContract.FavoritesEntry.CONTENT_URI, values);
                resultId = Integer.parseInt(newUri.getLastPathSegment());
            } else {
                resultId = getActivity().getContentResolver().delete(ItemsContract.FavoritesEntry.CONTENT_URI, ItemsContract.MOVIE_ID + "=?", new String[]{String.valueOf(mSelectedMovie.getId())});
            }

            LogUtil.log_i(TAG, "Uri of newly inserted row: " + resultId);

            return resultId;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (isAdded()) {
                if (integer >= 0) {
                    // successfully inserted data into Favorites table
                    // do something here
                    LogUtil.log_i(TAG, "Operation succeeded!");
                    if (liked) {
                        liked = false;
                        row.removeAction(removeFromFavoriteAction);
                        favoriteAction = new Action(ACTION_SAVE_TO_FAVORITE, getResources().getString(
                                R.string.save_to_favorite_1), getResources().getString(R.string.save_to_favorite_2));
                        row.addAction(favoriteAction);
                    } else {
                        liked = true;
                        row.removeAction(favoriteAction);

                        removeFromFavoriteAction = new Action(ACTION_REMOVE_FROM_FAVORITE, getResources().getString(
                                R.string.remove_from_favorite_1), getResources().getString(R.string.remove_from_favorite_2));
                        row.addAction(removeFromFavoriteAction);
                    }

                } else {
                    // operation failed
                    // alert user
                    LogUtil.log_i(TAG, "Operation failed!");
                }
            }
        }
    }

    class CheckIfAlreadyFavoritedAsyncTask extends AsyncTask<Long, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Long... params) {
            long movieId = params[0];
            ContentValues values = new ContentValues();
            values.put("movie_id", movieId);

            // NOTE: Whenever we query a cursor, we have to close it afterwards or there might be an error
            Cursor cursor = getActivity().getContentResolver().query(ItemsContract.FavoritesEntry.CONTENT_URI, null, ItemsContract.MOVIE_ID + "=?", new String[]{String.valueOf(movieId)}, null);
            LogUtil.log_i(TAG, "CheckIfAlreadyFavoritedAysncTask: movieId: " + movieId);
            LogUtil.log_i(TAG, "check if already favorited asynctask cursor row count: " + cursor.getCount());
            if (cursor.moveToFirst()) {
                // cursor not null. this movie has already been liked before
                liked = true;
            }

            // This is very important! Never forget to close cursor after the query
            cursor.close();

            LogUtil.log_i(TAG, "this movie is already liked? " + liked);
            return liked;
        }

        @Override
        protected void onPostExecute(Boolean mBoolean) {
            if (isAdded()) {
                liked = mBoolean;
            }
        }
    }

    class FetchTrailerListAsyncTask extends AsyncTask<Long, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Long... params) {
            JSONArray array = RemoteEndpointUtil.fetchTrailerListJsonArray(params[0]);
            return jsonArrayToStringArrayList(array);
        }

        private ArrayList<String> jsonArrayToStringArrayList(JSONArray array) {
            ArrayList<String> keyList = new ArrayList<>();
            ArrayList<String> nameList = new ArrayList<>();
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String key = object.getString("key");
                        String name = object.getString("name");
                        keyList.add(key);
                        nameList.add(name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            trailerNameList = nameList;
            return keyList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> trailerList) {
            if (isAdded()) {
                if (trailerList.size() != 0) {
                    int NUM_COLS = trailerList.size();

//                    ArrayObjectAdapter adapter = new ArrayObjectAdapter(ps);
//                    adapter.add(detailRow);

                    String subcategories[] = {
                            getString(R.string.trailers)
                    };
                    List<Movie> list = new ArrayList<>();
                    Collections.shuffle(list);
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
                    for (int j = 0; j < NUM_COLS; j++) {
                        Movie trailerMovie = new Movie();
                        trailerMovie.setCardImageUrl(buildTrailerUrl(trailerList.get(j)));
                        trailerMovie.setBackgroundImageUrl(buildTrailerUrl(trailerList.get(j)));
                        if (trailerNameList != null) {
                            trailerMovie.setTitle(trailerNameList.get(j));
                        }
                        LogUtil.log_i(TAG, "trailer image path: " + buildTrailerUrl(trailerList.get(j)));
                        list.add(trailerMovie);
                        listRowAdapter.add(list.get(j));
                    }

                    HeaderItem header = new HeaderItem(0, subcategories[0]);

                    adapter.add(new ListRow(header, listRowAdapter));

                    setAdapter(adapter);
                }
            }

        }
    }

}
