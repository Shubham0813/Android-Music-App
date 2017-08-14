package com.shubham.sharma.gameofmusic;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/*
* BURAK KARAHAN - 7711062 - Java class to represent playlists.
* Extends SugarRecord which is the persistence framework
* so the object is saved on SQL Lite with no SQL manipulation.
* */
public class Playlist extends SugarRecord {

    String name;

    public Playlist() {}

    public Playlist(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
