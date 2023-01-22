package org.ga2230net.socket_handlers;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

import org.ga2230net.packets.Packet;

public class SenderSocket {
    private DatagramSocket datagramSocket = null;

    private final int port;
    private final InetAddress listenerAddress;

    public SenderSocket(String ip, int port) throws IOException {
        this.port = port;
        this.listenerAddress = InetAddress.getByName(ip);
        datagramSocket = new DatagramSocket(port);
    }

    /**
     * sends a packet to the listener
     *
     * @param packet the packet to send
     * @throws IOException if there was a problem while sending the packet
     */
    public void sendPacket(Packet packet) throws IOException {
        byte[] ser = packet.serialize();
        DatagramPacket pack = new DatagramPacket(
            ser, ser.length,
            listenerAddress, port
        );
        datagramSocket.send(pack);
    }
}