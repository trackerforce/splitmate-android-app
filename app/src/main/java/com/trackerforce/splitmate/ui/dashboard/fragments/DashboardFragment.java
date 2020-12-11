package com.trackerforce.splitmate.ui.dashboard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.ui.dashboard.DashboardPageAdapter;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dash_fragment_home, container, false);

        DashboardPageAdapter dashboardAdapter = new DashboardPageAdapter(this.getActivity());

        ViewPager2 viewDashboard = root.findViewById(R.id.viewPagerDashboard);
        viewDashboard.setAdapter(dashboardAdapter);

        TabLayout tabLayout = root.findViewById(R.id.tabDashboard);
        new TabLayoutMediator(tabLayout, viewDashboard, this::createTabMediator).attach();

        return root;
    }

    private void createTabMediator(TabLayout.Tab tab, int position) {
        if (position == 0) {
            tab.setText(DashEventsFragment.TITLE);
            tab.setIcon(android.R.drawable.ic_menu_today);
        } else if (position == 1) {
            tab.setText(DashArchiveFragment.TITLE);
            tab.setIcon(android.R.drawable.ic_menu_save);
        } else {
            tab.setText(DashNotificationsFragment.TITLE);
            tab.setIcon(android.R.drawable.ic_dialog_info);
        }
    }
}