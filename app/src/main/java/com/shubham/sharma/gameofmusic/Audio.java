package com.shubham.sharma.gameofmusic;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;


public class Audio extends SugarRecord implements Parcelable {

    String mData;
    String mTitle;
    String mAlbum;
    String mArtist;
    byte[] mImage;
    String mDuration;

    public Audio(){}

    public Audio(Uri data, String title, String album, String artist,byte[] image, String duration) {
        this.mData = data.toString();
        this.mTitle = title;
        this.mAlbum = album;
        this.mArtist = artist;
        this.mImage = image;
        this.mDuration = duration;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public Uri getData() {
        return Uri.parse(mData);
    }

    public void setData(Uri data) {
        this.mData = data.toString();
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
        mData = parcel.readString();
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
        parcel.writeString(mData);
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