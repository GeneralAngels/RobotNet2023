package org.GANet.socket_handlers;

/** A generic class for socket handlers exceptions */
public class SocketHandlerException extends RuntimeException {
    public SocketHandlerException(String message) {
        super(message);
    }
}
