package org.silicon.device;

import org.silicon.computing.ComputeQueue;
import org.silicon.memory.Freeable;

public interface ComputeBuffer extends Freeable {
    ComputeBuffer copy();

    ComputeBuffer copyInto(ComputeBuffer other);

    ComputeBuffer copyAsync(ComputeQueue queue);

    ComputeBuffer copyIntoAsync(ComputeBuffer other, ComputeQueue queue);

    void get(byte[] data);

    void get(double[] data);

    void get(float[] data);

    void get(long[] data);

    void get(int[] data);

    void get(short[] data);
}
