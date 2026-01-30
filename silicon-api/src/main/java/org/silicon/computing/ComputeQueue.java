package org.silicon.computing;

import org.silicon.kernel.ComputeFunction;
import org.silicon.memory.Freeable;

import java.util.concurrent.CompletableFuture;

public interface ComputeQueue extends Freeable {
    void dispatch(
        ComputeFunction function,
        ComputeSize globalSize,
        ComputeSize groupSize,
        ComputeArgs args
    );

    CompletableFuture<Void> dispatchAsync(
        ComputeFunction function,
        ComputeSize globalSize,
        ComputeSize groupSize,
        ComputeArgs args
    );

    void awaitCompletion();
}
