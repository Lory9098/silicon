package org.silicon.opencl.kernel;

import org.lwjgl.opencl.CL10;
import org.lwjgl.system.MemoryStack;
import org.silicon.kernel.ComputeFunction;
import org.silicon.kernel.ComputeModule;

import java.nio.IntBuffer;

public record CLProgram(long handle) implements ComputeModule {
    
    @Override
    public ComputeFunction getFunction(String name) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer result = stack.mallocInt(1);
            
            long kernel = CL10.clCreateKernel(handle, name, result);
            if (result.get(0) != CL10.CL_SUCCESS) throw new RuntimeException("clCreateKernel failed: " + result.get(0));
            
            return new CLKernel(kernel);
        }
    }
}
