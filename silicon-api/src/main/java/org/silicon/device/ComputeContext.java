package org.silicon.device;

import org.silicon.computing.ComputeQueue;
import org.silicon.kernel.ComputeModule;

import java.io.InputStream;
import java.nio.file.Path;

public interface ComputeContext {
    
    default ComputeModule loadModuleFromResources(String resourcePath) throws Throwable {
        try (InputStream in = ComputeContext.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            
            byte[] src = in.readAllBytes();
            return loadModule(src);
        }
    }
    
    ComputeQueue createQueue() throws Throwable;
    ComputeModule loadModule(Path path) throws Throwable;
    ComputeModule loadModule(byte[] rawSrc) throws Throwable;
    ComputeModule loadModule(String source) throws Throwable;
    
    ComputeBuffer allocateBytes(long size) throws Throwable;
    ComputeBuffer allocateArray(byte[] data, long size) throws Throwable;
    ComputeBuffer allocateArray(byte[] data, long size, ComputeQueue queue) throws Throwable;
    ComputeBuffer allocateArray(double[] data, long size) throws Throwable;
    ComputeBuffer allocateArray(double[] data, long size, ComputeQueue queue) throws Throwable;
    ComputeBuffer allocateArray(float[] data, long size) throws Throwable;
    ComputeBuffer allocateArray(float[] data, long size, ComputeQueue queue) throws Throwable;
    ComputeBuffer allocateArray(long[] data, long size) throws Throwable;
    ComputeBuffer allocateArray(long[] data, long size, ComputeQueue queue) throws Throwable;
    ComputeBuffer allocateArray(int[] data, long size) throws Throwable;
    ComputeBuffer allocateArray(int[] data, long size, ComputeQueue queue) throws Throwable;
    ComputeBuffer allocateArray(short[] data, long size) throws Throwable;
    ComputeBuffer allocateArray(short[] data, long size, ComputeQueue queue) throws Throwable;
    
    default ComputeBuffer allocateArray(byte[] data) throws Throwable {
        return allocateArray(data, data.length);
    }
    
    default ComputeBuffer allocateArray(byte[] data, ComputeQueue queue) throws Throwable {
        return allocateArray(data, data.length, queue);
    }
    
    default ComputeBuffer allocateArray(double[] data) throws Throwable {
        return allocateArray(data, data.length * 8L);
    }
    
    default ComputeBuffer allocateArray(double[] data, ComputeQueue queue) throws Throwable {
        return allocateArray(data, data.length * 8L, queue);
    }
    
    default ComputeBuffer allocateArray(float[] data) throws Throwable {
        return allocateArray(data, data.length * 4L);
    }
    
    default ComputeBuffer allocateArray(float[] data, ComputeQueue queue) throws Throwable {
        return allocateArray(data, data.length * 4L, queue);
    }
    
    default ComputeBuffer allocateArray(long[] data) throws Throwable {
        return allocateArray(data, data.length * 8L);
    }
    
    default ComputeBuffer allocateArray(long[] data, ComputeQueue queue) throws Throwable {
        return allocateArray(data, data.length * 8L, queue);
    }
    
    default ComputeBuffer allocateArray(int[] data) throws Throwable {
        return allocateArray(data, data.length * 4L);
    }
    
    default ComputeBuffer allocateArray(int[] data, ComputeQueue queue) throws Throwable {
        return allocateArray(data, data.length * 4L, queue);
    }
    
    default ComputeBuffer allocateArray(short[] data) throws Throwable {
        return allocateArray(data, data.length * 2L);
    }
    
    default ComputeBuffer allocateArray(short[] data, ComputeQueue queue) throws Throwable {
        return allocateArray(data, data.length * 2L, queue);
    }
}
