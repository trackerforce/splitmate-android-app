package com.trackerforce.splitmate.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.trackerforce.splitmate.DashboardActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.event.EventController;
import com.trackerforce.splitmate.controller.user.UserController;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.utils.SplitConstants;

public abstract class SplitmateActivity extends AppCompatActivity implements SplitmateView {

    private final int layout;
    protected UserController userController;
    protected EventController eventController;
    protected boolean homeAsBack = true;

    public SplitmateActivity(int layout) {
        this.layout = layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);

        userController = new UserController(this);
        eventController = new EventController(this);

        onCreateView();
        initActivityData();
    }

    @Override
    public View getView() {
        return getWindow().getDecorView();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        setToggleLabel(menu.getItem(1));
        setToggleDarkLabel(menu.getItem(2));
        return super.onMenuOpened(featureId, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (homeAsBack)
                    return onHome();
                break;
            case R.id.menuItemDashboad:
                return onDashboard();
            case R.id.toggleConn:
                return onToggleConnection(item);
            case R.id.toggleUI:
                return onToggleUI(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private boolean onHome() {
        finish();
        return true;
    }

    private boolean onDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
        return true;
    }

    private boolean onToggleConnection(MenuItem item) {
        boolean isToggleOffline = Config.getInstance().getSettings(
                getBaseContext(), SplitConstants.TOGGLE_OFFLINE, false);

        if (isToggleOffline) {
            item.setTitle(R.string.switcherOnline);
            updateSettings(false);
            Config.getInstance().saveSettings(getBaseContext(), SplitConstants.TOGGLE_OFFLINE, false);
        } else {
            item.setTitle(R.string.switcherOffline);
            updateSettings(true);
            Config.getInstance().saveSettings(getBaseContext(), SplitConstants.TOGGLE_OFFLINE, true);
        }

        return true;
    }

    private boolean onToggleUI(MenuItem item) {
        boolean isToggleDark = Config.getInstance().getSettings(
                getBaseContext(), SplitConstants.TOGGLE_DARK, false);

        if (isToggleDark) {
            item.setTitle(R.string.switcherDark);
            Config.getInstance().saveSettings(getBaseContext(), SplitConstants.TOGGLE_DARK, false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            item.setTitle(R.string.switchLight);
            Config.getInstance().saveSettings(getBaseContext(), SplitConstants.TOGGLE_DARK, true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        return true;
    }

    private void setToggleLabel(MenuItem menuItem) {
        boolean isToggleOffline = Config.getInstance().getSettings(
                getBaseContext(), SplitConstants.TOGGLE_OFFLINE, false);

        if (isToggleOffline) {
            menuItem.setTitle(R.string.switcherOnline);
        } else {
            menuItem.setTitle(R.string.switcherOffline);
        }
    }

    private void setToggleDarkLabel(MenuItem menuItem) {
        boolean isToggleDark = Config.getInstance().getSettings(
                getBaseContext(), SplitConstants.TOGGLE_DARK, false);

        if (isToggleDark) {
            menuItem.setTitle(R.string.switchLight);
        } else {
            menuItem.setTitle(R.string.switcherDark);
        }
    }

    private void updateSettings(boolean newValue) {
        PreferenceFragmentCompat fragment =
                (PreferenceFragmentCompat) getSupportFragmentManager().findFragmentById(R.id.settings);

        if (fragment != null) {
            SwitchPreferenceCompat toggleDark = fragment.findPreference("TOGGLE_OFFLINE");
            assert toggleDark != null;
            toggleDark.setChecked(newValue);
        }
    }

    public int getLayout() {
        return layout;
    }

    public UserController getUserController() {
        return userController;
    }

    public EventController getEventController() {
        return eventController;
    }

    protected abstract void onCreateView();

    protected void initActivityData() {}
}
