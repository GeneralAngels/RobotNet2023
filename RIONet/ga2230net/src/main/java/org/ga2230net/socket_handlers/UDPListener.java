package org.ga2230net.socket_handlers;

import org.ga2230net.packets.Packet;
import org.ga2230net.packets.PacketBuilder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UDPListener implements ListenerSocket {
    private final DatagramSocket socket;
    private final PacketBuilder builder;
    private final DatagramPacket incomingHeader = new DatagramPacket(new byte[2], 2);

    public UDPListener(int port, PacketBuilder builder) throws IOException {
        this.builder = builder;
        socket = new DatagramSocket(port);
    }

    /**
     * udp socket does not support accepting clients
     * @throws SocketHandlerException if the accept method is used
     */
    @Override
    public void accept() throws IOException , SocketHandlerException{
        throw new SocketHandlerException("UDP listener does not support the accept method");
    }

    @Override
    public Packet getPacket() throws IOException {
        socket.receive(incomingHeader);
        short headerLength = (short) ((short)(incomingHeader.getData()[0] & 0xFF) << 8 | (incomingHeader.getData()[1] & 0xFF));
        byte[] rawHeader = new byte[headerLength];
        DatagramPacket headerPack = new DatagramPacket(
            new byte[headerLength],
            headerLength
        );

        String header = new String(headerPack.getData(), StandardCharsets.UTF_8);

        DatagramPacket bodyPack = new DatagramPacket(
            new byte[builder.sizeOf(header)],
            builder.sizeOf(header)
        );

        return builder.buildFromRaw(header, bodyPack.getData());
    }
}
