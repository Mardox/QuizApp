package biz.modernapps.quizapp.model;

/**
 * Created by Hooman on 30/07/2014.
 */

import android.os.Parcel;
import android.os.Parcelable;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageItem implements Image {

    private String name;
    private String description;
    private String path;

    public ImageItem() {
        super();
    }

    public ImageItem(String name, String description, String path) {
        super();
        this.name = name;
        this.description = description;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPath(String path) {
        this.path = path;
    }



    // IMAGE INTERFACE
    @Override
    public String getTitle() {

        return name;
    }

    @Override
    public String getThumbPath() {

        return null;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getPath() {
        return path;
    }




    //PARECELABLE INTERFACE
    public ImageItem(Parcel in) {
        readFromParcel(in);
    }

    /**
     * Read from parcel.
     *
     * @param in
     *            the in
     */
    private void readFromParcel(Parcel in) {
        name = in.readString();
        description = in.readString();
        path = in.readString();

    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(path);
    }

    /**
     * /** The Constant CREATOR.
     */
    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

}
