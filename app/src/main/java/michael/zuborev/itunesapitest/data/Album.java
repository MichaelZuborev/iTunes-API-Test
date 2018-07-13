package michael.zuborev.itunesapitest.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

import michael.zuborev.itunesapitest.NetworkController;

public class Album implements Parcelable {
    private String mTitle;
    private String mAuthor;
    private String mGenre;
    private String mPrice;
    private String mCurrency;
    private String mAlbumID;
    private int mYear;
    private URL mImage;
    private int mSongsQuantity;

    public Album(String title, String author, String genre, String price, String currency, String albumID, int year, URL image, int songsQuantity) {
        mTitle = title;
        mAuthor = author;
        mGenre = genre;
        mPrice = price;
        mCurrency = currency;
        mAlbumID = albumID;
        mYear = year;
        mImage = image;
        mSongsQuantity = songsQuantity;
    }

    public Album(Parcel in) {
        mTitle = in.readString();
        mAuthor = in.readString();
        mGenre = in.readString();
        mPrice = in.readString();
        mCurrency = in.readString();
        mAlbumID = in.readString();
        mYear = in.readInt();
        mImage = NetworkController.createUrl(in.readString());
        mSongsQuantity = in.readInt();
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getGenre() {
        return mGenre;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public String getAlbumID() {
        return mAlbumID;
    }

    public int getYear() {
        return mYear;
    }

    public URL getImage() {
        return mImage;
    }

    public int getSongsQuantity() {
        return mSongsQuantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mAuthor);
        parcel.writeString(mGenre);
        parcel.writeString(mPrice);
        parcel.writeString(mCurrency);
        parcel.writeString(mAlbumID);
        parcel.writeInt(mYear);
        parcel.writeString(mImage.toString());
        parcel.writeInt(mSongsQuantity);
    }

    public static final Parcelable.Creator<Album> CREATOR
            = new Parcelable.Creator<Album>() {

        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
