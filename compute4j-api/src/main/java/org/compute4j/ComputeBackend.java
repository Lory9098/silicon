package org.compute4j;

import org.compute4j.device.ComputeDevice;

public interface ComputeBackend {
    ComputeDevice createSystemDevice(int index);
}
