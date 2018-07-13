package michael.zuborev.itunesapitest.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import michael.zuborev.itunesapitest.MainActivity;
import michael.zuborev.itunesapitest.R;
import michael.zuborev.itunesapitest.adapters.RecyclerViewAdapter;

//This fragment appears when user search something
public class ScrollFragment extends Fragment {

    private static final String LOG_TAG = ScrollFragment.class.getSimpleName();

    private String mSearchInquiry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Hiding a keyboard of searchView");
        try {
            SearchView mSearchView = getActivity().findViewById(R.id.app_bar_search);
            mSearchView.clearFocus();
            Log.d(LOG_TAG, "Keyboard has been cleaned");
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "No search widget found, no need to clean focus " + e);
        }

        Log.d(LOG_TAG, "Getting search inquiry");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mSearchInquiry = bundle.getString("search", "");
            Log.d(LOG_TAG, "Search inquiry has been found: " + mSearchInquiry);
        } else {
            mSearchInquiry = "";
            Log.d(LOG_TAG, "No search inquiry has been found");
        }

        return inflater.inflate(R.layout.fragment_scroll, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null & getArguments().getString("search") != null){

            Log.d(LOG_TAG, "Launching asyncTask");
            ((MainActivity) getActivity()).getNetworkController().launchSearchAsyncTask(mSearchInquiry);
            Log.d(LOG_TAG, "AsyncTask has been launched");

        } else {
            Log.d(LOG_TAG, "Launching recyclerView");
            launchRecyclerView();
            Log.d(LOG_TAG, "Recycler view has been launched");
        }

    }

    public void launchRecyclerView() {
        Log.d(LOG_TAG, "Creating recyclerView & creating adapter");
        RecyclerView mRecyclerView = getActivity().findViewById(R.id.recycler_view);
        RecyclerViewAdapter mRecyclerViewAdapter = new RecyclerViewAdapter(getContext());
        Log.d(LOG_TAG, "Adapter and view has been created");

        Log.d(LOG_TAG, "Checking orientation to set adequate gridView span count. Setting layout manager");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
            Log.d(LOG_TAG, "Layout manager has been set. spanCount is 4");
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            Log.d(LOG_TAG, "Layout manager has been set. spanCount is 2");
        }
        Log.d(LOG_TAG, "Setting adapter");
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        Log.d(LOG_TAG, "Adapter has been sent");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSavedInstanceSave has been called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.getArguments().clear();
    }
}
