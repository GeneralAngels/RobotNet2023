package org.ga2230net.socket_handlers;

import java.net.Socket;

import java.io.IOException;
import java.io.DataOutputStream;

import org.ga2230net.packets.Packet;

public class SenderSocket {
    private Socket sock;
    private DataOutputStream outStream;

    public SenderSocket() {}

    /**
     * connects the sender to a listener on the given ip and port
     *
     * @param ip   the ip of the listener to connect to
     * @param port the port of the listener to connect to
     */
    public void connect(String ip, int port) throws IOException {
        sock = new Socket(ip, port);
        outStream = new DataOutputStream(sock.getOutputStream());
    }

    /**
     * sends a packet to the listener
     *
     * @param packet the packet to send
     */
    public void sendPacket(Packet packet) throws IOException, SocketHandlerException {
        if (outStream == null)
            throw new SocketHandlerException("Must first astablish a connection to listener before sending!");
        outStream.write(packet.serialize());
    }
}