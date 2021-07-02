package com.trackerforce.splitmate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.MobileAds;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;

import java.util.Objects;

public class MainActivity extends SplitmateActivity implements ServiceCallback<User> {

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MobileAds.initialize(this);
        Config.getInstance().loadSettings(getBaseContext());

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateView() {
        getTextView(R.id.txtVersion).setText(BuildConfig.VERSION_NAME);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setOnClickListener(R.id.buttonGoDashboard, this::initAPI);

        initAPI(getView());
    }

    private void initAPI(@Nullable View view) {
        getComponent(R.id.progressLoading, ProgressBar.class).setVisibility(View.VISIBLE);
        getButton(R.id.buttonGoDashboard).setVisibility(View.GONE);

        userController.checkAPI(new ServiceCallback<String>() {
            @Override
            public void onSuccess(String data) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> initApp(), 2000);
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(MainActivity.this, error);
                getComponent(R.id.progressLoading, ProgressBar.class).setVisibility(View.GONE);
                getButton(R.id.buttonGoDashboard).setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Initialize app
     * <p>
     * Verifies if user already exist and is logged, then it will redirect to the Dashboard view.
     * Otherwise, it will redirect to login/sign up activity
     */
    private void initApp() {
        try {
            userController.syncToken();
            userController.getUser(this);
        } catch (Exception e) {
            AppUtils.showMessage(MainActivity.this, e.getMessage());
        }
    }

    @Override
    public void onSuccess(User data) {
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    @Override
    public void onError(String error) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    @Override
    public void onError(String error, Object obj) {
        AppUtils.showMessage(MainActivity.this, obj.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}