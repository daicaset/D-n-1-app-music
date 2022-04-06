package com.media.musicplayer.share_pre;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class AppPre {
    private static AppPre appPref;
    private SharedPreferences pref;

    public static AppPre getInstance(Context context) {
        if (appPref == null) {
            appPref = new AppPre(context);
        }
        return appPref;
    }

    private AppPre(Context context) {
        pref = context.getSharedPreferences("myPre",Context.MODE_PRIVATE);
    }

    public void putString(final String key, final String newValue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, newValue);
        editor.apply();
    }

    public String getString(final String key, final String defValue) {
        return pref.getString(key, defValue);
    }

    public void putStringSet(final String key, final Set<String> value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public Set<String> getStringSet(final String key, final Set<String> defValue) {
        return pref.getStringSet(key, defValue);
    }

    public void putInt(final String key, final int newValue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, newValue);
        editor.apply();
    }

    public int getInt(final String key, final int defValue) {
        return pref.getInt(key, defValue);
    }

    public void putFloat(final String key, final float newValue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, newValue);
        editor.apply();
    }

    public float getFloat(final String key, final float defValue) {
        return pref.getFloat(key, defValue);
    }

    public void putLong(final String key, final long newValue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, newValue);
        editor.apply();
    }

    public long getLong(final String key, final long defValue) {
        return pref.getLong(key, defValue);
    }

    public void putBoolean(final String key, final Boolean newValue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, newValue);
        editor.apply();
    }

    public boolean getBoolean(final String key, final Boolean defValue) {
        return pref.getBoolean(key, defValue);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.apply();
    }
}
