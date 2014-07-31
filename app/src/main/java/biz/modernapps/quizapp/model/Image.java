package biz.modernapps.quizapp.model;

import android.os.Parcelable;

/**
 * Created by Hooman on 30/07/2014.
 */
public interface Image extends Parcelable {

    public String getTitle();

    public String getDescription();

    public String getPath();

    public String getThumbPath();

}
