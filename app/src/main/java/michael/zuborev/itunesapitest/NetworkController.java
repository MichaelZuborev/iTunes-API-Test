package michael.zuborev.itunesapitest;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import michael.zuborev.itunesapitest.data.Album;
import michael.zuborev.itunesapitest.data.Track;
import michael.zuborev.itunesapitest.fragments.AlbumFragment;
import michael.zuborev.itunesapitest.fragments.ScrollFragment;

//This class is supposed to control all actions with web
public class NetworkController {

    private static final String LOG_TAG = NetworkController.class.getSimpleName();

    //2 parts of URL must cover searchInquiry like bread in a sandwich
    private static final String mFirstPartSearchURL = "https://itunes.apple.com/search?term=";
    private static final String mSecondPartSearchURL = "&entity=album&limit=100";

    private static AsyncTask mAsyncTask;

    private Context mContext;

    public NetworkController(Context context) {
        mContext = context;
    }

    public void launchSearchAsyncTask(String searchInquiry) {
        Log.d(LOG_TAG, "launching searchAsyncTask");
        mAsyncTask = new NetworkSearchAsyncTask(parseSearchInquiry(searchInquiry));
        ((NetworkSearchAsyncTask) mAsyncTask).execute();
        Log.d(LOG_TAG, "AsyncTask has been launched");
    }

    public void launchTrackAsyncTask(String albumID) {
        Log.d(LOG_TAG, "launching searchTrackTask");
        mAsyncTask = new NetworkTrackAsyncTask(makeTracksURL(albumID));
        ((NetworkTrackAsyncTask) mAsyncTask).execute();
        Log.d(LOG_TAG, "AsyncTask has been launched");
    }

    private static String makeTracksURL(String albumID) {
        Log.d(LOG_TAG, "Making tracks url");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("itunes.apple.com");
        builder.appendPath("lookup");
        builder.appendQueryParameter("id",albumID);
        builder.appendQueryParameter("entity","song");
        Log.d(LOG_TAG, "Url has been made");
        return builder.toString();
    }

    private static String parseSearchInquiry(String searchInquiry) {
        Log.d(LOG_TAG, "Parsing search inquiry");
        char[] cashInquiry = searchInquiry.toCharArray();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("itunes.apple.com");
        builder.appendPath("search");

        //filter characters
        StringBuilder term = new StringBuilder();
        for (int i = 0; i < cashInquiry.length; i++) {
            if (cashInquiry[i] == ' ') {
                cashInquiry[i] = '+';
                term.append(cashInquiry[i]);
            } else if (Character.isLetterOrDigit(cashInquiry[i]) || cashInquiry[i] == '*'
                    || cashInquiry[i] == '-' || cashInquiry[i] == '_' || cashInquiry[i] == '.') {
                term.append(cashInquiry[i]);
            }
        }

        builder.appendQueryParameter("term", term.toString());
        builder.appendQueryParameter("entity","album");
        builder.appendQueryParameter("limit","100");

        Log.d(LOG_TAG, "SearchInquiry has been parsed");
        return builder.toString();
    }

    private static ArrayList<Album> parseJSONSearch(String jsonResponse) {
        Log.d(LOG_TAG, "parsing JSON search response");
        ArrayList<Album> mAlbumList = new ArrayList<>();

        try {

            JSONObject mMainBody = new JSONObject(jsonResponse);
            JSONArray mResults = mMainBody.getJSONArray("results");
            for (int i = 0; i < mResults.length(); i++) {
                JSONObject mResult = mResults.getJSONObject(i);
                String mTitle = mResult.getString("collectionName");
                String mAuthor = mResult.getString("artistName");
                String mGenre = mResult.getString("primaryGenreName");
                String mCurrency = mResult.getString("currency");
                String mPrice;
                //Not every album has a price
                try {
                    mPrice = mResult.getString("collectionPrice");
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "No price found");
                    mPrice = null;
                    mCurrency = null;
                }
                String mAlbumID = mResult.getString("collectionId");
                int mYear;
                //Parsing date to year
                char[] cashYear = mResult.getString("releaseDate").toCharArray();
                StringBuilder stringYear = new StringBuilder();
                for (int j = 0; j < 4; j++) {
                    stringYear.append(cashYear[j]);
                }
                mYear = Integer.valueOf(stringYear.toString());
                //parsing URL
                URL mImage = createUrl(mResult.getString("artworkUrl100"));
                int mQuantity = mResult.getInt("trackCount");
                Log.d(LOG_TAG, "JSON response has been parsed." +
                        " Adding new album to list. i = " + i);

                mAlbumList.add(new Album(mTitle, mAuthor, mGenre, mPrice,
                        mCurrency, mAlbumID, mYear, mImage, mQuantity));
            }
            Log.d(LOG_TAG, "All albums has been added");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON parsing went wrong " + e);
            e.printStackTrace();
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "Runtime exception " + e);
            e.printStackTrace();
        }

        return mAlbumList;
    }

    private static List<Track> parseJSONTracks(String jsonResponse) {
        Log.d(LOG_TAG, "Parsing JSON tracks response");
        List<Track> mTracks = new ArrayList<>();
        try {
            JSONObject mMainBody = new JSONObject(jsonResponse);
            JSONArray mResults = mMainBody.getJSONArray("results");
            //i = 1 because the first object is album information
            for (int i = 1; i < mResults.length(); i++) {
                JSONObject mTrack = mResults.getJSONObject(i);
                String mTitle = mTrack.getString("trackName");
                String mAuthor = mTrack.getString("artistName");

                Log.d(LOG_TAG, "Adding new track");
                mTracks.add(new Track(mTitle, mAuthor));
            }
            Log.d(LOG_TAG, "All tracks has been parsed and added");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON parsing went wrong " + e);
            e.printStackTrace();
        }

        return mTracks;
    }

    //Create URL from string
    /*@Nullable
    public static URL createUrl(String stringUrl) {
        Log.d(LOG_TAG, "Creating URL");
        URL url;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        Log.d(LOG_TAG, "URL has been created");

        return url;
    }*/

    public static URL createUrl(String stringUrl) {
        Log.d(LOG_TAG, "Creating URL");
        URL url;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        Log.d(LOG_TAG, "URL has been created");

        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        Log.d(LOG_TAG, "Making http request");

        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            Log.d(LOG_TAG, "Opening url connection");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            Log.d(LOG_TAG, "Connecting");
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "HTTP request went wrong " + e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
                Log.d(LOG_TAG, "Disconnecting");
            }
            if (inputStream != null) {
                inputStream.close();
                Log.d(LOG_TAG, "InputStream has been closed");
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.d(LOG_TAG, "Staring reading data");
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
            Log.d(LOG_TAG, "Data has been read");
        }
        return output.toString();
    }

    private class NetworkSearchAsyncTask extends AsyncTask<URL, Void, List<Album>> {

        private String mStringURL;

        private NetworkSearchAsyncTask(String stringURL) {
            mStringURL = stringURL;
        }

        @Override
        protected List<Album> doInBackground(URL... urls) {
            Log.d(LOG_TAG, "AsyncTask starting. Creating url");
            URL url = createUrl(mStringURL);
            ArrayList<Album> mAlbumList;

            String jsonResponse = "";
            if (!isCancelled()) {
                try {
                    Log.d(LOG_TAG, "Making Http request");
                    jsonResponse = makeHttpRequest(url);
                    Log.d(LOG_TAG, "Http request has been made");
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Exception making http request " + e);
                    e.printStackTrace();
                }
            }

            Log.d(LOG_TAG, "Launching json parsing");
            mAlbumList = parseJSONSearch(jsonResponse);
            Log.d(LOG_TAG, "Parsing succeed");

            return mAlbumList;
        }


        @Override
        protected void onPostExecute(List<Album> albums) {
            Log.d(LOG_TAG, "Setting albums");
            ((MainActivity) mContext).setAlbums(albums);

            //We need to launch recycler view adapter with our data
            Log.d(LOG_TAG, "Launching recyclerView");
            Fragment currentFragment = ((MainActivity) mContext).getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_placeholder);
            if (currentFragment instanceof ScrollFragment) {
                ((ScrollFragment) currentFragment).launchRecyclerView();
                Log.d(LOG_TAG, "RecyclerView has been launched");
            }
        }
    }

    private class NetworkTrackAsyncTask extends AsyncTask<URL, Void, List<Track>> {
        private String mStringURL;

        public NetworkTrackAsyncTask(String stringURL) {
            mStringURL = stringURL;
        }

        @Override
        protected List<Track> doInBackground(URL... urls) {
            Log.d(LOG_TAG, "Creating URL");
            URL url = createUrl(mStringURL);
            List<Track> mTracks;

            String jsonResponse = "";
            if (!isCancelled()) {
                try {
                    Log.d(LOG_TAG, "Making HTTP request");
                    jsonResponse = makeHttpRequest(url);
                    Log.d(LOG_TAG, "HTTP request has been made");
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Exception making http request " + e);
                    e.printStackTrace();
                }
            }

            Log.d(LOG_TAG, "Parsing json response");
            mTracks = parseJSONTracks(jsonResponse);
            Log.d(LOG_TAG, "Response has been parsed");

            return mTracks;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            super.onPostExecute(tracks);

            //We need to launch adapter with our data
            Log.d(LOG_TAG, "Launching adapter");
            Fragment currentFragment = ((MainActivity) mContext).getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_placeholder);
            if (currentFragment instanceof AlbumFragment) {
                ((AlbumFragment) currentFragment).setTracksAdapter(tracks);
                Log.d(LOG_TAG, "Adapter has been launched");
            }
        }
    }
}