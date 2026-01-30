package org.silicon.computing;

import java.util.concurrent.CompletableFuture;

public interface ComputeEvent {
    default boolean isCompleted() {
        return future().isDone();
    }

    CompletableFuture<Void> future();
}
