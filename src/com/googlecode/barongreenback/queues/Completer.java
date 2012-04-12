package com.googlecode.barongreenback.queues;

import java.util.concurrent.Callable;

public interface Completer {
    void complete(Callable<?> task);

    void restart();
}