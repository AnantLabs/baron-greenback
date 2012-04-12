package com.googlecode.barongreenback.queues;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static com.googlecode.totallylazy.Function.function;
import static java.lang.Math.max;
import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class CpuBoundedCompleter implements Completer {
    private ExecutorService executors;

    public CpuBoundedCompleter() {
        start();
    }

    public static ExecutorService cpuBoundExecutorService() {
        return newFixedThreadPool(max(1, getRuntime().availableProcessors() - 1));
    }

    @Override
    public void complete(Callable<?> task) {
        executors.execute(function(task));
    }

    @Override
    public void restart() {
        executors.shutdownNow();
        start();
    }

    private void start() {
        executors = cpuBoundExecutorService();
    }
}