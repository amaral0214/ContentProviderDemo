package com.example.dict;

import android.database.Cursor;

import com.example.dict.content.DictItem;
import com.example.dict.content.Words;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frank on 2017/3/5.
 */

public class DictUtils {
    public static ArrayList<Map<String, String>> convertCursorToMapList(Cursor cursor) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        // 遍历Cursor结果集
        while (cursor.moveToNext()) {
            // 将结果集中的数据存入ArrayList中
            Map<String, String> map = new HashMap<>();
            // 取出查询记录中第2列、第3列的值
            map.put(Words.Word.WORD, cursor.getString(1));
            map.put(Words.Word.DETAIL, cursor.getString(2));
            result.add(map);
        }
        return result;
    }

    public static ArrayList<DictItem> convertCursorToList(Cursor cursor) {
        ArrayList<DictItem> result = new ArrayList<>();
        // 遍历Cursor结果集
        int i = 0;
        do {
            DictItem dictItem = new DictItem(String.valueOf(i++), cursor.getString(1), cursor.getString(2));
            result.add(dictItem);
        } while (cursor.moveToNext());
        return result;
    }
}
