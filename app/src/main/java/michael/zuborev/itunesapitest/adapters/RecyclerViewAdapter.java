package michael.zuborev.itunesapitest.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import michael.zuborev.itunesapitest.MainActivity;
import michael.zuborev.itunesapitest.R;
import michael.zuborev.itunesapitest.fragments.AlbumFragment;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String LOG_TAG = RecyclerViewAdapter.class.getSimpleName();

    private Context mContext;

    public RecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        Log.d(LOG_TAG, "Inflating view");
        view = mLayoutInflater.inflate(R.layout.recyclerview_item, parent, false);
        Log.d(LOG_TAG, "View has been inflated");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mAlbumTitle.setText(((MainActivity) mContext).getAlbums().get(position).getTitle());
        holder.mAuthorName.setText(((MainActivity) mContext).getAlbums().get(position).getAuthor());

        Log.d(LOG_TAG, "PICASSO: downloading images");
        Picasso.get()
                .load(((MainActivity) mContext)
                        .getAlbums().get(position)
                        .getImage().toString())
                .into(holder.mAlbumPic);
        Log.d(LOG_TAG, "PICASSO: downloading complete");

        holder.mRecyclerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Creating fragment manager");
                FragmentTransaction mFragmentTransaction = ((MainActivity) mContext)
                        .getSupportFragmentManager()
                        .beginTransaction();

                Log.d(LOG_TAG, "Fragment manager has been created. Starting replace fragment transaction");
                //Adding position of the element in the new fragment in order to get Album from AlbumList
                Bundle mBundle = new Bundle();
                mBundle.putInt("pos", position);
                AlbumFragment mFragment = new AlbumFragment();
                mFragment.setArguments(mBundle);
                mFragmentTransaction.replace(R.id.fragment_placeholder, mFragment);
                Log.d(LOG_TAG, "Adding to backStack");
                mFragmentTransaction.addToBackStack(null);
                Log.d(LOG_TAG, "Committing");
                mFragmentTransaction.commit();
                Log.d(LOG_TAG, "Committed");
            }
        });
    }

    @Override
    public int getItemCount() {
        return ((MainActivity) mContext).getAlbums().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mAlbumTitle;
        private TextView mAuthorName;
        private ImageView mAlbumPic;
        private CardView mRecyclerItem;

        public ViewHolder(View itemView) {
            super(itemView);

            mAlbumTitle = itemView.findViewById(R.id.albumTitle);
            mAuthorName = itemView.findViewById(R.id.authorName);
            mAlbumPic = itemView.findViewById(R.id.albumPic);
            mRecyclerItem = itemView.findViewById(R.id.recyclerview_item);
        }
    }

}
