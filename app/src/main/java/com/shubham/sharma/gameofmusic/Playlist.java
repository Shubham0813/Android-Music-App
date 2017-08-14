package com.shubham.sharma.gameofmusic;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

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
