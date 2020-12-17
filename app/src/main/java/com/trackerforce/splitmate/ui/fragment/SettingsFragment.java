package com.trackerforce.splitmate.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trackerforce.splitmate.BuildConfig;
import com.trackerforce.splitmate.DashboardActivity;
import com.trackerforce.splitmate.LoginActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.ui.SplitmateView;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

public class SettingsFragment extends Fragment implements SplitmateView {

    private PreferencesFragment settingsFragment;
    private WebViewFragment webviewFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initSettingsView();

        getTextView(R.id.txtSignout).setOnClickListener(this::onSignOut);
        getTextView(R.id.txtEULA).setOnClickListener(this::onClickEULA);
        getTextView(R.id.txtPrivacy).setOnClickListener(this::onClickPrivacyPolicy);
        getComponent(R.id.btnCloseWebview, FloatingActionButton.class).setOnClickListener(this::onCloseWebView);
    }

    private void initSettingsView() {
        final DashboardActivity activity = (DashboardActivity) requireActivity();

        settingsFragment =  new PreferencesFragment();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, settingsFragment)
                .commit();

        toggleFooter(false);
    }

    private void onSignOut(@Nullable View view) {
        final DashboardActivity activity = (DashboardActivity) requireActivity();

        activity.getUserController().logout(new ServiceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(activity.getBaseContext(), error);
            }
        });
    }

    private void onClickEULA(@Nullable View view) {
        final DashboardActivity activity = (DashboardActivity) requireActivity();

        webviewFragment = new WebViewFragment();
        webviewFragment.setUrl(BuildConfig.API_URL + "eula.html");
        activity.getSupportFragmentManager()
                .beginTransaction()
                .remove(settingsFragment)
                .replace(R.id.settings, webviewFragment)
                .commit();

        toggleFooter(true);
    }

    private void onClickPrivacyPolicy(@Nullable View view) {
        final DashboardActivity activity = (DashboardActivity) requireActivity();

        webviewFragment = new WebViewFragment();
        webviewFragment.setUrl(BuildConfig.API_URL + "privacy.html");

        activity.getSupportFragmentManager()
                .beginTransaction()
                .remove(settingsFragment)
                .replace(R.id.settings, webviewFragment)
                .commit();

        toggleFooter(true);
    }

    private void onCloseWebView(@Nullable View view) {
        final DashboardActivity activity = (DashboardActivity) requireActivity();

        activity.getSupportFragmentManager()
                .beginTransaction()
                .remove(webviewFragment)
                .commit();

        initSettingsView();
    }

    private void toggleFooter(boolean hide) {
        if (hide) {
            getComponent(R.id.btnCloseWebview, FloatingActionButton.class).setVisibility(View.VISIBLE);
            getComponent(R.id.linearLayoutSettings, LinearLayout.class).setVisibility(View.GONE);
            getComponent(R.id.imgSplitmate, ImageView.class).setVisibility(View.GONE);
        } else {
            getComponent(R.id.btnCloseWebview, FloatingActionButton.class).setVisibility(View.INVISIBLE);
            getComponent(R.id.linearLayoutSettings, LinearLayout.class).setVisibility(View.VISIBLE);
            getComponent(R.id.imgSplitmate, ImageView.class).setVisibility(View.VISIBLE);
        }
    }

    public static class PreferencesFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreferenceCompat toggleDark = findPreference(SplitConstants.TOGGLE_DARK.toString());
            assert toggleDark != null;
            toggleDark.setOnPreferenceChangeListener((arg0, isEnabled) -> {
                if ((Boolean) isEnabled)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                return true;
            });
        }
    }
}