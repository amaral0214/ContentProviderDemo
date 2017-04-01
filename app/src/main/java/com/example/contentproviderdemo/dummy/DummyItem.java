package com.example.contentproviderdemo.dummy;

/**
 * Created by frank.bi on 3/6/2017.
 */

public class DummyItem {
    public final int id;
    public final String content;
    public final String details;

    public DummyItem(int id, String content, String details) {
        this.id = id;
        this.content = content;
        this.details = details;
    }

    @Override
    public String toString() {
        return content;
    }
}