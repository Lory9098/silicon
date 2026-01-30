package org.silicon.opencl.computing;

import org.lwjgl.opencl.CL10;
import org.silicon.computing.ComputeEvent;

import java.util.concurrent.CompletableFuture;

public class CLEvent implements ComputeEvent {

    private final CompletableFuture<Void> callback = new CompletableFuture<>();

    public CLEvent(long eventPtr) {
        Thread.startVirtualThread(() -> {
            try {
                CL10.clWaitForEvents(eventPtr);
                callback.complete(null);
            } catch (Throwable t) {
                callback.completeExceptionally(t);
            } finally {
                CL10.clReleaseEvent(eventPtr);
            }
        });
    }

    @Override
    public boolean isCompleted() {
        return callback.isDone();
    }

    @Override
    public CompletableFuture<Void> future() {
        return callback;
    }
}
