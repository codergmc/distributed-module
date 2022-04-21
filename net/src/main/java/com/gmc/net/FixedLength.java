package com.gmc.net;

public interface FixedLength {
    /**
     * if the message sizeLength is fixed,please implement this interface.
     * The sizeLength only include the fields in the message.The sizeLength of all bytes decode by this message should not include in this message,which should be processed in {@link MessageCodeManager}
     *
     * @return sizeLength of this message
     */
    int size();
}
