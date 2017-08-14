package com.shubham.sharma.gameofmusic;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

public class Audio extends SugarRecord {

/*
* BURAK KARAHAN - 7711062 - Java class to represent every music.
* Extends SugarRecord which is the persistence framework
* so the object is saved on SQL Lite with no SQL manipulation.
* */

    String mData;
    String mTitle;
    String mAlbum;
    String mArtist;
    byte[] mImage;
    String mDuration;
    Playlist mPlaylist;
    String mGenre;
    boolean isSelected;

    public Audio(){}

    public Audio(Uri data, String title, String album, String artist,byte[] image, String duration,
                 String genre) {
        this.mData = data.toString();
        this.mTitle = title;
        this.mAlbum = album;
        this.mArtist = artist;
        this.mImage = image;
        this.mDuration = duration;
        this.mPlaylist = null;
        if(genre == null || genre.isEmpty())
            this.mGenre = "";
        else
            this.mGenre = genre;
        this.isSelected = false;
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

    public Playlist getPlaylist() {
        return mPlaylist;
    }

    public void setPlaylist(Playlist playlist) {
        this.mPlaylist = playlist;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Audio)) return false;
        Audio audio = (Audio) o;
        if(this.getId() == audio.getId()) return true;
        return false;
    }

    public String getGenre() {
        return mGenre;
    }

    public void setGenre(String genre) {
        mGenre = genre;
    }
}