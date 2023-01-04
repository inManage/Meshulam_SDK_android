package il.co.inmanage.meshulam_sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPreferencesHandler {

    private final HashMap<String, HashMap<String, String>> cache = new HashMap<>();

    private final Context context;

    public SharedPreferencesHandler(Context context) {
        this.context = context;
    }

    public boolean writeToDisk(String filename, String key, String value) {
        inseretIntoCache(filename, key, value);
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_MULTI_PROCESS);
        return prefs.edit().putString(key, value).commit();
    }

    public boolean writeToDisk(String filename, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_MULTI_PROCESS);
        return prefs.edit().putBoolean(key, value).commit();
    }

    public boolean removeFromDisk(String filename, String key) {
        removeFromCache(filename, key);
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_MULTI_PROCESS);
        return prefs.edit().remove(key).commit();
    }

    private void inseretIntoCache(String filename, String key, String text) {
        HashMap<String, String> diskMap = cache.get(filename);
        if (diskMap == null) {
            HashMap<String, String> newMap = new HashMap<>();
            newMap.put(key, text);
            cache.put(filename, newMap);
        } else {
            diskMap.put(key, text);
        }
    }

    private void removeFromCache(String filename, String key) {
        HashMap<String, String> diskMap = cache.get(filename);
        if (diskMap != null) {
            diskMap.remove(key);
        }
    }

    public String readFromDisk(String filename, String key) {
        String toReturn = getFromDiskCache(filename, key);
        if (toReturn == null) {
            SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_MULTI_PROCESS);
            String value = prefs.getString(key, null);
            if (value != null) {
                inseretIntoCache(filename, key, value);
            }
            return value;
        }
        LoggingHelper.d("toReturn = " + toReturn);
        return toReturn;
    }

    public boolean readBooleanFromDisk(String filename, String key){
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_MULTI_PROCESS);
        boolean value = prefs.getBoolean(key,true);
        return value;
    }

    private String getFromDiskCache(String filename, String key) {
        HashMap<String, String> map = cache.get(filename);
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    public HashMap<String, HashMap<String, String>> getCache() {
        return cache;
    }
}
