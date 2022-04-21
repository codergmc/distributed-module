package com.gmc.net;

import com.gmc.core.LogUtils;

public class MaxRetryException extends Exception{
    public MaxRetryException(int retry, Throwable cause) {
        super(LogUtils.format("max retry times :{}",retry), cause);
    }
}
