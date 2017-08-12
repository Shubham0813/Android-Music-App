package com.shubham.sharma.gameofmusic;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Shubham on 2017-08-11.
 */

public class Audio implements Serializable {

    private Uri data;
    private String title;
    private String album;
    private String artist;

    public Audio(Uri data, String title, String album, String artist) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    public Uri getData() {
        return data;
    }

    public void setData(Uri data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
