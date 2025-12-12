package org.compute4j.device;

import org.compute4j.computing.ComputeStream;

public interface ComputeDevice {
    ComputeContext createContext();
    
    ComputeBuffer allocateBytes(long size);
    ComputeBuffer allocateBytes(long size, ComputeStream stream);
    
    // implicit array api
    default ComputeBuffer allocateArray(double[] data) {
        return allocateArray(data, data.length * 8L);
    }
    
    default ComputeBuffer allocateArray(double[] data, ComputeStream stream) {
        return allocateArray(data, data.length * 8L, stream);
    }
    
    default ComputeBuffer allocateArray(float[] data) {
        return allocateArray(data, data.length * 4L);
    }
    
    default ComputeBuffer allocateArray(float[] data, ComputeStream stream) {
        return allocateArray(data, data.length * 4L, stream);
    }
    
    default ComputeBuffer allocateArray(long[] data) {
        return allocateArray(data, data.length * 8L);
    }
    
    default ComputeBuffer allocateArray(long[] data, ComputeStream stream) {
        return allocateArray(data, data.length * 8L, stream);
    }
    
    default ComputeBuffer allocateArray(int[] data) {
        return allocateArray(data, data.length * 4L);
    }
    
    default ComputeBuffer allocateArray(int[] data, ComputeStream stream) {
        return allocateArray(data, data.length * 4L, stream);
    }
    
    default ComputeBuffer allocateArray(short[] data) {
        return allocateArray(data, data.length * 2L);
    }
    
    default ComputeBuffer allocateArray(short[] data, ComputeStream stream) {
        return allocateArray(data, data.length * 2L, stream);
    }
    
    // explicit array api
    ComputeBuffer allocateArray(double[] data, long size);
    ComputeBuffer allocateArray(double[] data, long size, ComputeStream stream);
    ComputeBuffer allocateArray(float[] data, long size);
    ComputeBuffer allocateArray(float[] data, long size, ComputeStream stream);
    ComputeBuffer allocateArray(long[] data, long size);
    ComputeBuffer allocateArray(long[] data, long size, ComputeStream stream);
    ComputeBuffer allocateArray(int[] data, long size);
    ComputeBuffer allocateArray(int[] data, long size, ComputeStream stream);
    ComputeBuffer allocateArray(short[] data, long size);
    ComputeBuffer allocateArray(short[] data, long size, ComputeStream stream);
}
