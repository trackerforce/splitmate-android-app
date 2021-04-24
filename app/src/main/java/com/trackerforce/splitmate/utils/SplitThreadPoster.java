package com.trackerforce.splitmate.utils;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SplitThreadPoster {

    private static final int CORE_THREADS = 4;
    private static final long KEEP_ALIVE_SECONDS = 15L;

    private final ThreadPoolExecutor threadPoolExecutor;

    public SplitThreadPoster() {
        threadPoolExecutor = getThreadPoolExecutor();
    }

    public void post(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    protected ThreadPoolExecutor getThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                CORE_THREADS,
                Integer.MAX_VALUE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new SynchronousQueue<>()
        );
    }
}
