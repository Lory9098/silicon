package org.silicon.metal.computing;

import org.silicon.computing.ComputeEvent;

import java.util.concurrent.CompletableFuture;

public class MetalEvent implements ComputeEvent {

    private final CompletableFuture<Void> callback = new CompletableFuture<>();

    public MetalEvent(MetalCommandBuffer buffer) {
        Thread.startVirtualThread(() -> {
            try {
                buffer.waitUntilCompleted();
                callback.complete(null);
            } catch (Throwable t) {
                callback.completeExceptionally(t);
            } finally {
                buffer.free();
            }
        });
    }

    @Override
    public CompletableFuture<Void> future() {
        return callback;
    }
}
