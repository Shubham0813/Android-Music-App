package com.shubham.sharma.gameofmusic;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

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
