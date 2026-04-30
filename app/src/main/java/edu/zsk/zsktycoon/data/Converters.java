package edu.zsk.zsktycoon.data;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;

public class Converters {
    @TypeConverter
    public static Set<String> fromJson(String json) {
        Set<String> set = new HashSet<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                set.add(arr.getString(i));
            }
        } catch (Exception e) { }
        return set;
    }

    @TypeConverter
    public static String toJson(Set<String> set) {
        return new JSONArray(new ArrayList<>(set)).toString();
    }
}