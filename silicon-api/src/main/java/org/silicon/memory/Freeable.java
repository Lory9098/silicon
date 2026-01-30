package org.silicon.memory;

public interface Freeable {

    default boolean isAlive() {
        return state() == MemoryState.ALIVE;
    }

    default void ensureAlive() {
        if (state() == MemoryState.ALIVE) return;

        throw new IllegalStateException(getClass().getSimpleName() + " is not ALIVE! State: " + state());
    }

    default void ensureOther(Freeable freeable) {
        if (freeable.state() == MemoryState.ALIVE) return;

        throw new IllegalStateException("Other " + freeable.getClass().getSimpleName() + " is not ALIVE! State: " + freeable.state());
    }

    MemoryState state();

    void free();
}
