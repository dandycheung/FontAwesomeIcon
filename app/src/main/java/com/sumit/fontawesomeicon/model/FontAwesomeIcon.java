package com.sumit.fontawesomeicon.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sahoos16 on 7/27/2017.
 */

public class FontAwesomeIcon implements Parcelable{

    private int id;
    private String iconUnicode;
    private String iconClassName;
    private int iconColor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIconUnicode() {
        return iconUnicode;
    }

    public void setIconUnicode(String iconUnicode) {
        this.iconUnicode = iconUnicode;
    }

    public String getIconClassName() {
        return iconClassName;
    }

    public void setIconClassName(String iconClassName) {
        this.iconClassName = iconClassName;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.iconUnicode);
        dest.writeString(this.iconClassName);
        dest.writeInt(this.iconColor);
    }

    public FontAwesomeIcon() {
    }

    protected FontAwesomeIcon(Parcel in) {
        this.id = in.readInt();
        this.iconUnicode = in.readString();
        this.iconClassName = in.readString();
        this.iconColor = in.readInt();
    }

    public static final Creator<FontAwesomeIcon> CREATOR = new Creator<FontAwesomeIcon>() {
        @Override
        public FontAwesomeIcon createFromParcel(Parcel source) {
            return new FontAwesomeIcon(source);
        }

        @Override
        public FontAwesomeIcon[] newArray(int size) {
            return new FontAwesomeIcon[size];
        }
    };
}
