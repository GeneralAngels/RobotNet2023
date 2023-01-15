package org.GANet.socket_handlers;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

import org.GANet.Packet;
import org.GANet.PacketBuilder;

public class ListenerSocket { // TODO implement multiple clients connection

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inStream;
    private final PacketBuilder packetBuilder;

    public ListenerSocket(int port, PacketBuilder packetBuilder) throws IOException {
        serverSocket = new ServerSocket(port);
        this.packetBuilder = packetBuilder;
    }

    /**
     * accepts a sender connection
     */
    public void accept() throws IOException {
        clientSocket = serverSocket.accept();
        inStream = new DataInputStream(clientSocket.getInputStream());
    }

    /**
     * recieve a packet from senders wrapped around a Packet object
     *
     * @return the packet
     */
    public Packet getPacket() throws IOException, SocketHandlerException {
        if (clientSocket == null)
            throw new SocketHandlerException("Must first astablish a connection to sender before recieving data!");

        String header = inStream.readUTF();

        byte[] raw_body = new byte[packetBuilder.sizeOf(header)];
        inStream.readFully(raw_body);

        return packetBuilder.buildFromRaw(header, raw_body);
    }
}
