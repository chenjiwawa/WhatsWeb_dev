package com.qltech.messagesaver.common.executor;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * @author SkeeterWang Created on 2019-05-02.
 */
public abstract class AbstractMainExecutor implements Executor {
    /**
     * remove {@link Runnable} from main thread
     *
     * @param runnable {@link Runnable} to remove
     */
    public abstract void removeCallbacks(@NonNull Runnable runnable);

    /**
     * post {@link Runnable} to execute after delayMillis
     *
     * @param runnable     {@link Runnable} to post
     * @param delayMillis execute delay
     */
    public abstract void postDelayed(@NonNull Runnable runnable, long delayMillis);
}
