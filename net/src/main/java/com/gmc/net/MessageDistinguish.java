package com.gmc.net;

public interface MessageDistinguish {
    /**
     * don't need to reset buffer readIndex,it reset in {@link MessageCodeManager#messageDistinguish(BytesBuffer)}
     * @param buffer
     * @return
     */
    Message distinguish(BytesBuffer buffer);
}
