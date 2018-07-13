package michael.zuborev.itunesapitest;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import michael.zuborev.itunesapitest.data.Album;
import michael.zuborev.itunesapitest.fragments.InitFragment;
import michael.zuborev.itunesapitest.fragments.ScrollFragment;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private List<Album> mAlbums;

    private NetworkController mNetworkController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = findViewById(R.id.toolbarMainActivity);
        setSupportActionBar(mToolbar);

        if (savedInstanceState != null) {
            mAlbums = savedInstanceState.getParcelableArrayList("Albums");
            Log.d(LOG_TAG, "savedInstanceState has been called");
        } else {
            mAlbums = new ArrayList<>();
            Log.d(LOG_TAG, "New list of albums has been created");
        }

        mNetworkController = new NetworkController(this);
        Log.d(LOG_TAG, "NetworkController hs been launched");

        //check if it is the first launch
        if (savedInstanceState == null) {
            Log.d(LOG_TAG, "Creating fragment manager");
            FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            Log.d(LOG_TAG, "Fragment manager has been created. Starting replace fragment transaction");
            mFragmentTransaction.replace(R.id.fragment_placeholder, new InitFragment());
            Log.d(LOG_TAG, "Committing");
            mFragmentTransaction.commit();
            Log.d(LOG_TAG, "Committed");
        }

    }

    public NetworkController getNetworkController() {
        return mNetworkController;
    }

    public List<Album> getAlbums() {
        return mAlbums;
    }

    public void setAlbums(List<Album> albums) {
        sortAlbums(albums);
        mAlbums = albums;
        Log.d(LOG_TAG, "New albums list has been set");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        Log.d(LOG_TAG, "SearchManager has been created");
        SearchView searchView =
                (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        Log.d(LOG_TAG, "SearchView has been created");
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        Log.d(LOG_TAG, "Searchable info was successfully set");
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(LOG_TAG, "Intent caught");

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.d(LOG_TAG, "Creating fragment manager");
            FragmentTransaction mFragmentTransaction = this.getSupportFragmentManager().beginTransaction();
            Log.d(LOG_TAG, "Fragment manager has been created. Starting replace fragment transaction");
            //Adding search inquiry in the new fragment
            Bundle mBundle = new Bundle();
            mBundle.putString("search", intent.getStringExtra(SearchManager.QUERY));
            ScrollFragment mFragment = new ScrollFragment();
            mFragment.setArguments(mBundle);
            mFragmentTransaction.replace(R.id.fragment_placeholder, mFragment);
            Log.d(LOG_TAG, "Committing");
            mFragmentTransaction.commit();
            Log.d(LOG_TAG, "Committed");
        }
    }

    //sorting albums in alphabetical order
    private List<Album> sortAlbums(List<Album> albums) {
        Log.d(LOG_TAG, "Starting albums sorting process");
        Collections.sort(albums, new Comparator<Album>() {
            @Override
            public int compare(Album a1, Album a2) {
                return a1.getTitle().compareToIgnoreCase(a2.getTitle());
            }
        });
        Log.d(LOG_TAG, "Albums has been sorted");

        return albums;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(LOG_TAG, "Putting albums into bundle");
        outState.putParcelableArrayList("Albums", (ArrayList<? extends Parcelable>) mAlbums);
        Log.d(LOG_TAG, "Albums has been set. Saving fragments");
        getSupportFragmentManager().saveFragmentInstanceState(getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder));
        Log.d(LOG_TAG, "Fragments has been saved");
    }
}
