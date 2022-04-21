package com.gmc.net;

public class ConnectTimeoutException extends Exception{
    public ConnectTimeoutException(String message) {
        super(message);
    }

    public ConnectTimeoutException() {
    }
}
