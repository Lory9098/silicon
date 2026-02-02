package org.silicon.api.kernel;

import java.util.concurrent.CompletableFuture;

public interface ComputeEvent {
    default boolean isCompleted() {
        return future().isDone();
    }

    CompletableFuture<Void> future();

    void await();
}
