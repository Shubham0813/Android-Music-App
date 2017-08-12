package com.shubham.sharma.gameofmusic;

import android.net.Uri;

import com.orm.SugarRecord;

import java.io.Serializable;

public class Audio extends SugarRecord implements Serializable {

    private Uri data;
    private String title;
    private String album;
    private String artist;

    public Audio(){}

    public Audio(Uri data, String title, String album, String artist) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return title;
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
