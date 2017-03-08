package com.example.dict.content;

import com.example.contentproviderdemo.dummy.DummyItem;

/**
 * Created by frank.bi on 3/6/2017.
 */

public class DictItem extends DummyItem {
    public DictItem(String id, String content, String details) {
        super(id, content, details);
    }

    @Override
    public String toString() {
        return content;
    }
}
