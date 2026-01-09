package org.silicon.opencl.device;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.system.MemoryStack;
import org.silicon.device.ComputeContext;
import org.silicon.device.ComputeDevice;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

public record CLDevice(long handle, long platform) implements ComputeDevice {
    
    @Override
    public ComputeContext createContext() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer properties = stack.mallocPointer(3);
            IntBuffer result = stack.mallocInt(1);
            
            properties.put(CL10.CL_CONTEXT_PLATFORM).put(platform).put(0);
            properties.flip();
            
            long context = CL10.clCreateContext(properties, handle, null, 0, result);
            if (result.get(0) != CL10.CL_SUCCESS) throw new RuntimeException("clCreateContext failed: " + result.get(0));
            
            return new CLContext(context, handle);
        }
    }
    
    @Override
    public String getName() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer sizeBuf = stack.mallocPointer(1);
            CL10.clGetDeviceInfo(handle, CL10.CL_DEVICE_NAME, (ByteBuffer) null, sizeBuf);
            
            long size = sizeBuf.get(0);
            
            ByteBuffer nameBuffer = stack.malloc((int) size);
            CL10.clGetDeviceInfo(handle, CL10.CL_DEVICE_NAME, nameBuffer, null);
            
            return StandardCharsets.UTF_8.decode(nameBuffer).toString().trim();
        }
    }
}
