package com.trackerforce.splitmate.ui.event;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.trackerforce.splitmate.ui.event.fragments.AbstractEventFragment;
import com.trackerforce.splitmate.ui.event.fragments.EventItemsFragment;
import com.trackerforce.splitmate.ui.event.fragments.EventMembersFragment;
import com.trackerforce.splitmate.ui.event.fragments.EventViewFragment;

import java.util.ArrayList;
import java.util.List;

public class EventPageAdapter extends FragmentStateAdapter {

    private List<AbstractEventFragment> fragments;

    public EventPageAdapter(FragmentActivity fa) {
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
        fragments.add(new EventViewFragment());
        fragments.add(new EventItemsFragment());
        fragments.add(new EventMembersFragment());
    }

}
