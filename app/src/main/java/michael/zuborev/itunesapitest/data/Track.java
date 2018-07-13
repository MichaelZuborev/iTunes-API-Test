package michael.zuborev.itunesapitest.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable {
    private String mTitle;
    private String mAuthor;

    public Track(String title, String author) {
        mTitle = title;
        mAuthor = author;
    }

    public Track(Parcel in) {
        mTitle = in.readString();
        mAuthor = in.readString();
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mAuthor);
    }

    public static final Parcelable.Creator<Track> CREATOR
            = new Parcelable.Creator<Track>() {

        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}
