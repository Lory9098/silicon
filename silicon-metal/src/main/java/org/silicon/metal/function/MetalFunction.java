package org.silicon.metal.function;

import org.silicon.api.SiliconException;
import org.silicon.api.function.ComputeFunction;
import org.silicon.metal.MetalObject;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public final class MetalFunction implements MetalObject, ComputeFunction {

    private static final MethodHandle METAL_MAX_WORK_GROUP_SIZE = MetalObject.find(
        "metal_max_work_group_size",
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
    );

    private final MemorySegment handle;
    private final MetalPipeline pipeline;

    public MetalFunction(MemorySegment handle) {
        this.handle = handle;
        this.pipeline = makePipeline();
    }

    public MetalPipeline makePipeline() {
        return MetalPipeline.makePipeline(this);
    }

    @Override
    public MemorySegment handle() {
        return handle;
    }

    public MetalPipeline pipeline() {
        return pipeline;
    }

    @Override
    public int maxWorkGroupSize() {
        try {
            return (int) METAL_MAX_WORK_GROUP_SIZE.invokeExact(pipeline.handle());
        } catch (Throwable t) {
            throw new SiliconException("maxWorkGroupSize() failed", t);
        }
    }
}
