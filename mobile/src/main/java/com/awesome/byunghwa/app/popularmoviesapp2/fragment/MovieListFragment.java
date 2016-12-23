package com.awesome.byunghwa.app.popularmoviesapp2.fragment;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.awesome.byunghwa.app.popularmoviesapp2.R;
import com.awesome.byunghwa.app.popularmoviesapp2.activity.MainActivity;
import com.awesome.byunghwa.app.popularmoviesapp2.activity.SettingsActivity;
import com.awesome.byunghwa.app.popularmoviesapp2.adapter.CustomStaggeredGridLayoutManager;
import com.awesome.byunghwa.app.popularmoviesapp2.adapter.MovieListRecyclerViewAdapter;
import com.awesome.byunghwa.app.popularmoviesapp2.data.FavoriteMoviesLoader;
import com.awesome.byunghwa.app.popularmoviesapp2.data.PopularMoviesLoader;
import com.awesome.byunghwa.app.popularmoviesapp2.data.PopularMoviesUpdaterService;
import com.awesome.byunghwa.app.popularmoviesapp2.data.TopRatedMoviesLoader;
import com.awesome.byunghwa.app.popularmoviesapp2.data.TopRatedMoviesUpdaterService;
import com.awesome.byunghwa.app.popularmoviesapp2.util.EndlessRecyclerOnScrollListener;
import com.awesome.byunghwa.app.popularmoviesapp2.util.GlobalConsts;
import com.awesome.byunghwa.app.popularmoviesapp2.util.LogUtil;
import com.awesome.byunghwa.app.popularmoviesapp2.util.MaterialColorPaletteUtil;
import com.awesome.byunghwa.app.popularmoviesapp2.util.NetworkUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MovieListRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "MovieListFragment";

    private static final int MY_LOADER_ID_POPULAR = 0;
    private static final int MY_LOADER_ID_TOP_RATED = 1;
    private static final int MY_LOADER_ID_FAVORITES = 2;

    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private static MovieListRecyclerViewAdapter adapter;
    private static ProgressBar progressBar;
    private Spinner spinner_nav;

    private boolean popularListInitialized;
    private boolean topRatedListInitialized;

    private boolean justTappedOnSpinnerPopItem;
    private boolean justTappedOnSpinnerTopRatedItem;

    private ActionBar actionBar;

    private boolean mIsDualPane;

    private PopularMovieListFetchedReceiver mPopularMovieListReceiver;
    private TopRatedMovieListFetchedReceiver mTopRatedMovieListReceiver;
    private FavoriteMovieListFetchedReceiver mFavoriteMovieListReceiver;

    private int spinnerSelection;

    // The listener we are to notify when a movie poster is selected
    private OnMoviePosterSelectedListener mMoviePosterSelectedListener = null;

    // declared to keep track of recycler view position when spinner selection changes
    private String BUNDLE_RECYCLER_LAYOUT_POPULAR = "com.awesome.byunghwa.app.popularmoviesapp2.fragment.BUNDLE_RECYCLER_LAYOUT_POPULAR";
    private String BUNDLE_RECYCLER_LAYOUT_TOP_RATED = "com.awesome.byunghwa.app.popularmoviesapp2.fragment.BUNDLE_RECYCLER_LAYOUT_TOP_RATED";
    private String BUNDLE_RECYCLER_LAYOUT_FAVORITES = "com.awesome.byunghwa.app.popularmoviesapp2.fragment.BUNDLE_RECYCLER_LAYOUT_FAVORITES";

    /**
     * Disable predictive animations. There is a bug in RecyclerView which causes views that
     * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
     * adapter size has decreased since the ViewHolder was recycled.
     */
    private CustomStaggeredGridLayoutManager sglm;

    // here we set arguments so that later we call getArguments() to store recycler view state
    public MovieListFragment() {
        LogUtil.log_i(TAG, "Movie List Fragment Constructor gets called");
        setArguments(new Bundle());
    }

    /**
     * Represents a listener that will be notified of movie poster selections.
     */
    public interface OnMoviePosterSelectedListener {
        /**
         * Called when a given headline is selected.
         *
         * @param clickedMovieId the id of the selected movie poster.
         */
        void onMoviePosterSelected(View view, long clickedMovieId, String type);
    }

    /**
     * Sets the listener that should be notified of headline selection events.
     *
     * @param listener the listener to notify.
     */
    public void setOnMoviePosterSelectedListener(OnMoviePosterSelectedListener listener) {
        mMoviePosterSelectedListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.log_i(TAG, "onCreateView gets called");
        // Inflate the layout for this fragment
        View layoutView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        initRecyclerView(layoutView);
        initSwipeRefreshLayout(layoutView);
        initEmptyView(layoutView);
        initSpinner(layoutView);
        initProgressBar(layoutView);

        // we use appbarlayout as the view to decide if we are inflating the phone or tablet layout
        View mAppBarLayout = layoutView.findViewById(R.id.appBarLayout);
        mIsDualPane = mAppBarLayout == null;

        LogUtil.log_i(TAG, "mIsDualPane? " + mIsDualPane);

        if (!mIsDualPane) {
            setHasOptionsMenu(true);// this is different from declaring menu items in activity. We have to explicitly
            // declare it here
        }

        // this has to get called after we know if this is a dual pane layout, aka, a tablet layout
        initToolbar(layoutView);

        return layoutView;
    }



    private void initProgressBar(View layoutView) {
        progressBar = (ProgressBar) layoutView.findViewById(R.id.progressBar);
    }

    private void initSpinner(View layoutView) {
        spinner_nav = (Spinner) layoutView.findViewById(R.id.spinner_nav);
        String[] items = getActivity().getResources().getStringArray(R.array.sort_order_entries);
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
        spinner_nav.setAdapter(itemsAdapter);

        spinner_nav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                LogUtil.log_i(TAG, "spinner on item selected gets called...");

                // save recycler view state so that we can restore it later when navigating back from detail activity
                if (spinnerSelection == 0) {
                    // popular movies
                    LogUtil.log_i(TAG, "saving spinner popular position...");
                    getArguments().putParcelable(BUNDLE_RECYCLER_LAYOUT_POPULAR, recyclerView.getLayoutManager().onSaveInstanceState());
                }

                if (spinnerSelection == 1) {
                    // top rated movies
                    LogUtil.log_i(TAG, "saving spinner top rated position...");
                    getArguments().putParcelable(BUNDLE_RECYCLER_LAYOUT_TOP_RATED, recyclerView.getLayoutManager().onSaveInstanceState());
                }

                if (spinnerSelection == 2) {
                    // favorite movies
                    LogUtil.log_i(TAG, "saving spinner favorite position...");
                    getArguments().putParcelable(BUNDLE_RECYCLER_LAYOUT_FAVORITES, recyclerView.getLayoutManager().onSaveInstanceState());
                }

                spinnerSelection = position;

                switch (position) {
                    case 0:
                        // most popular movies
                        mSwipeRefreshLayout.setRefreshing(true);
                        Intent intent_popular = new Intent(getActivity(), PopularMoviesUpdaterService.class);
                        if (!popularListInitialized) {
                            intent_popular.putExtra("new", true);
                            intent_popular.putExtra("page", 1);
                        }
                        getActivity().startService(intent_popular);

                        // !!!!!! remember to check if this loader already exists before initializing it
                        // if it already exists, then simply restart it
                        if (getLoaderManager().getLoader(MY_LOADER_ID_POPULAR) == null) {
                            getLoaderManager().initLoader(MY_LOADER_ID_POPULAR, null, MovieListFragment.this);
                        } else {
                            getLoaderManager().restartLoader(MY_LOADER_ID_POPULAR, null, MovieListFragment.this);
                        }

                        popularListInitialized = true;
                        justTappedOnSpinnerPopItem = true;
                        break;
                    case 1:
                        // top rated movies
                        mSwipeRefreshLayout.setRefreshing(true);
                        Intent intent_top_rated = new Intent(getActivity(), TopRatedMoviesUpdaterService.class);
                        if (!topRatedListInitialized) {
                            intent_top_rated.putExtra("new", true);
                            intent_top_rated.putExtra("page", 1);
                        }
                        getActivity().startService(intent_top_rated);

                        // loader initialization should be done in onResume() instead of in onCreate()
                        // so that onLoadFinished() would not get called twice
                        if (getLoaderManager().getLoader(MY_LOADER_ID_TOP_RATED) == null) {
                            getLoaderManager().initLoader(MY_LOADER_ID_TOP_RATED, null, MovieListFragment.this);
                        } else {
                            getLoaderManager().restartLoader(MY_LOADER_ID_TOP_RATED, null, MovieListFragment.this);
                        }

                        topRatedListInitialized = true;
                        justTappedOnSpinnerTopRatedItem = true;
                        break;
                    case 2:
                        // my favorites
                        mSwipeRefreshLayout.setRefreshing(true);

                        // loader initialization should be done in onResume() instead of in onCreate()
                        // so that onLoadFinished() would not get called twice
                        //getLoaderManager().initLoader(MY_LOADER_ID_FAVORITES, null, MovieListFragment.this);
                        // loader initialization should be done in onResume() instead of in onCreate()
                        // so that onLoadFinished() would not get called twice
                        if (getLoaderManager().getLoader(MY_LOADER_ID_FAVORITES) == null) {
                            getLoaderManager().initLoader(MY_LOADER_ID_FAVORITES, null, MovieListFragment.this);
                        } else {
                            getLoaderManager().restartLoader(MY_LOADER_ID_FAVORITES, null, MovieListFragment.this);
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void initEmptyView(View view) {
        emptyView = (TextView) view.findViewById(R.id.empty_view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        registerReceivers();
        super.onActivityCreated(savedInstanceState);
    }

    private void registerReceivers() {
        mPopularMovieListReceiver = new PopularMovieListFetchedReceiver();
        mTopRatedMovieListReceiver = new TopRatedMovieListFetchedReceiver();
        mFavoriteMovieListReceiver = new FavoriteMovieListFetchedReceiver();

        IntentFilter filter_popular = new IntentFilter(GlobalConsts.POPULAR_MOVIES_FETCHED);
        IntentFilter filter_top_rated = new IntentFilter(GlobalConsts.TOP_RATED_MOVIES_FETCHED);
        IntentFilter filter_favorite = new IntentFilter(GlobalConsts.MY_FAVORITE_MOVIES_FETCHED);

        filter_popular.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter_top_rated.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter_favorite.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);

        getActivity().registerReceiver(mPopularMovieListReceiver, filter_popular);
        getActivity().registerReceiver(mTopRatedMovieListReceiver, filter_top_rated);
        getActivity().registerReceiver(mFavoriteMovieListReceiver, filter_favorite);
        getActivity().registerReceiver(myNetworkStateChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    // if we wanna inflate specific menu item for the fragment only, then we cannot inflate the menu here
    // instead we should only inflate it with toolbar as: toolbar.inflateMenu()
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // as inflating a menu resource in fragment's onCreateOptionsMenu callback will also end up in activity's
        // options menu(which will also end up in other fragments' options menu)
        // in this app's case, if we are using this app on a tablet, if we inflate the settings menu item here,
        // in detail fragment we will also have this settings menu item, which is not what we what
        // so we have to dynamically inflate it
        if (!mIsDualPane) {
            inflater.inflate(R.menu.menu_main, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        LogUtil.log_i(TAG, "onPause gets called");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPopularMovieListReceiver != null) {
            getActivity().unregisterReceiver(mPopularMovieListReceiver);
        }
        if (mTopRatedMovieListReceiver != null) {
            getActivity().unregisterReceiver(mTopRatedMovieListReceiver);
        }
        if (mFavoriteMovieListReceiver != null) {
            getActivity().unregisterReceiver(mFavoriteMovieListReceiver);
        }
        if (myNetworkStateChangeReceiver != null) {
            getActivity().unregisterReceiver(myNetworkStateChangeReceiver);
        }
    }

    private void initRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        recyclerView.setHasFixedSize(true);// setting this to true will prevent the whole list from refreshing when
        // new items have been added to the list (which prevents list from flashing)

        adapter = new MovieListRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        int columnCount = 2;
        sglm = new CustomStaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);
    }

    private void initSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        LogUtil.log_i(TAG, "main activity instance is null? " + MainActivity.activityInstance == null);
        LogUtil.log_i(TAG, "accent color: " + MaterialColorPaletteUtil.fetchAccentColor(MainActivity.activityInstance) + ", primary color: " + MaterialColorPaletteUtil.fetchPrimaryColor(MainActivity.activityInstance));

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccentClassic, R.color.colorAccentRomantic);

        //set the listener to be notified when a refresh is triggered via the swipe gesture
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (spinner_nav.getSelectedItemPosition() == 0) {
                    LogUtil.log_i(TAG, "SwipeRefreshLayout onRefreshListener Gets Called!");
                    if (NetworkUtil.isNetworkAvailable(getActivity())) {
                        Intent intent_popular = new Intent(getActivity(), PopularMoviesUpdaterService.class);
                        intent_popular.putExtra("refresh", true);
                        intent_popular.putExtra("page", 1);
                        intent_popular.putExtra("new", true);
                        getActivity().startService(intent_popular);
                    }

                } else if (spinner_nav.getSelectedItemPosition() == 1) {
                    LogUtil.log_i(TAG, "SwipeRefreshLayout onRefreshListener Gets Called!");
                    if (NetworkUtil.isNetworkAvailable(getActivity())) {
                        Intent intent_top_rated = new Intent(getActivity(), TopRatedMoviesUpdaterService.class);
                        intent_top_rated.putExtra("refresh", true);
                        intent_top_rated.putExtra("page", 1);
                        intent_top_rated.putExtra("new", true);
                        getActivity().startService(intent_top_rated);
                    }

                } else {
                    // if its Favorites, then there is no need to perform update action
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void initToolbar(View view) {
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_main);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.movie));
    }

    public static class PopularMovieListFetchedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.log_i(TAG, "popular movie list has been fetched...");

            // if the intent returned has this boolean extra, it proves that the user has failed to fetch new data
            if (!intent.hasExtra("fetchNewItemsResult")) {
                progressBar.setVisibility(View.GONE);
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public class TopRatedMovieListFetchedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.log_i(TAG, "top rated movie list has been fetched...");
            mSwipeRefreshLayout.setRefreshing(false);

            // if the intent returned has this boolean extra, it proves that the user has failed to fetch new data
            if (!intent.hasExtra("fetchNewItemsResult")) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    public static class FavoriteMovieListFetchedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.log_i(TAG, "favorite movie list has been fetched...");
            mSwipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver myNetworkStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                LogUtil.log_i(TAG, "Network State Change Receiver onReceive!");

                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    LogUtil.log_i(TAG, "Network connected and Refreshing");

                    if (mSwipeRefreshLayout.isRefreshing() || progressBar.getVisibility() == View.VISIBLE) {
                        if (spinner_nav.getSelectedItemPosition() == 0) {
                            Intent intent_popular = new Intent(getActivity(), PopularMoviesUpdaterService.class);
                            intent_popular.putExtra("refresh", true);
                            intent_popular.putExtra("page", 1);
                            intent_popular.putExtra("new", true);
                            getActivity().startService(intent_popular);
                        } else if (spinner_nav.getSelectedItemPosition() == 1) {
                            Intent intent_top_rated = new Intent(getActivity(), TopRatedMoviesUpdaterService.class);
                            intent_top_rated.putExtra("refresh", true);
                            intent_top_rated.putExtra("page", 1);
                            intent_top_rated.putExtra("new", true);
                            getActivity().startService(intent_top_rated);
                        }
                    }

                }
            }
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i == MY_LOADER_ID_POPULAR) {
            return PopularMoviesLoader.newAllPopularMoviesInstance(getActivity());
        }
        if (i == MY_LOADER_ID_TOP_RATED) {
            return TopRatedMoviesLoader.newAllTopRatedMoviesInstance(getActivity());
        }
        if (i == MY_LOADER_ID_FAVORITES) {
            return FavoriteMoviesLoader.newAllFavoriteMoviesInstance(getActivity());
        }
        return null;
    }

    // remember that in fragments, onLoadFinished() would get called every time the fragment refreshes
    // so performing fragment state restoration is better done here than in other fragment lifecycle methods
    // like onCreate() or other ones
    @Override
    public void onLoadFinished(final Loader<Cursor> cursorLoader, final Cursor cursor) {
        LogUtil.log_i(TAG, "onLoadFinished cursor: " + cursor.getCount());

        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (recyclerView.getChildCount() > 0) {
                    // Since we know we're going to get items, we keep the listener around until
                    // we see Children.

                    recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                    AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

                    // only start postponed enter transition when recycler view has been drawn onto the screen
                    appCompatActivity.supportStartPostponedEnterTransition();

                    return true;
                }

                return false;
            }
        });

        // recycler view loads more data when list reaches the end
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(sglm) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // dont show progress bar for favorite list
                        if (cursorLoader.getId() != MY_LOADER_ID_FAVORITES) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        if (cursorLoader.getId() == MY_LOADER_ID_POPULAR) {
                            fetchMorePopularMovies(cursor.getCount() / 20 + 1);
                        } else if (cursorLoader.getId() == MY_LOADER_ID_TOP_RATED) {
                            fetchMoreTopRatedMovies(cursor.getCount() / 20 + 1);
                        }

                    }
                });

            }
        });

        switch (cursorLoader.getId()) {
            case MY_LOADER_ID_POPULAR:
                // as the three loaders share the same recyclerview, one possible bug might be that: when user has
                // liked a movie from either popular or top rated movie entry(in detail fragment), and then return
                // to movie list fragment, since the underlying database data has been changed, this onLoadFinished()
                // will get called and the favorites loader will be triggered which will force the recycler view to display
                //favorites list despite the spinner selection
                // so we have to double check before we swap the cursor

                if (spinner_nav.getSelectedItemPosition() == 0) {
                    if (cursor.moveToFirst()) {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        LogUtil.log_i(TAG, "Bundle Recycler Layout: " + getArguments().getParcelable(BUNDLE_RECYCLER_LAYOUT_POPULAR));
                        if (getArguments().getParcelable(BUNDLE_RECYCLER_LAYOUT_POPULAR) != null) {
                            LogUtil.log_i(TAG, "restoring recycler view position...");
                            sglm.onRestoreInstanceState(getArguments().getParcelable(BUNDLE_RECYCLER_LAYOUT_POPULAR));
                        }

                        getArguments().remove(BUNDLE_RECYCLER_LAYOUT_POPULAR);

                        // before swapping the cursor, first compare the size of the previous list and the new cursor

                        adapter.swapCursor(cursor);

                        LogUtil.log_i(TAG, "just tapped on spinner pop item? " + justTappedOnSpinnerPopItem);

                        if (justTappedOnSpinnerPopItem) {
                            // if spinner item has just been selected, then simply notifyDatasetChanged
                            adapter.notifyDataSetChanged();
                        } else {
                            // if not, then on load finished would only get called when new items have been inserted
                            adapter.notifyItemRangeInserted(cursor.getCount() - 20, 20);
                        }
                        justTappedOnSpinnerPopItem = false;

                        LogUtil.log_i(TAG, "onLoadFinished range of new items inserted: " + (cursor.getCount() - 20) + "-" + cursor.getCount());

                        adapter.setType("popular");
                        adapter.setOnItemClickListener(this);

                        // get the first movie id. if its tablet layout and detail fragment is not showing anything
                        // on startup, then load the detail page for first movie item
                        // first see if we have previously initiated any fragment before
                        if (getFragmentManager().findFragmentByTag(MovieDetailFragment.TAG) == null && mIsDualPane) {
                            // as we cannot perform fragment transaction inside of onLoadFinished(), we simply
                            // send a broadcast to main activity and let it handle fragment transaction for us
                            long firstMovieId = cursor.getLong(cursor.getColumnIndex("movie_id"));
                            Intent intent = new Intent(MainActivity.ACTION_MOVIE_LIST_LOADED);
                            intent.putExtra("first_movie_id", firstMovieId);
                            getActivity().sendBroadcast(intent);
                        }

                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                // NOTE: DON'T FORGET TO DESTROY LOADER WHEN DONE WITH IT!!!!!!!!!!
                //getLoaderManager().destroyLoader(MY_LOADER_ID_POPULAR);
                break;
            case MY_LOADER_ID_TOP_RATED:
                if (spinner_nav.getSelectedItemPosition() == 1) {
                    if (cursor.moveToFirst()) {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        LogUtil.log_i(TAG, "Bundle Recycler Layout: " + getArguments().getParcelable(BUNDLE_RECYCLER_LAYOUT_TOP_RATED));
                        if (getArguments().getParcelable(BUNDLE_RECYCLER_LAYOUT_TOP_RATED) != null) {
                            sglm.onRestoreInstanceState(getArguments().getParcelable(BUNDLE_RECYCLER_LAYOUT_TOP_RATED));
                        }

                        getArguments().remove(BUNDLE_RECYCLER_LAYOUT_TOP_RATED);

                        adapter.swapCursor(cursor);

                        //LogUtil.log_i(TAG, "just tapped on spinner top rated item...");
                        //
                        LogUtil.log_i(TAG, "just tapped on spinner top rated item? " + justTappedOnSpinnerTopRatedItem);

                        if (justTappedOnSpinnerTopRatedItem) {
                            // if spinner item has just been selected, then simply notifyDatasetChanged
                            adapter.notifyDataSetChanged();
                        } else {
                            // if not, then on load finished would only get called when new items have been inserted
                            adapter.notifyItemRangeInserted(cursor.getCount() - 20, 20);
                        }
                        justTappedOnSpinnerTopRatedItem = false;
                        LogUtil.log_i(TAG, "onLoadFinished range of new items inserted: " + (cursor.getCount() - 20) + "-" + cursor.getCount());

                        adapter.setType("top_rated");
                        adapter.setOnItemClickListener(this);
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                // NOTE: DON'T FORGET TO DESTROY LOADER WHEN DONE WITH IT!!!!!!!!!!
                //getLoaderManager().destroyLoader(MY_LOADER_ID_TOP_RATED);
                break;
            case MY_LOADER_ID_FAVORITES:
                LogUtil.log_i(TAG, "favorites cursor: " + cursor);
                mSwipeRefreshLayout.setRefreshing(false);
                if (spinner_nav.getSelectedItemPosition() == 2) {
                    if (cursor.moveToFirst()) {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        LogUtil.log_i(TAG, "Bundle Recycler Layout: " + getArguments().getParcelable(BUNDLE_RECYCLER_LAYOUT_FAVORITES));
                        if (getArguments().getParcelable(BUNDLE_RECYCLER_LAYOUT_FAVORITES) != null) {
                            sglm.onRestoreInstanceState(getArguments().getParcelable(BUNDLE_RECYCLER_LAYOUT_FAVORITES));
                        }

                        getArguments().remove(BUNDLE_RECYCLER_LAYOUT_FAVORITES);

                        LogUtil.log_i(TAG, "swapping favorites cursor...");
                        adapter.swapCursor(cursor);
                        // we dont call notifyItemRangeInserted because we dont implement endless recycler view for favorites cursor
                        adapter.notifyDataSetChanged();

                        adapter.setType("favorite");
                        adapter.setOnItemClickListener(this);
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                break;
        }

    }

    private void fetchMoreTopRatedMovies(int pageNumTopRated) {
        Intent intent_top_rated = new Intent(getActivity(), TopRatedMoviesUpdaterService.class);
        intent_top_rated.putExtra("page", pageNumTopRated);
        intent_top_rated.putExtra("refresh", false);
        intent_top_rated.putExtra("new", true);
        getActivity().startService(intent_top_rated);
    }

    private void fetchMorePopularMovies(int pageNumPopular) {
        Intent intent_popular = new Intent(getActivity(), PopularMoviesUpdaterService.class);
        intent_popular.putExtra("page", pageNumPopular);
        intent_popular.putExtra("refresh", false);
        intent_popular.putExtra("new", true);
        getActivity().startService(intent_popular);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recyclerView.setAdapter(null);
    }


    /**
     * Handles a click on a headline.
     * <p>
     * This causes the configured listener to be notified that a headline was selected.
     */
    @Override
    public void onItemClick(View view, long clickedMovieId, String type) {
        if (null != mMoviePosterSelectedListener) {
            mMoviePosterSelectedListener.onMoviePosterSelected(view, clickedMovieId, type);
        }
    }

}
