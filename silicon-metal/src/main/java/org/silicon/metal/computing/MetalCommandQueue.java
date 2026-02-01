package org.silicon.metal.computing;

import org.silicon.SiliconException;
import org.silicon.computing.ComputeArgs;
import org.silicon.computing.ComputeQueue;
import org.silicon.computing.ComputeSize;
import org.silicon.device.ComputeArena;
import org.silicon.kernel.ComputeFunction;
import org.silicon.memory.Freeable;
import org.silicon.memory.MemoryState;
import org.silicon.metal.MetalObject;
import org.silicon.metal.device.MetalBuffer;
import org.silicon.metal.kernel.MetalFunction;
import org.silicon.metal.kernel.MetalPipeline;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class MetalCommandQueue implements MetalObject, ComputeQueue, Freeable {

    public static final MethodHandle METAL_CREATE_COMMAND_BUFFER = MetalObject.find(
        "metal_create_command_buffer",
        FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS)
    );

    private final MemorySegment handle;
    private final List<MetalCommandBuffer> commandBuffers;
    private final ComputeArena arena;
    private MemoryState state;

    public MetalCommandQueue(MemorySegment handle, ComputeArena arena) {
        this.handle = handle;
        this.arena = arena;
        this.commandBuffers = new ArrayList<>();
        this.state = MemoryState.ALIVE;
    }

    @Override
    public void dispatch(ComputeFunction function, ComputeSize globalSize, ComputeSize groupSize, ComputeArgs args) {
        dispatchRaw((MetalFunction) function, globalSize, groupSize, args);
    }

    @Override
    public CompletableFuture<Void> dispatchAsync(
        ComputeFunction function,
        ComputeSize globalSize,
        ComputeSize groupSize,
        ComputeArgs args
    ) {
        if (!(function instanceof MetalFunction metalFunction)) {
            throw new IllegalArgumentException("Compute function is not a Metal function");
        }
        
        MetalCommandBuffer commandBuffer = dispatchRaw(metalFunction, globalSize, groupSize, args);
        CompletableFuture<Void> callback = new CompletableFuture<>();

        Thread.startVirtualThread(() -> {
            try {
                commandBuffer.waitUntilCompleted();
                callback.complete(null);
            } catch (Throwable t) {
                callback.completeExceptionally(t);
            } finally {
                commandBuffer.free();
            }
        });

        return callback;
    }

    private MetalCommandBuffer dispatchRaw(MetalFunction function, ComputeSize globalSize, ComputeSize groupSize, ComputeArgs args) {
        MetalPipeline pipeline = function.pipeline();

        MetalCommandBuffer commandBuffer = makeCommandBuffer();
        
        Objects.requireNonNull(globalSize, "Global size must not be null");
        Objects.requireNonNull(groupSize, "Group size must not be null");
        
        if (groupSize.total() <= 0) {
            throw new IllegalArgumentException("Invalid group size");
        }

        try (MetalEncoder encoder = commandBuffer.makeEncoder(pipeline)) {
            setArgs(args, encoder);

            encoder.dispatchThreads(globalSize.x(), globalSize.y(), globalSize.z(), groupSize.x(), groupSize.y(), groupSize.z());
        }

        commandBuffer.commit();
        commandBuffers.add(commandBuffer);

        return commandBuffer;
    }

    @Override
    public void awaitCompletion() {
        if (state != MemoryState.ALIVE) {
            throw new IllegalStateException("Queue is not ALIVE! Current Queue state: " + state);
        }

        if (commandBuffers.isEmpty()) return;

        commandBuffers.getLast().waitUntilCompleted();

        for (MetalCommandBuffer buf : commandBuffers) {
            buf.free();
        }

        commandBuffers.clear();
    }

    private static void setArgs(ComputeArgs args, MetalEncoder encoder) {
        List<Object> argList = args.getArgs();

        for (int i = 0; i < args.size(); i++) {
            switch (argList.get(i)) {
                case Double x -> encoder.setDouble(x, i);
                case Float x -> encoder.setFloat(x, i);
                case Long x -> encoder.setLong(x, i);
                case Integer x -> encoder.setInt(x, i);
                case Short x -> encoder.setShort(x, i);
                case MetalBuffer x -> encoder.setBuffer(x, i);
                default -> throw new IllegalStateException("Unexpected value: " + argList.get(i));
            }
        }
    }

    public MetalCommandBuffer makeCommandBuffer() {
        try {
            MemorySegment ptr = (MemorySegment) METAL_CREATE_COMMAND_BUFFER.invokeExact(handle);
            
            if (ptr == null || ptr.address() == 0) {
                throw new RuntimeException("metalMakeCommandBuffer failed");
            }
            
            return new MetalCommandBuffer(ptr);
        } catch (Throwable e) {
            throw new SiliconException("makeCommandBuffer() failed", e);
        }
    }

    @Override
    public MemorySegment handle() {
        return handle;
    }

    @Override
    public MemoryState state() {
        return state;
    }

    @Override
    public void free() {
        if (!isAlive()) return;

        try {
            METAL_RELEASE_OBJECT.invokeExact(handle);
            state = MemoryState.FREE;
        } catch (Throwable t) {
            throw new SiliconException("free() failed", t);
        }
    }
}

