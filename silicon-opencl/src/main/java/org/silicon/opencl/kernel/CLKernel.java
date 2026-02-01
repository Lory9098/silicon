package org.silicon.opencl.kernel;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.system.MemoryStack;
import org.silicon.SiliconException;
import org.silicon.kernel.ComputeFunction;

public record CLKernel(long handle, long device) implements ComputeFunction {
    @Override
    public int maxWorkGroupSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer buffer = stack.mallocPointer(1);
            int err = CL10.clGetKernelWorkGroupInfo(
                handle, device,
                CL10.CL_KERNEL_WORK_GROUP_SIZE,
                buffer, null
            );
            
            if (err != CL10.CL_SUCCESS) {
                throw new SiliconException("clGetKernelWorkGroupInfo failed: " + err);
            }
            
            long max = buffer.get(0);
            return (int) max;
        }
    }
}
