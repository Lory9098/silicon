package org.compute4j;

import org.compute4j.device.ComputeDevice;

public interface ComputeBackend {
    boolean isAvailable();
    BackendType getType();
    ComputeDevice createSystemDevice(int index);
}
