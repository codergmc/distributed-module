package com.gmc.net;

public interface Writeable {
   /**
    * Encode from bytesBuffer.
    * Only encode the fields in this implementation class.
    * This function don't involve bytesBuffer sizeLength.The sizeLength have processed in {@link MessageCodeManager}*
    * @param buffer
    */
   void write(BytesBuffer buffer);
}
