package michael.zuborev.itunesapitest.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import michael.zuborev.itunesapitest.R;
import michael.zuborev.itunesapitest.data.Track;

public class TracksAdapter extends BaseAdapter {

    private static final String LOG_TAG = TracksAdapter.class.getSimpleName();

    private List<Track> mItemList;
    private LayoutInflater mLayoutInflater;

    public TracksAdapter(Context context, List<Track> itemList) {
        mItemList = itemList;
        Log.d(LOG_TAG, "Getting inflater");
        mLayoutInflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d(LOG_TAG, "Have got inflater");
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View mResultView;

        if (view != null) {
            mResultView = view;
        } else {
            mResultView = mLayoutInflater.inflate(R.layout.track_list_item, viewGroup, false);
        }

        Track mItem = mItemList.get(i);

        Log.d(LOG_TAG, "TextView init");
        TextView mTitle = mResultView.findViewById(R.id.trackTitle);
        TextView mAuthor = mResultView.findViewById(R.id.trackAuthor);


        Log.d(LOG_TAG, "TextView setting text");

        mTitle.setText(mItem.getTitle());
        mAuthor.setText(mItem.getAuthor());

        Log.d(LOG_TAG, "text has been set");

        return mResultView;
    }
}
