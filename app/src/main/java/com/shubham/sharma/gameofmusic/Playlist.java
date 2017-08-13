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
    List<Audio> list;

    public Playlist() {
        name = "Unnamed";
        list = new ArrayList<>();
    }

    public Playlist(String name) {
        this.name = name;
        list = new ArrayList<>();
    }

    public Playlist(String name, List<Audio> list) {
        this.name = name;
        this.list = list;
    }

    public void add(Audio audio) {
        list.add(audio);
    }

    public void remove(Audio audio) {
        list.remove(audio);
    }

    @Override
    public String toString() {
        return name;
    }
}
