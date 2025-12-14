package org.silicon.opencl.device;

import org.lwjgl.opencl.CL10;
import org.lwjgl.system.MemoryStack;
import org.silicon.computing.ComputeQueue;
import org.silicon.device.ComputeBuffer;
import org.silicon.opencl.computing.CLCommandQueue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record CLBuffer(long handle, CLContext context, long size) implements ComputeBuffer {
    
    @Override
    public CLBuffer copy() {
        return copyAsync(null);
    }
    
    @Override
    public CLBuffer copyInto(ComputeBuffer other) {
        return copyIntoAsync(other, null);
    }
    
    @Override
    public CLBuffer copyAsync(ComputeQueue queue) {
        CLBuffer buffer = context.allocateBytes(size);
        return copyIntoAsync(buffer, queue);
    }
    
    @Override
    public CLBuffer copyIntoAsync(ComputeBuffer other, ComputeQueue queue) {
        if (!(other instanceof CLBuffer buffer)) {
            throw new IllegalArgumentException("Other buffer must be an OpenCL buffer!");
        }
        
        boolean newQueue = queue == null;
        if (newQueue) queue = context.createQueue();
        
        CLCommandQueue clQueue = (CLCommandQueue) queue;
        
        int result = CL10.clEnqueueCopyBuffer(
            clQueue.handle(), buffer.handle(), handle,
            0, 0, size, null, null
        );
        if (result != 0) throw new RuntimeException("clEnqueueCopyBuffer failed: " + result);
        
        if (newQueue) {
            clQueue.awaitCompletion();
            clQueue.release();
        }
        
        return buffer;
    }
    
    @Override
    public void free() {
        CL10.clReleaseMemObject(handle);
    }
    
    @Override
    public void get(byte[] data) {
        if (data.length > size) {
            throw new IllegalArgumentException("Requested read of " + data.length + " bytes, but buffer size is " + size);
        }
        
        CLCommandQueue queue = context.createQueue();
        ByteBuffer buffer = ByteBuffer
            .allocateDirect(data.length)
            .order(ByteOrder.nativeOrder());
        
        CL10.clEnqueueReadBuffer(queue.handle(), handle, true, 0, buffer, null, null);
        buffer.get(data);
        
        queue.awaitCompletion();
        queue.release();
    }
    
    @Override
    public void get(double[] data) {
        long required = (long) data.length * Double.BYTES;
        if (required > size) {
            throw new IllegalArgumentException("Requested read of " + required + " bytes, but buffer size is " + size);
        }
        
        CLCommandQueue queue = context.createQueue();
        ByteBuffer buffer = ByteBuffer.
            allocateDirect((int) required)
            .order(ByteOrder.nativeOrder());
        
        CL10.clEnqueueReadBuffer(queue.handle(), handle, true, 0, buffer, null, null);
        buffer.asDoubleBuffer().get(data);
        
        queue.awaitCompletion();
        queue.release();
    }
    
    @Override
    public void get(float[] data) {
        long required = (long) data.length * Float.BYTES;
        if (required > size) {
            throw new IllegalArgumentException("Requested read of " + required + " bytes, but buffer size is " + size);
        }
        
        CLCommandQueue queue = context.createQueue();
        ByteBuffer buffer = ByteBuffer
            .allocateDirect((int) required)
            .order(ByteOrder.nativeOrder());
        
        CL10.clEnqueueReadBuffer(queue.handle(), handle, true, 0, buffer, null, null);
        buffer.asFloatBuffer().get(data);
        
        queue.awaitCompletion();
        queue.release();
    }
    
    @Override
    public void get(long[] data) {
        long required = (long) data.length * Long.BYTES;
        if (required > size) {
            throw new IllegalArgumentException("Requested read of " + required + " bytes, but buffer size is " + size);
        }
        
        CLCommandQueue queue = context.createQueue();
        ByteBuffer buffer = ByteBuffer
            .allocateDirect((int) required)
            .order(ByteOrder.nativeOrder());
        
        CL10.clEnqueueReadBuffer(queue.handle(), handle, true, 0, buffer, null, null);
        buffer.asLongBuffer().get(data);
        
        queue.awaitCompletion();
        queue.release();
    }
    
    @Override
    public void get(int[] data) {
        long required = (long) data.length * Integer.BYTES;
        if (required > size) {
            throw new IllegalArgumentException("Requested read of " + required + " bytes, but buffer size is " + size);
        }
        
        CLCommandQueue queue = context.createQueue();
        ByteBuffer buffer = ByteBuffer
            .allocateDirect((int) required)
            .order(ByteOrder.nativeOrder());
        
        CL10.clEnqueueReadBuffer(queue.handle(), handle, true, 0, buffer, null, null);
        buffer.asIntBuffer().get(data);
        
        queue.awaitCompletion();
        queue.release();
    }
    
    @Override
    public void get(short[] data) {
        long required = (long) data.length * Short.BYTES;
        if (required > size) {
            throw new IllegalArgumentException("Requested read of " + required + " bytes, but buffer size is " + size);
        }
        
        CLCommandQueue queue = context.createQueue();
        ByteBuffer buffer = ByteBuffer
            .allocateDirect((int) required)
            .order(ByteOrder.nativeOrder());
        
        CL10.clEnqueueReadBuffer(queue.handle(), handle, true, 0, buffer, null, null);
        buffer.asShortBuffer().get(data);
        
        queue.awaitCompletion();
        queue.release();
    }
}
