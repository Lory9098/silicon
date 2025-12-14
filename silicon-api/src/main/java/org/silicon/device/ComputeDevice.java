package org.silicon.device;

public interface ComputeDevice {
    ComputeContext createContext() throws Throwable;
    String getName() throws Throwable;
}
