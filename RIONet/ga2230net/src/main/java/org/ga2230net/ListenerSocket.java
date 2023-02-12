package org.ga2230net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListenerSocket {
    private final DatagramSocket datagramSocket;
    private final PacketBuilder builder;
    private final DatagramPacket rawPacket = new DatagramPacket(new byte[2048], 2048);
    public ListenerSocket(int port, PacketBuilder builder) throws IOException {
        this.builder = builder;

        datagramSocket = new DatagramSocket(port);
    }

    public Packet getPacket() throws IOException, SocketHandlerException {
        datagramSocket.receive(rawPacket);
        int headerLength = rawPacket.getData()[0] + rawPacket.getData()[1];
        byte[] rawHeader = new byte[headerLength];
        System.arraycopy(rawPacket.getData(), 2, rawHeader, 0, headerLength);
        String header = new String(rawHeader);
        byte[] rawBody = new byte[builder.sizeOf(header)];
        System.arraycopy(rawPacket.getData(), 2 + headerLength, rawBody, 0, builder.sizeOf(header));
        return builder.buildFromRaw(header, rawBody);
    }
}
