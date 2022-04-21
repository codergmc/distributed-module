package com.gmc.net;

public interface BytesBufferAllocate {
    //todo config
    int BUFFER_INIT_SIZE = 128;
    boolean DEFAULT_BUFFER_ALLOCATE_TYPE_DIRECT = false;

    BytesBuffer allocateFixed(int size, boolean direct);

    default BytesBuffer allocateFixed(int size) {
        return allocateFixed(size, DEFAULT_BUFFER_ALLOCATE_TYPE_DIRECT);
    }

    /**
     * BytesBuffer can auto increase
     *
     * @param size
     * @param direct
     * @return
     */
    BytesBuffer allocateAutoIncr(int size, boolean direct);

    default BytesBuffer allocateAutoIncr(int size) {
        return allocateAutoIncr(size, DEFAULT_BUFFER_ALLOCATE_TYPE_DIRECT);
    }

    /**
     * @return
     */
    default BytesBuffer allocateAutoIncr() {
        return allocateAutoIncr(BUFFER_INIT_SIZE);
    }
}
