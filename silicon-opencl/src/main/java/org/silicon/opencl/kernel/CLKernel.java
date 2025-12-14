package org.silicon.opencl.kernel;

import org.silicon.kernel.ComputeFunction;

public record CLKernel(long handle) implements ComputeFunction {
}
