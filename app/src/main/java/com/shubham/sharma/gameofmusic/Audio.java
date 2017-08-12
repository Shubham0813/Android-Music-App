package com.shubham.sharma.gameofmusic;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Shubham on 2017-08-11.
 */

public class Audio implements Parcelable {

    private Uri mData;
    private String mTitle;
    private String mAlbum;
    private String mArtist;
    private byte[] mImage;
    private String mDuration;

    public Audio(Uri data, String title, String album, String artist,byte[] image, String duration) {
        this.mData = data;
        this.mTitle = title;
        this.mAlbum = album;
        this.mArtist = artist;
        this.mImage = image;
        this.mDuration = duration;
    }

    public Uri getData() {
        return mData;
    }

    public void setData(Uri data) {
        this.mData = data;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        this.mAlbum = album;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public byte[] getImage() {
        return mImage;
    }

    public void setImage(byte[] image) {
        this.mImage = image;
    }

    public String getDuration() {
        return mDuration;
    }

    // Parcelling part
    public Audio(Parcel parcel){
        mData = Uri.parse(parcel.readString());
        mTitle = parcel.readString();
        mAlbum = parcel.readString();
        mArtist = parcel.readString();
        mImage = new byte[parcel.readInt()];
        parcel.readByteArray(mImage);
        mDuration = parcel.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mData.toString());
        parcel.writeString(mTitle);
        parcel.writeString(mAlbum);
        parcel.writeString(mArtist);
        parcel.writeInt(mImage.length);
        parcel.writeByteArray(mImage);
        parcel.writeString(mDuration);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };
}