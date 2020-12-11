package com.trackerforce.splitmate;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.ui.dashboard.fragments.DashEventsFragment;
import com.trackerforce.splitmate.ui.fragment.FragmentListener;
import com.trackerforce.splitmate.utils.Config;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

public class DashboardActivity extends SplitmateActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private final FragmentListener fragmentListener;

    public DashboardActivity() {
        super(R.layout.activity_dashboard);
        this.fragmentListener = new FragmentListener();
        this.homeAsBack = false;
    }

    @Override
    protected void onCreateView() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_account, R.id.settings).setOpenableLayout(drawer)
                .build();

        final View headerView = navigationView.getHeaderView(0);
        getComponent(headerView, R.id.txtUsername, TextView.class)
                .setText(String.format("@%s", Config.getInstance().getLoggedUser().getUser().getUsername()));

        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            fragmentListener.notifySubscriber(DashEventsFragment.TITLE);
        }
    }

    public FragmentListener getFragmentListener() {
        return fragmentListener;
    }
}