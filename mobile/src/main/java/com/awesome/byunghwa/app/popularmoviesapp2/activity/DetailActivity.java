package com.awesome.byunghwa.app.popularmoviesapp2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.awesome.byunghwa.app.popularmoviesapp2.R;
import com.awesome.byunghwa.app.popularmoviesapp2.fragment.MovieDetailFragment;
import com.awesome.byunghwa.app.popularmoviesapp2.preferences.Preferences;
import com.awesome.byunghwa.app.popularmoviesapp2.util.LogUtil;


public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    public static final String EXTRA_MOVIE_ID_CLICKED = "com.awesome.byunghwa.app.popularmoviesapp2.extraClickedMovieId";
    public static final String EXTRA_MOVIE_TYPE_CLICKED = "com.awesome.byunghwa.app.popularmoviesapp2.extraClickedMovieType";
    public static final String EXTRA_IS_DUAL_PANE = "com.awesome.byunghwa.app.popularmoviesapp2.extraIsDualPane";

    // declare long value to store clicked movie item id
    private long clickedId;
    private String type;

    public static AppCompatActivity activityInstance;


    public static void navigate(AppCompatActivity activity, View transitionImage, long clickedMovieId, String type) {

        // shared element transition setup
        Intent intent = new Intent(activity, DetailActivity.class);

        intent.putExtra(EXTRA_MOVIE_ID_CLICKED, clickedMovieId);
        intent.putExtra(EXTRA_MOVIE_TYPE_CLICKED, type);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, String.valueOf(clickedMovieId));
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferences.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        activityInstance = this;

        LogUtil.log_i(TAG, "onCreate gets called");

        // retrieve the clicked movie item id from intent
        if (getIntent().hasExtra(EXTRA_MOVIE_ID_CLICKED)) {
            clickedId = getIntent().getLongExtra(EXTRA_MOVIE_ID_CLICKED, -1);
            type = getIntent().getStringExtra(EXTRA_MOVIE_TYPE_CLICKED);
            LogUtil.log_i(TAG, "clicked id: " + clickedId);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putLong(EXTRA_MOVIE_ID_CLICKED, clickedId);
            arguments.putString(EXTRA_MOVIE_TYPE_CLICKED, type);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();

            // Being here means we are in animation mode
            ActivityCompat.postponeEnterTransition(this);
        }


    }

}