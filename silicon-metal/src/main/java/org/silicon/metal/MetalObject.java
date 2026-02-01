package org.silicon.metal;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.Optional;

public interface MetalObject {
    
    Linker LINKER = Metal.LINKER;
    SymbolLookup LOOKUP = Metal.LOOKUP;
    MethodHandle METAL_RELEASE_OBJECT = MetalObject.find(
        "metal_release_object",
        FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
    );
    
    static MethodHandle find(String callName, FunctionDescriptor descriptor) {
        Optional<MemorySegment> call = LOOKUP.find(callName);
        
        if (call.isEmpty()) throw new NullPointerException("%s is not present".formatted(callName));
        
        return LINKER.downcallHandle(call.get(), descriptor);
    }

    MemorySegment handle();
}
