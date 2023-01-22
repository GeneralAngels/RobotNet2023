package org.ga2230net.socket_handlers;

import org.ga2230net.packets.Packet;
import org.ga2230net.packets.PacketBuilder;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ListenerSocket {
    private DatagramSocket datagramSocket = null;
    private final PacketBuilder builder;
    private final DatagramPacket incomingHeader = new DatagramPacket(new byte[2], 2);
    private ServerSocket serverSocket = null;
    private Socket clientSocket;
    private DataInputStream inStream;

    private SocketType socketType;
    public ListenerSocket(int port, PacketBuilder builder, SocketType socketType) throws IOException, SocketHandlerException {
        this.builder = builder;

        if (socketType == SocketType.TCP) {
            serverSocket = new ServerSocket(port);
        } else if (socketType == SocketType.UDP) {
            datagramSocket = new DatagramSocket(port);
        } else {
            throw new SocketHandlerException("Must specify socket type");
        }
    }

    public void accept() throws IOException, SocketHandlerException {
        if (socketType == SocketType.TCP) {
            clientSocket = serverSocket.accept();
            inStream = new DataInputStream(clientSocket.getInputStream());
        } else if (socketType == SocketType.UDP) {
            throw new SocketHandlerException("UDP listener does not support the accept method");
        }
    }

    public Packet getPacket() throws IOException, SocketHandlerException {
        if (socketType == SocketType.TCP) {
            if (clientSocket == null)
                throw new SocketHandlerException("Must first establish a connection to sender before receiving data!");

            String header = inStream.readUTF();

            byte[] raw_body = new byte[builder.sizeOf(header)];
            inStream.readFully(raw_body);

            return builder.buildFromRaw(header, raw_body);
        } else if (socketType == SocketType.UDP) {
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
        return null;
    }

}
