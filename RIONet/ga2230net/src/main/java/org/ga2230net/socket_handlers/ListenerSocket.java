package org.ga2230net.socket_handlers;

import org.ga2230net.packets.Packet;

import java.io.IOException;

public interface ListenerSocket {
    /**
     * receive a packet from senders wrapped around a Packet object
     *
     * @return the packet
     * @throws IOException if an error occurs while receiving the packet
     * @throws SocketHandlerException if the socket is not connected to a sender
     */
    Packet getPacket() throws IOException;

    /**
     * accepts a single sender connection
     */
    void accept();
}
