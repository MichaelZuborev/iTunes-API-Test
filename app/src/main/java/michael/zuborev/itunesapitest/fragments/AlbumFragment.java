package michael.zuborev.itunesapitest.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import michael.zuborev.itunesapitest.MainActivity;
import michael.zuborev.itunesapitest.R;
import michael.zuborev.itunesapitest.adapters.TracksAdapter;
import michael.zuborev.itunesapitest.data.Album;
import michael.zuborev.itunesapitest.data.Track;

//This fragment appears when user tap on an element
public class AlbumFragment extends Fragment {

    private static final String LOG_TAG = AlbumFragment.class.getSimpleName();

    private int mId;
    private Album mAlbum;
    private List<Track> mItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            mId = bundle.getInt("pos", 0);
            Log.d(LOG_TAG, "Got id: " + mId);
        } else {
            mId = 0;
        }

        return inflater.inflate(R.layout.fragment_album, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAlbum = ((MainActivity) getActivity()).getAlbums().get(mId);
        Log.d(LOG_TAG, "Got albums");

        //if no save data -> download it
        if (savedInstanceState == null) {
            Log.d(LOG_TAG, "Launching asyncTask");
            ((MainActivity) getActivity()).getNetworkController().launchTrackAsyncTask(mAlbum.getAlbumID());
            Log.d(LOG_TAG, "asyncTask has been launched");
        } else {
            Log.d(LOG_TAG, "Data has been found");
            mItemList = savedInstanceState.getParcelableArrayList("Items");
            Log.d(LOG_TAG, "List has been written. Setting adapter");
            setTracksAdapter(mItemList);
            Log.d(LOG_TAG, "Adapter has been set");
        }

        TextView mTitle = view.findViewById(R.id.albumTitle_page);
        TextView mAuthor = view.findViewById(R.id.albumAuthor_page);
        TextView mGenreAndYear = view.findViewById(R.id.albumGenreAndYear_page);
        TextView mSongsQuantity = view.findViewById(R.id.albumSongsQuantity_page);
        TextView mPrice = view.findViewById(R.id.albumPrice_page);

        Log.d(LOG_TAG, "PICASSO: downloading an image");
        Picasso.get()
                .load(mAlbum
                        .getImage().toString())
                .into((ImageView) view.findViewById(R.id.albumPic_page));
        Log.d(LOG_TAG, "PICASSO: image has been downloaded");

        mTitle.setText(mAlbum.getTitle());
        mAuthor.setText(mAlbum.getAuthor());
        mGenreAndYear.setText(mAlbum.getGenre() + " " + mAlbum.getYear());

        //Not every album has a price
        if (mAlbum.getPrice() != null) {
            mPrice.setText(mAlbum.getPrice() + " " + mAlbum.getCurrency());
        } else {
            mPrice.setText("");
            Log.d(LOG_TAG, "Album has no price");
        }

        int mQuantity = mAlbum.getSongsQuantity();
        StringBuilder mResult = new StringBuilder();
        if (mQuantity == 1) {
            mResult.append(mQuantity);
            mResult.append(' ');
            mResult.append(getString(R.string.songs_quantity_1));
        } else {
            mResult.append(mQuantity);
            mResult.append(' ');
            mResult.append(getString(R.string.songs_quantity_not_1));
        }
        mSongsQuantity.setText(mResult.toString());
        Log.d(LOG_TAG, "All text has been set");
    }

    public void setTracksAdapter(List<Track> itemList) {
        Log.d(LOG_TAG, "Setting an adapter");
        mItemList = itemList;
        ListView mTracksList = getActivity().findViewById(R.id.albumSongsList_page);
        Log.d(LOG_TAG, "ListView has been created. Creating adapter");
        TracksAdapter mAdapter = new TracksAdapter(getContext(), itemList);
        Log.d(LOG_TAG, "Adapter has been created. Setting it");
        mTracksList.setAdapter(mAdapter);
        Log.d(LOG_TAG, "Adapter has been set");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(LOG_TAG, "Saving itemList");
        outState.putParcelableArrayList("Items", (ArrayList<? extends Parcelable>) mItemList);
        Log.d(LOG_TAG, "List has been saved");

    }
}

