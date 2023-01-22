package org.ga2230net.socket_handlers;

import org.ga2230net.packets.Packet;
import org.ga2230net.packets.PacketBuilder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class ListenerSocket {
    private DatagramSocket datagramSocket = null;
    private final PacketBuilder builder;
    private final DatagramPacket incomingHeader = new DatagramPacket(new byte[2], 2);
    public ListenerSocket(int port, PacketBuilder builder) throws IOException {
        this.builder = builder;

        datagramSocket = new DatagramSocket(port);
    }

    public Packet getPacket() throws IOException, SocketHandlerException {
        datagramSocket.receive(incomingHeader);
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
