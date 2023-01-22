package org.ga2230net.socket_handlers;

import java.net.*;

import java.io.IOException;
import java.io.DataOutputStream;

import org.ga2230net.packets.Packet;

public class SenderSocket {
    private Socket sock = null;
    private DatagramSocket datagramSocket = null;
    private DataOutputStream outStream;

    private final String ip;
    private final int port;
    private final SocketType socketType;
    private final InetAddress listenerAddress;

    public SenderSocket(String ip, int port, SocketType socketType) throws IOException {
        this.ip = ip;
        this.port = port;
        this.socketType = socketType;

        this.listenerAddress = InetAddress.getByName(ip);

        if (socketType == SocketType.UDP) {
            datagramSocket = new DatagramSocket(port);
        }
    }

    /**
     * connects the TCP sender to a TCP listener on the given ip and port
     * @throws IOException if there was a problem while connecting to listener
     * @throws SocketHandlerException if the sender is set as UDP
     */
    public void connect() throws IOException, SocketHandlerException {
        if (socketType == SocketType.UDP) throw new SocketHandlerException("a UDP sender cannot connect to listener");
        sock = new Socket(ip, port);
        outStream = new DataOutputStream(sock.getOutputStream());
    }

    /**
     * sends a packet to the listener
     *
     * @param packet the packet to send
     * @throws IOException if there was a problem while sending the packet
     * @throws SocketHandlerException if the sender is of TCP and a connection to a listener wasn't established
     */
    public void sendPacket(Packet packet) throws IOException, SocketHandlerException {
        if (socketType == SocketType.TCP) {
            if (outStream == null)
                throw new SocketHandlerException("Must first establish a connection to listener before sending!");
            outStream.write(packet.serialize());
        } else if (socketType == SocketType.UDP) {
            byte[] ser = packet.serialize();
            DatagramPacket pack = new DatagramPacket(
                ser, ser.length,
                listenerAddress, port
            );
            datagramSocket.send(pack);
        }
    }
}