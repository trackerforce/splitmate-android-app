package com.trackerforce.splitmate.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.trackerforce.splitmate.ui.dashboard.fragments.DashEventsFragment;
import com.trackerforce.splitmate.ui.dashboard.fragments.DashArchiveFragment;
import com.trackerforce.splitmate.ui.dashboard.fragments.DashNotificationsFragment;

public class DashboardPageAdapter extends FragmentStateAdapter {

    public DashboardPageAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new DashArchiveFragment();
            case 2:
                return new DashNotificationsFragment();
            default:
                return new DashEventsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
