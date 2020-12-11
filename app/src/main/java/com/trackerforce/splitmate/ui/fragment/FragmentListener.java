package com.trackerforce.splitmate.ui.fragment;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FragmentListener {

    private final List<IFragmentSubscriber> subscribers;

    public FragmentListener() {
        subscribers = new ArrayList<>();
    }

    public void subscribe(IFragmentSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void notifySubscribers(@Nullable Object... args) {
        for (IFragmentSubscriber s : subscribers) {
            s.notifyFragment(args);
        }
    }

    public void notifySubscriber(String fragmentTag, @Nullable Object... args) {
        for (IFragmentSubscriber s : subscribers) {
            if (s.getFragmentTag().equals(fragmentTag)) {
                s.notifyFragment(args);
                break;
            }
        }
    }
}
