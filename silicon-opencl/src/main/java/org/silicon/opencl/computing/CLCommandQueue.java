package org.silicon.opencl.computing;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.system.MemoryStack;
import org.silicon.computing.ComputeArgs;
import org.silicon.computing.ComputeQueue;
import org.silicon.computing.ComputeSize;
import org.silicon.kernel.ComputeFunction;
import org.silicon.opencl.device.CLBuffer;
import org.silicon.opencl.kernel.CLKernel;

import java.nio.IntBuffer;
import java.util.List;

public class CLCommandQueue implements ComputeQueue {
    
    private final long handle;
    private volatile long lastEvent = 0L;
    
    public CLCommandQueue(long handle) {
        this.handle = handle;
    }
    
    public long handle() {
        return handle;
    }
    
    private PointerBuffer toBuffer(ComputeSize size, MemoryStack stack) {
        int dim = size.workDim();
        PointerBuffer buf = stack.mallocPointer(dim);
        buf.put(0, size.x());
        if (dim > 1) buf.put(1, size.y());
        if (dim > 2) buf.put(2, size.z());
        return buf;
    }
    
    private void replaceLastEvent(long newEvent) {
        long old = this.lastEvent;
        this.lastEvent = newEvent;
        
        if (old != 0L) {
            CL10.clReleaseEvent(old);
        }
    }
    
    @Override
    public void dispatch(ComputeFunction function, ComputeSize globalSize, ComputeSize groupSize, ComputeArgs args) {
        if (!(function instanceof CLKernel(long kernelHandle))) {
            throw new IllegalArgumentException("Compute function is not an OpenCL kernel!");
        }
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            List<Object> computeArgs = args.getArgs();
            for (int i = 0; i < args.size(); i++) {
                Object arg = computeArgs.get(i);
                switch (arg) {
                    case Byte val -> CL10.clSetKernelArg(kernelHandle, i, stack.bytes(val));
                    case Double val -> CL10.clSetKernelArg(kernelHandle, i, stack.doubles(val));
                    case Float val -> CL10.clSetKernelArg(kernelHandle, i, stack.floats(val));
                    case Integer val -> CL10.clSetKernelArg(kernelHandle, i, stack.ints(val));
                    case Long val -> CL10.clSetKernelArg(kernelHandle, i, stack.longs(val));
                    case Short val -> CL10.clSetKernelArg(kernelHandle, i, stack.shorts(val));
                    case String val -> CL10.clSetKernelArg(kernelHandle, i, stack.ASCII(val));
                    case CLBuffer val -> CL10.clSetKernelArg(kernelHandle, i, stack.pointers(val.handle()));
                    default -> throw new IllegalStateException("Unexpected value: " + arg);
                }
            }
            
            ComputeSize fixedLocal  = fixLocalSize(globalSize, groupSize);
            ComputeSize fixedGlobal = fixGlobalSize(globalSize, fixedLocal);
            
            PointerBuffer globalBuf = toBuffer(fixedGlobal, stack);
            PointerBuffer localBuf  = fixedLocal != null ? toBuffer(fixedLocal, stack) : null;
            
            PointerBuffer eventBuf = stack.mallocPointer(1);
            
            int err = CL10.clEnqueueNDRangeKernel(
                handle,
                kernelHandle, // cl_kernel
                globalSize.workDim(), // work_dim
                null, // global offset
                globalBuf, // global size
                localBuf,
                null, // wait list (in-order queue)
                eventBuf // event out
            );
            if (err != CL10.CL_SUCCESS) throw new IllegalStateException("clEnqueueNDRangeKernel failed: " + err);
            
            long kernelEvent = eventBuf.get(0);
            replaceLastEvent(kernelEvent);
        }
    }
    
    @Override
    public void awaitCompletion() {
        long event = lastEvent;
        if (event != 0L) CL10.clWaitForEvents(event);
    }
    
    @Override
    public void release() {
        CL10.clReleaseCommandQueue(handle);
    }
    
    @Override
    public boolean isCompleted() {
        long event = lastEvent;
        if (event == 0L) return true;
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer status = stack.mallocInt(1);
            
            int err = CL10.clGetEventInfo(event, CL10.CL_EVENT_COMMAND_EXECUTION_STATUS, status, null);
            if (err != CL10.CL_SUCCESS) throw new IllegalStateException("clGetEventInfo failed: " + err);
            
            return status.get(0) == CL10.CL_COMPLETE;
        }
    }
    
    private ComputeSize fixGlobalSize(ComputeSize global, ComputeSize local) {
        if (local == null) return global;
        
        int x = global.x();
        int lx = local.x();
        
        if (lx > x) lx = x;
        
        int fixedX = ((x + lx - 1) / lx) * lx;
        
        return new ComputeSize(fixedX, 1, 1);
    }
    
    private ComputeSize fixLocalSize(ComputeSize global, ComputeSize local) {
        if (local == null) return null;
        
        int lx = local.x();
        if (lx > global.x()) lx = global.x();
        
        return new ComputeSize(lx, 1, 1);
    }
}
