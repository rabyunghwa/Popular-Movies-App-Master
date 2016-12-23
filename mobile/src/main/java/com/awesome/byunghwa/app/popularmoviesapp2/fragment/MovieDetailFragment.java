package com.awesome.byunghwa.app.popularmoviesapp2.fragment;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.awesome.byunghwa.app.popularmoviesapp2.R;
import com.awesome.byunghwa.app.popularmoviesapp2.activity.DetailActivity;
import com.awesome.byunghwa.app.popularmoviesapp2.activity.SettingsActivity;
import com.awesome.byunghwa.app.popularmoviesapp2.adapter.TransitionAdapter;
import com.awesome.byunghwa.app.popularmoviesapp2.data.FavoriteMoviesLoader;
import com.awesome.byunghwa.app.popularmoviesapp2.data.ItemsContract;
import com.awesome.byunghwa.app.popularmoviesapp2.data.PopularMoviesLoader;
import com.awesome.byunghwa.app.popularmoviesapp2.data.Review;
import com.awesome.byunghwa.app.popularmoviesapp2.data.TopRatedMoviesLoader;
import com.awesome.byunghwa.app.popularmoviesapp2.util.DateUtil;
import com.awesome.byunghwa.app.popularmoviesapp2.util.LogUtil;
import com.awesome.byunghwa.app.popularmoviesapp2.util.MaterialColorPaletteUtil;
import com.awesome.byunghwa.app.popularmoviesapp2.util.NetworkUtil;
import com.awesome.byunghwa.app.popularmoviesapp2.util.RemoteEndpointUtil;
import com.awesome.byunghwa.app.popularmoviesapp2.util.Utils;
import com.awesome.byunghwa.app.popularmoviesapp2.util.WindowCompatUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String TAG = "MovieDetailFragment";

    // views
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private Cursor mCursor;
    private ImageView image;
    private TextView releaseDate;
    private TextView plotSynopsis;
    private TextView voteAverage;

    //private boolean fromFavorite;
    //private TextView reviewContentPreview;
    //private TextView trailerContentPreview;
    private ProgressBar progressBar;

    // declare long value to store clicked movie item id
    private long clickedId;

    private boolean liked;

    private ArrayList<String> trailerKeyList;
    private ArrayList<String> trailerNameList;

    private static PopupWindow popupWindowReviews;
    private static PopupWindow popupWindowTrailers;

    private static ViewGroup trailerSection;
    private static ViewGroup reviewSection;
    private ViewGroup reviewViewGroup;
    private ViewGroup trailerViewGroup;
    private HorizontalScrollView trailerScrollerView;

    private ViewGroup trailersView;
    private ViewGroup reviewsView;

    private String type;

    private static final int MY_LOADER_ID_POPULAR = 5;
    private static final int MY_LOADER__ID_FAVORITE = 6;
    private static final int MY_LOADER_ID_TOP_RATED = 7;
    private View view;

    //private static final String KEY_DETAIL_CURSOR_ID = "com.awesome.byunghwa.app.popularmoviesapp2.DETAILCURSOR";

    private CheckIfAlreadyFavoritedAsyncTask checkIfAlreadyFavoritedAsyncTask;
    private FetchReviewsAsyncTask fetchReviewsAsyncTask;
    private FetchTrailerListAsyncTask fetchTrailerListAsyncTask;
    private FavoriteAsyncTask favoriteAsyncTask;
    private CollapsingToolbarLayout collapsingToolbar;

    private boolean mIsDualPane;

    // for phones, the corresponding activity associated with this fragment is DetailActivity
    // for tablets, it's MainActivity
    //private AppCompatActivity mActivityInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (LogUtil.DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            clickedId = arguments.getLong(DetailActivity.EXTRA_MOVIE_ID_CLICKED);
            type = arguments.getString(DetailActivity.EXTRA_MOVIE_TYPE_CLICKED);
            mIsDualPane = arguments.getBoolean(DetailActivity.EXTRA_IS_DUAL_PANE);
        }

        WindowCompatUtils.setStatusBarcolor(getActivity().getWindow(), android.R.color.transparent);

        LogUtil.log_i(TAG, "MovieDetailFragment clicked id: " + clickedId);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_detail);
        trailerScrollerView = (HorizontalScrollView) view.findViewById(R.id.trailers_container);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBarMovieDetail);

        // first contact content provider to see if this movie has already been favorited. if yes, then set fab color to pressed
        checkIfAlreadyFavoritedAsyncTask = new CheckIfAlreadyFavoritedAsyncTask();

        if (clickedId > 0) {
            checkIfAlreadyFavoritedAsyncTask.execute(clickedId);
        }

        setHasOptionsMenu(true); // This is very important!!!!

        image = (ImageView) view.findViewById(R.id.image);
        releaseDate = (TextView) view.findViewById(R.id.release_date);
        plotSynopsis = (TextView) view.findViewById(R.id.plot_synopsis);
        voteAverage = (TextView) view.findViewById(R.id.vote_average);

        trailerSection = (ViewGroup) view.findViewById(R.id.card_view_trailer_section);
        reviewSection = (ViewGroup) view.findViewById(R.id.card_view_comment_section);
        reviewViewGroup = (ViewGroup) view.findViewById(R.id.viewGroupReview);
        trailerViewGroup = (ViewGroup) view.findViewById(R.id.viewGroupTrailer);

        collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);

        fab = (FloatingActionButton) view.findViewById(R.id.fab_detail);

        // animate how the fab appears only when the scene transition is over
        if (savedInstanceState == null) {
            fab.setScaleX(0);
            fab.setScaleY(0);
            getActivity().getWindow().getEnterTransition().addListener(new TransitionAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    getActivity().getWindow().getEnterTransition().removeListener(this);
                    fab.animate().scaleX(1).scaleY(1);
                }
            });
        }

        fab.setOnClickListener(MovieDetailFragment.this);

        LogUtil.log_i("MovieDetailFragment", "transition name: " + clickedId);

        ViewCompat.setTransitionName(image, String.valueOf(clickedId));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getView() == null){
            return;
        }

//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//                    // handle back button's click listener
//                    fab.animate().scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
//                            appCompatActivity.supportFinishAfterTransition();
//
//                            fab.hide();
//                        }
//                    });
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    @Override
    public void onPause() {
        // asynctasks should be cancelled when fragment is canceled or stopped
        if (checkIfAlreadyFavoritedAsyncTask != null && !checkIfAlreadyFavoritedAsyncTask.isCancelled()) {
            checkIfAlreadyFavoritedAsyncTask.cancel(true);
        }
        if (fetchReviewsAsyncTask != null && !fetchReviewsAsyncTask.isCancelled()) {
            fetchReviewsAsyncTask.cancel(true);
        }
        if (fetchTrailerListAsyncTask != null && !fetchTrailerListAsyncTask.isCancelled()) {
            fetchTrailerListAsyncTask.cancel(true);
        }
        if (favoriteAsyncTask != null && !favoriteAsyncTask.isCancelled()) {
            favoriteAsyncTask.cancel(true);
        }

        // store clicked movie id
        super.onPause();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroyView() {
        super.onDestroy();
        WindowCompatUtils.setStatusBarcolor(getActivity().getWindow(), MaterialColorPaletteUtil.fetchPrimaryDarkColor(getActivity()));
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setNavigationBarColor(MaterialColorPaletteUtil.fetchPrimaryDarkColor(getActivity()));
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(myNetworkStateChangeReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver myNetworkStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                LogUtil.log_i(TAG, "Network State Change Receiver onReceive!");

                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    seeReviews();
                    watchTrailers();
                }
            }
        }
    };

    private void watchTrailers() {
        fetchTrailorLists();
    }

    private void fetchReviews() {
        // start an asynctask to fetch reviews from server
        fetchReviewsAsyncTask = new FetchReviewsAsyncTask();
        fetchReviewsAsyncTask.execute(clickedId);
    }

    private void fetchTrailorLists() {
        // start an asynctask to fetch trailor list from server
        fetchTrailerListAsyncTask = new FetchTrailerListAsyncTask();
        fetchTrailerListAsyncTask.execute(clickedId);
    }

    private void seeReviews() {
        fetchReviews();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // init loader
        if (type != null) {
            if (type.equals("popular")) {
                getLoaderManager().initLoader(MY_LOADER_ID_POPULAR, null, this);
            } else if (type.equals("top_rated")) {
                getLoaderManager().initLoader(MY_LOADER_ID_TOP_RATED, null, this);
            } else if (type.equals("favorite")) {
                getLoaderManager().initLoader(MY_LOADER__ID_FAVORITE, null, this);
            }
        }

        // listens for network connection state change. If there is network available, start asynctask and
        // fetch reviews and trailer lists
        // Note that this gets called on start of this activity
        getActivity().registerReceiver(myNetworkStateChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();

        // if its tablet layout, then we dont want to display the home button
        if (actionBar != null && !mIsDualPane) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        super.onActivityCreated(savedInstanceState);
    }

    // loader setup
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        LogUtil.log_i(TAG, "MovieDetailFragment onCreateLoader Gets Called!");
        switch (i) {
            case MY_LOADER_ID_POPULAR:
                return PopularMoviesLoader.newInstanceForItemId(getActivity(),
                        clickedId);
            case MY_LOADER_ID_TOP_RATED:
                return TopRatedMoviesLoader.newInstanceForItemId(getActivity(),
                        clickedId);
            case MY_LOADER__ID_FAVORITE:
                return FavoriteMoviesLoader.newInstanceForFavoriteId(getActivity(),
                        clickedId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
            return;
        }

        ActivityCompat.startPostponedEnterTransition(getActivity());
        switch (cursorLoader.getId()) {
            case MY_LOADER_ID_POPULAR:
                bindViews();
                break;
            case MY_LOADER_ID_TOP_RATED:
                bindViews();
                break;
            case MY_LOADER__ID_FAVORITE:
                bindViewsFavorite();
                break;
        }
    }

    private void bindViewsFavorite() {
        if (mCursor != null && mCursor.moveToFirst()) {
            String titleData = null;
            String voteAverageData = null;
            String urlPosterThumbnail_not_complete = null;
            String releaseDateData = null;
            String overviewData = null;
            // retrieve data from cursor
            if (!mCursor.isNull(PopularMoviesLoader.Query.ORIGINAL_TITLE)) {
                titleData = mCursor.getString(PopularMoviesLoader.Query.ORIGINAL_TITLE);
                LogUtil.log_i(TAG, "title: " + titleData);
            }

            if (!mCursor.isNull(PopularMoviesLoader.Query.VOTE_AVERAGE)) {
                voteAverageData = mCursor.getString(PopularMoviesLoader.Query.VOTE_AVERAGE);
                LogUtil.log_i(TAG, "vote average: " + voteAverageData);
            }

            if (!mCursor.isNull(PopularMoviesLoader.Query.POSTER_THUMBNAIL)) {
                urlPosterThumbnail_not_complete = mCursor.getString(PopularMoviesLoader.Query.POSTER_THUMBNAIL);
                LogUtil.log_i(TAG, "poster thumbnail: " + urlPosterThumbnail_not_complete);
            }

            String urlCompleteData = "http://image.tmdb.org/t/p/w185/" + urlPosterThumbnail_not_complete;

            if (!mCursor.isNull(PopularMoviesLoader.Query.RELEASE_DATE)) {
                releaseDateData = mCursor.getString(PopularMoviesLoader.Query.RELEASE_DATE);
                LogUtil.log_i(TAG, "release date: " + releaseDateData);
            }

            if (!mCursor.isNull(PopularMoviesLoader.Query.OVERVIEW)) {
                overviewData = mCursor.getString(PopularMoviesLoader.Query.OVERVIEW);
                LogUtil.log_i(TAG, "overview: " + overviewData);
            }

            if (titleData != null && !titleData.equals("null")) {
                collapsingToolbar.setTitle(titleData);
            } else {
                collapsingToolbar.setTitle(getString(R.string.empty_title));
            }

            if (releaseDateData != null && !releaseDateData.equals("null")) {
                releaseDate.setText(DateUtil.formatDate(releaseDateData));
            } else {
                releaseDate.setText(getString(R.string.empty_release_date));
            }

            if (voteAverageData != null && !voteAverageData.equals("null")) {
                voteAverage.setText(voteAverageData + "/10");
            } else {
                voteAverage.setText(getString(R.string.empty_rating));
            }

            if (overviewData != null && !overviewData.equals("null")) {
                plotSynopsis.setText(overviewData);
            } else {
                plotSynopsis.setText(getString(R.string.empty_overview));
            }

            Picasso.with(getActivity()).load(urlCompleteData).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            applyPalette(palette);
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    private void bindViews() {
        if (mCursor != null && mCursor.moveToFirst()) {
            String titleData = null;
            String voteAverageData = null;
            String urlPosterThumbnail_not_complete = null;
            String releaseDateData = null;
            String overviewData = null;
            // retrieve data from cursor
            if (!mCursor.isNull(PopularMoviesLoader.Query.ORIGINAL_TITLE)) {
                titleData = mCursor.getString(PopularMoviesLoader.Query.ORIGINAL_TITLE);
                LogUtil.log_i(TAG, "title: " + titleData);
            }

            if (!mCursor.isNull(PopularMoviesLoader.Query.VOTE_AVERAGE)) {
                voteAverageData = mCursor.getString(PopularMoviesLoader.Query.VOTE_AVERAGE);
                LogUtil.log_i(TAG, "vote average: " + voteAverageData);
            }

            if (!mCursor.isNull(PopularMoviesLoader.Query.POSTER_THUMBNAIL)) {
                urlPosterThumbnail_not_complete = mCursor.getString(PopularMoviesLoader.Query.POSTER_THUMBNAIL);
                LogUtil.log_i(TAG, "poster thumbnail: " + urlPosterThumbnail_not_complete);
            }

            String urlCompleteData = "http://image.tmdb.org/t/p/w185/" + urlPosterThumbnail_not_complete;

            if (!mCursor.isNull(PopularMoviesLoader.Query.RELEASE_DATE)) {
                releaseDateData = mCursor.getString(PopularMoviesLoader.Query.RELEASE_DATE);
                LogUtil.log_i(TAG, "release date: " + releaseDateData);
            }

            if (!mCursor.isNull(PopularMoviesLoader.Query.OVERVIEW)) {
                overviewData = mCursor.getString(PopularMoviesLoader.Query.OVERVIEW);
                LogUtil.log_i(TAG, "overview: " + overviewData);
            }

            if (titleData != null && !titleData.equals("null")) {
                // set toolbar title to movie name
                collapsingToolbar.setTitle(titleData);
            } else {
                // set toolbar title to movie name
                collapsingToolbar.setTitle(getString(R.string.empty_title));
            }

            if (releaseDateData != null && !releaseDateData.equals("null")) {
                releaseDate.setText(DateUtil.formatDate(releaseDateData));
            } else {
                releaseDate.setText(getString(R.string.empty_release_date));
            }

            if (voteAverageData != null && !voteAverageData.equals("null")) {
                voteAverage.setText(voteAverageData + "/10");
            } else {
                voteAverage.setText(getString(R.string.empty_rating));
            }

            if (overviewData != null && !overviewData.equals("null")) {
                plotSynopsis.setText(overviewData);
            } else {
                plotSynopsis.setText(getString(R.string.empty_overview));
            }

            Picasso.with(getActivity()).load(urlCompleteData).into(image, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            applyPalette(palette);
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void applyPalette(Palette palette) {
        int primaryDark = MaterialColorPaletteUtil.fetchPrimaryDarkColor(getActivity());
        int primary = MaterialColorPaletteUtil.fetchPrimaryColor(getActivity());
        collapsingToolbar.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbar.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setNavigationBarColor(palette.getDarkMutedColor(primaryDark));
        }

        updateFabBackground(fab, palette);
    }

    private void updateFabBackground(FloatingActionButton fab, Palette palette) {
        int lightMutedColor = palette.getLightMutedColor(getResources().getColor(android.R.color.white));
        int mutedColor = palette.getMutedColor(MaterialColorPaletteUtil.fetchAccentColor(getActivity()));

        fab.setRippleColor(lightMutedColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(mutedColor));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            //restablishActionBar();
            LogUtil.log_i(TAG, "Home Button Clicked!");
            //AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                getActivity().finishAfterTransition();
            } else {
                getActivity().finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // review section on click handling
        // initiate a popup window
        switch (v.getId()) {
            case R.id.fab_detail:
                LogUtil.log_i(TAG, "FAB Clicked");
                favoriteAsyncTask = new FavoriteAsyncTask();
                favoriteAsyncTask.execute(clickedId);
                break;
        }
    }

    class FavoriteAsyncTask extends AsyncTask<Long, Void, Integer> {

        @Override
        protected Integer doInBackground(Long... params) {
            long movieId = params[0];
            ContentValues values = new ContentValues();
            int resultId = -1;
            if (mCursor != null && mCursor.moveToFirst()) {
                //try {
                LogUtil.log_i(TAG, "FavoriteAsyncTask movieid: " + movieId);
                values.put(ItemsContract.FavoritesEntry.MOVIE_ID, movieId);
                LogUtil.log_i(TAG, "FavoriteAsyncTask original title: " + mCursor.getString(PopularMoviesLoader.Query.ORIGINAL_TITLE));
                values.put(ItemsContract.FavoritesEntry.ORIGINAL_TITLE, mCursor.getString(PopularMoviesLoader.Query.ORIGINAL_TITLE));
                LogUtil.log_i(TAG, "FavoriteAsyncTask overview: " + mCursor.getString(PopularMoviesLoader.Query.OVERVIEW));
                values.put(ItemsContract.FavoritesEntry.OVERVIEW, mCursor.getString(PopularMoviesLoader.Query.OVERVIEW));
                LogUtil.log_i(TAG, "FavoriteAsyncTask vote average: " + mCursor.getString(PopularMoviesLoader.Query.VOTE_AVERAGE));
                values.put(ItemsContract.FavoritesEntry.VOTE_AVERAGE, mCursor.getString(PopularMoviesLoader.Query.VOTE_AVERAGE));
                LogUtil.log_i(TAG, "FavoriteAsyncTask poster thumbnail: " + mCursor.getString(PopularMoviesLoader.Query.POSTER_THUMBNAIL));
                values.put(ItemsContract.FavoritesEntry.POSTER_THUMBNAIL, mCursor.getString(PopularMoviesLoader.Query.POSTER_THUMBNAIL));
                LogUtil.log_i(TAG, "FavoriteAsyncTask release date: " + mCursor.getString(PopularMoviesLoader.Query.RELEASE_DATE));
                values.put(ItemsContract.FavoritesEntry.RELEASE_DATE, mCursor.getString(PopularMoviesLoader.Query.RELEASE_DATE));

                // ui updates might be quite instantaneous, so we might have to double check whether we really
                // have to insert the data into the database
                // one possible scenario: user has already tapped on the like button, and the like operation
                // has been performed, but due to some reason, the button's like state is not updated
                // in this case, user might tap on the button again just to like again, in this case, there
                // might be a crash
                Cursor cursor = getActivity().getContentResolver().query(ItemsContract.FavoritesEntry.buildDirUri(), null, ItemsContract.FavoritesEntry.MOVIE_ID + "=?", new String[]{String.valueOf(movieId)}, null);
                LogUtil.log_i(TAG, "favorites cursor is empty?: " + (!cursor.moveToFirst()));
                if (!liked) {
                    if (!cursor.moveToFirst()) {
                        Uri newUri = getActivity().getContentResolver().insert(ItemsContract.FavoritesEntry.CONTENT_URI, values);
                        resultId = Integer.parseInt(newUri.getLastPathSegment());
                    }

                } else {
                    if (cursor.moveToFirst()) {
                        resultId = getActivity().getContentResolver().delete(ItemsContract.FavoritesEntry.CONTENT_URI, ItemsContract.FavoritesEntry.MOVIE_ID + "=?", new String[]{String.valueOf(clickedId)});
                    }

                }

                LogUtil.log_i(TAG, "Uri of newly inserted row: " + resultId);
            }

            return resultId;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(Integer integer) {
            if (isAdded()) {
                if (integer >= 0) {
                    // successfully inserted data into Favorites table
                    // do something here
                    LogUtil.log_i(TAG, "Operation succeeded!");
                    if (liked) {
                        liked = false;
                        fab.setImageResource(R.drawable.ic_favorite_white);
                    } else {
                        liked = true;
                        fab.setImageResource(R.drawable.ic_favorite_white_full);
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
            Cursor cursor = getActivity().getContentResolver().query(ItemsContract.FavoritesEntry.CONTENT_URI, null, ItemsContract.FavoritesEntry.MOVIE_ID + "=?", new String[]{String.valueOf(movieId)}, null);
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

                if (mBoolean) {
                    fab.setImageResource(R.drawable.ic_favorite_white_full);
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_white);
                }
            }
        }
    }

    class FetchReviewsAsyncTask extends AsyncTask<Long, Void, ArrayList<Review>> {

        @Override
        protected ArrayList<Review> doInBackground(Long... params) {
            JSONArray reviewArray = RemoteEndpointUtil.fetchMovieReviewJsonArray(params[0]);
            return jsonArrayToReviewArrayList(reviewArray);
        }

        private ArrayList<Review> jsonArrayToReviewArrayList(JSONArray reviewJsonArray) {
            ArrayList<Review> reviewArrayList = new ArrayList<>();
            if (reviewJsonArray != null) {
                for (int i = 0; i < reviewJsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = reviewJsonArray.getJSONObject(i);
                        String author = jsonObject.getString("author");
                        String content = jsonObject.getString("content");
                        Review review = new Review();
                        review.setReviewAuthor(author);
                        review.setReviewContent(content);
                        reviewArrayList.add(review);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                LogUtil.log_i(TAG, "FetchReviewsAsyncTask ReviewArrayList Length: " + reviewArrayList.size());
            }

            return reviewArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviewArrayList) {
            if (isAdded()) {
                reviewViewGroup.removeAllViews();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                progressBar.setVisibility(View.GONE);
                reviewSection.setVisibility(View.VISIBLE);

                LogUtil.log_i(TAG, "review size: " + reviewArrayList.size());
                if (reviewArrayList.size() == 0) {
                    View viewEmpty = inflater.inflate(R.layout.review_empty, null, false);
                    TextView reviewEmpty = (TextView) viewEmpty.findViewById(R.id.empty_view_review_section);
                    if (reviewEmpty.getParent() != null)
                        ((ViewGroup) reviewEmpty.getParent()).removeView(reviewEmpty);
                    reviewViewGroup.addView(reviewEmpty);
                } else {

                    for (Review review : reviewArrayList) {
                        ViewGroup reviewContainer = (ViewGroup) inflater.inflate(R.layout.review, reviewsView, false);
                        TextView reviewAuthor = (TextView) reviewContainer.findViewById(R.id.review_author);
                        final TextView reviewContent = (TextView) reviewContainer.findViewById(R.id.review_content);
                        reviewAuthor.setText(review.getReviewAuthor());
                        reviewContent.setText(Html.fromHtml(review.getReviewContent().replace("\n\n", " ").replace("\n", " ")));

                        Utils.makeTextViewResizable(reviewContent, 3, "View More", true);
                        reviewContainer.setTag(review);
                        reviewViewGroup.addView(reviewContainer);
                    }
                }

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
            ArrayList<String> stringList = new ArrayList<>();
            ArrayList<String> nameList = new ArrayList<>();
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String key = object.getString("key");
                        String name = object.getString("name");
                        stringList.add(key);
                        nameList.add(name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            trailerNameList = nameList;
            return stringList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> trailerList) {
            if (isAdded()) {
                trailerViewGroup.removeAllViews();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                progressBar.setVisibility(View.GONE);
                trailerSection.setVisibility(View.VISIBLE);

                LogUtil.log_i(TAG, "trailer size: " + trailerList.size());

                trailerKeyList = trailerList;
                int posterWidth = getResources().getDimensionPixelSize(R.dimen.video_width);
                int posterHeight = getResources().getDimensionPixelSize(R.dimen.video_height);
                if (trailerKeyList.size() == 0) {
                    View viewEmpty = inflater.inflate(R.layout.trailer_empty, null, false);
                    TextView trailerEmpty = (TextView) viewEmpty.findViewById(R.id.empty_view_trailer_section);
                    if (trailerEmpty.getParent() != null)
                        ((ViewGroup) trailerEmpty.getParent()).removeView(trailerEmpty);
                    trailerViewGroup.addView(trailerEmpty);

                    toolbar.inflateMenu(R.menu.menu_main);
                } else {
                    trailerScrollerView.setVisibility(View.VISIBLE);
                    for (final String trailer : trailerList) {
                        final ViewGroup thumbContainer = (ViewGroup) inflater.inflate(R.layout.video, trailersView,
                                false);
                        final ImageView thumbView = (ImageView) thumbContainer.findViewById(R.id.video_thumb);
                        final String urlString = "http://www.youtube.com/watch?v=" + trailer;
                        final String thumbnailPath = String.format("http://img.youtube.com/vi/%1$s/0.jpg", trailer);
                        LogUtil.log_i(TAG, "thumbnail path: " + thumbnailPath);
                        thumbView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlString)));
                            }
                        });

                        Glide.with(getActivity())
                                .load(thumbnailPath)
                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.movie_placeholder).error(R.drawable.movie_placeholder).
                                into(new SimpleTarget<Bitmap>(posterWidth, posterHeight) {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                        Log.d("trailer resource", "ready");
                                        thumbView.setImageBitmap(resource); // Possibly runOnUiThread()
                                    }
                                });

                        trailerViewGroup.addView(thumbContainer);
                    }

                    // only show share action menu item when there is at least one trailer video available
                    if (mIsDualPane) {
                        toolbar.inflateMenu(R.menu.menu_detail_tablet);
                    } else {
                        toolbar.inflateMenu(R.menu.menu_detail);
                    }

                }
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_share) {
                            LogUtil.log_i(TAG, "Share Button Clicked!");
                            String url = "http://www.youtube.com/watch?v=" + trailerKeyList.get(0);
                            startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                                    .setType("text/plain")
                                    .setText(url)
                                    .getIntent(), getString(R.string.action_share)));
                            return true;
                        }
                        if (item.getItemId() == R.id.action_settings) {
                            startActivity(new Intent(getActivity(), SettingsActivity.class));
                            return true;
                        }
                        return false;
                    }
                });
            }

        }
    }
}
