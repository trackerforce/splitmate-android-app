package com.trackerforce.splitmate.ui.fragment;

import androidx.annotation.Nullable;

public interface IFragmentSubscriber {

    void notifyFragment(@Nullable Object... args);

    String getFragmentTag();

}
