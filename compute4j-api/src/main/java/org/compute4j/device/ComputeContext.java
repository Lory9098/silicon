package org.compute4j.device;

import org.compute4j.kernel.ComputeModule;
import org.compute4j.computing.ComputeQueue;

import java.nio.file.Path;

public interface ComputeContext {
    ComputeQueue createQueue();
    ComputeModule loadModule(Path path);
    ComputeModule loadModule(byte[] rawSrc);
    ComputeModule loadModule(String source);
}
