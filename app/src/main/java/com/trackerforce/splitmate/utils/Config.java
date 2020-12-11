package com.trackerforce.splitmate.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.trackerforce.splitmate.BuildConfig;
import com.trackerforce.splitmate.model.Jwt;
import com.trackerforce.splitmate.model.Login;
import com.trackerforce.splitmate.model.User;

public class Config {

    private static Config instance;
    public final String API_URL;
    private Login loggedUser;

    private Config() {
        API_URL = BuildConfig.API_URL;
        loggedUser = new Login();
        loggedUser.setJwt(new Jwt());
        loggedUser.setUser(new User());
    }

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();
        return instance;
    }

    public <T> void saveSettings(Context context, SplitConstants key, T value) {
        final SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreference.edit();

        if (value instanceof Boolean) {
            editor.putBoolean(key.toString(), (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key.toString(), (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key.toString(), (Integer) value);
        }

        editor.apply();
    }

    public <T> T getSettings(Context context, SplitConstants key, T defaultValue, Class<T> clazz) {
        final SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);

        T settings = null;
        if (defaultValue instanceof Boolean) {
            settings = clazz.cast(sharedPreference.getBoolean(key.toString(), (Boolean) defaultValue));
        } else if (defaultValue instanceof String) {
            settings = clazz.cast(sharedPreference.getString(key.toString(), (String) defaultValue));
        } else if (defaultValue instanceof Integer) {
            settings = clazz.cast(sharedPreference.getInt(key.toString(), (Integer) defaultValue));
        }

        return settings;
    }

    /**
     * Preload settings when the app startup
     */
    public void loadSettings(Context context) {
        boolean isToggleDark = Config.getInstance().getSettings(
                context, SplitConstants.TOGGLE_DARK, false, Boolean.class);

        if (isToggleDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public String getToken() {
        return loggedUser != null ? loggedUser.getJwt().getToken() : "";
    }

    public Login getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(Login loggedUser) {
        this.loggedUser = loggedUser;
    }

}
