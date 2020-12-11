package com.trackerforce.splitmate.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.trackerforce.splitmate.ui.dashboard.fragments.AbstractDashFragment;
import com.trackerforce.splitmate.ui.dashboard.fragments.DashEventsFragment;
import com.trackerforce.splitmate.ui.dashboard.fragments.DashArchiveFragment;
import com.trackerforce.splitmate.ui.dashboard.fragments.DashNotificationsFragment;

import java.util.ArrayList;
import java.util.List;

public class DashboardPageAdapter extends FragmentStateAdapter {

    private List<AbstractDashFragment> fragments;

    public DashboardPageAdapter(FragmentActivity fa) {
        super(fa);
        initFragments();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(new DashEventsFragment());
        fragments.add(new DashArchiveFragment());
        fragments.add(new DashNotificationsFragment());
    }

}
