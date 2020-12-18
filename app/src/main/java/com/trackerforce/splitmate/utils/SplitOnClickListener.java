package com.trackerforce.splitmate.utils;

import android.os.SystemClock;
import android.view.View;

public abstract class SplitOnClickListener implements View.OnClickListener {

    private long lastClick = 0;

    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - lastClick < 1000)
            return;

        lastClick = SystemClock.elapsedRealtime();
        onClicking(view);
    }

    protected abstract void onClicking(View view);

}
