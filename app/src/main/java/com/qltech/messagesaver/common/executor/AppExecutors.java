/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qltech.messagesaver.common.executor;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Global executor pools for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */

public class AppExecutors {

    private static final String TAG = AppExecutors.class.getSimpleName();

    private static final String EXECUTOR_NAME_SCHEDULED = "executor_thread";
    private static final String EXECUTOR_NAME_DISK_IO = "io_thread";
    private static final String EXECUTOR_NAME_NETWORK = "network_thread";

    private final Executor localIO;
    private final Executor remoteIO;
    private final MainExecutor mainThread;

    private static class Holder {
        private static final AppExecutors INSTANCE = new AppExecutors();
    }

    public static AppExecutors getInstance() {
        return Holder.INSTANCE;
    }

    private AppExecutors(Executor localIO, Executor remoteIO, MainExecutor mainThread) {
        this.localIO = localIO;
        this.remoteIO = remoteIO;
        this.mainThread = mainThread;
    }

    private AppExecutors() {
        this(diskIoExecutor(), networkExecutor(), new MainExecutor());
    }

    private static ScheduledExecutorService scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(16,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r, EXECUTOR_NAME_SCHEDULED);
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    }
                });
    }

    private static ExecutorService diskIoExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r, EXECUTOR_NAME_DISK_IO);
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    }
                });
    }

    private static ExecutorService networkExecutor() {
        return new ThreadPoolExecutor(3, 6, 1000, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(40),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r, EXECUTOR_NAME_NETWORK);
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    }
                });
    }

    /**
     * 磁盘IO线程池（单线程）
     * <p>
     * 和磁盘操作有关的进行使用此线程(如读写数据库,读写文件)
     * 禁止延迟,避免等待
     * 此线程不用考虑同步问题
     */
    public Executor diskIO() {
        return localIO;
    }

    /**
     * 操作Database專用线程池
     */
    public Executor dbIO() {
        return localIO;
    }

    /**
     * 网络IO线程池
     * <p>
     * 网络请求,异步任务等适用此线程
     * 不建议在这个线程 sleep 或者 wait
     */
    public Executor networkIO() {
        return remoteIO;
    }

    /**
     * UI线程
     * <p>
     * Android 的MainThread
     * UI线程不能做的事情这个都不能做
     */
    public MainExecutor mainThread() {
        return mainThread;
    }

    public static class MainExecutor extends AbstractMainExecutor {
        private Handler mainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            mainHandler.post(runnable);
        }

        @Override
        public void removeCallbacks(@NonNull Runnable runnable) {
            mainHandler.removeCallbacks(runnable);
        }

        @Override
        public void postDelayed(@NonNull Runnable runnable, long delayMillis) {
            mainHandler.postDelayed(runnable, delayMillis);
        }
    }
}
