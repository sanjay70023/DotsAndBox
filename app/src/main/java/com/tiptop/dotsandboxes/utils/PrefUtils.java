package com.tiptop.dotsandboxes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * utility for shared preferences
 * <p>
 * contains setters and getters for different types of data
 */
public class PrefUtils {

    //preference file
    private static final String DEFAULT_PREFS = "default_shared_prefs";

    //any numeric getter method will return -1 as default value
    private static final int DEFAULT_NUMERIC_VALUE = -1;

    //any string getter method will return empty string as default value
    private static final String DEFAULT_STRING_VALUE = "";

    private final Context mContext;
    private final Gson mGson;

    public PrefUtils(Context context) {
        //no direct instances allowed. use di instead.
        mContext = context;
        mGson = new Gson();
    }

    public void setString(String key, @Nullable String value) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(key, DEFAULT_STRING_VALUE);
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public void setLong(String key, long value) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        return prefs.getLong(key, DEFAULT_NUMERIC_VALUE);
    }

    public void setInteger(String key, int value) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInteger(String key) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(key, DEFAULT_NUMERIC_VALUE);
    }

    public void setFloat(String key, float value) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        return prefs.getFloat(key, (float) DEFAULT_NUMERIC_VALUE);
    }

    /**
     * to set pojo object in preferences. will store json string of it.
     */
    public void setObject(String key, @NonNull Object value) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key, mGson.toJson(value));
        editor.apply();
    }

    /**
     * to get pojo object from json stored in preferences.
     * returns null if key doesn't exist in preferences
     */
    @Nullable
    public <T> T getObject(String key, Class<T> pojoClass) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(key, null);
        if (jsonString == null) {
            return null;
        }
        return mGson.fromJson(jsonString, pojoClass);
    }

    /**
     * fetches all key-value pairs from preferences in the form of map
     */
    public Map<String, ?> getAll() {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        return prefs.getAll();
    }

    /**
     * removes particular key (and its associated value) from preferences
     */
    public void removeKey(String key) {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * clears all key-value pairs in shared preferences
     */
    public void clearAll() {
        SharedPreferences prefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * check particular key is available or not before accessing
     */
    public boolean checkKeyAvailable(String key) {
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(DEFAULT_PREFS, Context.MODE_PRIVATE);
        return sharedPrefs.contains(key);
    }
}
