package org.ga2230net;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class SenderSocket {
    private final DatagramSocket datagramSocket;

    private final int port;
    private final InetAddress listenerAddress;

    public SenderSocket(String listener_ip, int listener_port, int sender_port) throws IOException {
        this.port = listener_port;
        this.listenerAddress = InetAddress.getByName(listener_ip);
        datagramSocket = new DatagramSocket(sender_port);
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