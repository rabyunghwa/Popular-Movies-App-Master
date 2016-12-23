package com.awesome.byunghwa.app.popularmoviesapp2.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.awesome.byunghwa.app.popularmoviesapp2.R;
import com.awesome.byunghwa.app.popularmoviesapp2.fragment.MovieDetailFragment;
import com.awesome.byunghwa.app.popularmoviesapp2.fragment.MovieListFragment;
import com.awesome.byunghwa.app.popularmoviesapp2.preferences.Preferences;
import com.awesome.byunghwa.app.popularmoviesapp2.util.LogUtil;

public class MainActivity extends AppCompatActivity implements MovieListFragment.OnMoviePosterSelectedListener{

    private static final String TAG = "MainActivity";

    // for tablet
    private static boolean mIsDualPane; // declare a variable to decide if its tablet landscape layout

    private static View emptyView;

    public static AppCompatActivity activityInstance;

    public static String ACTION_MOVIE_LIST_LOADED = "com.awesome.byunghwa.app.popularmoviesapp2.activity.ACTION_MOVIE_LIST_LOADED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferences.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityInstance = this;

        emptyView = findViewById(R.id.empty_view_detail_fragment);

        // find our fragments
        MovieListFragment mMovieListFragment = (MovieListFragment) getFragmentManager().findFragmentById(
                R.id.fragment_movie_list);

        supportPostponeEnterTransition();

        // Register ourselves as the listener for the movie list fragment events.
        mMovieListFragment.setOnMoviePosterSelectedListener(this);

        View mMovieDetailContainer = findViewById(R.id.movie_detail_container);
        mIsDualPane = mMovieDetailContainer != null && mMovieDetailContainer.getVisibility() == View.VISIBLE;

        LogUtil.log_i(TAG, "dual pane? " + mIsDualPane);

        // for tablet
        // register a broadcast receiver to be notified when movie lists have been loaded
        registerReceiver(myMovieListInfoLoadedReceiver,
                new IntentFilter(ACTION_MOVIE_LIST_LOADED));

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myMovieListInfoLoadedReceiver);
        super.onDestroy();
    }

    // for tablet version to show detail page of the first item in each category
    private BroadcastReceiver myMovieListInfoLoadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            // only inflate detail fragment when in dual pane mode and first movie id is valid(>0)
            LogUtil.log_i(TAG, "broadcast received...");
            long firstMovieId = intent.getLongExtra("first_movie_id", 0);
            if (mIsDualPane && ACTION_MOVIE_LIST_LOADED.equals(intent.getAction()) && firstMovieId > 0) {
                MovieDetailFragment fragment = new MovieDetailFragment();

                emptyView.setVisibility(View.GONE);

                Bundle args = new Bundle();
                args.putLong(DetailActivity.EXTRA_MOVIE_ID_CLICKED, firstMovieId);
                args.putString(DetailActivity.EXTRA_MOVIE_TYPE_CLICKED, "popular");
                args.putBoolean(DetailActivity.EXTRA_IS_DUAL_PANE, mIsDualPane);
                fragment.setArguments(args);

                LogUtil.log_i(TAG, "BroadcastReceiver first movie id: " + firstMovieId);

                getFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, MovieDetailFragment.TAG)
                        .commit();
            }

            if (mIsDualPane && firstMovieId == 0) {
                LogUtil.log_i(TAG, "favorite list is empty");
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    };

    /** Called when a headline is selected.
     *
     * This is called by the HeadlinesFragment (via its listener interface) to notify us that a
     * headline was selected in the Action Bar. The way we react depends on whether we are in
     * single or dual-pane mode. In single-pane mode, we launch a new activity to display the
     * selected article; in dual-pane mode we simply display it on the article fragment.
     *
     * @param clickedMovieId the index of the selected headline.
     */
    @Override
    public void onMoviePosterSelected(View view, long clickedMovieId, String type) {
        LogUtil.log_i(TAG, "mIsDualPane?" + mIsDualPane);
        if (mIsDualPane) {
            // display it on the detail fragment
            LogUtil.log_i(TAG, "MainActivity two pane layout onItemClick clicked");
            Bundle args = new Bundle();
            args.putLong(DetailActivity.EXTRA_MOVIE_ID_CLICKED, clickedMovieId);
            args.putBoolean(DetailActivity.EXTRA_IS_DUAL_PANE, mIsDualPane);
            args.putString(DetailActivity.EXTRA_MOVIE_TYPE_CLICKED, type);
            view.setSelected(true);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MovieDetailFragment.TAG)
                    .commit();
        }
        else {
            // use separate activity
            DetailActivity.navigate(this, view.findViewById(R.id.image), clickedMovieId, type);
        }
    }
}
