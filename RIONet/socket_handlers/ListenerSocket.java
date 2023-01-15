package RIONet.socket_handlers;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

import RIONet.packets.Packet;
import RIONet.packets.PacketBuilder;

public class ListenerSocket { // TODO implement multiple clients connection

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inStream;
    private final PacketBuilder packetBuilder;

    /**
     * create a new listener socket
     * @param port the port to listen on
     * @param packetBuilder the packet builder to use to build packets
     * @throws IOException if an error occurs while creating the socket
     */
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
     * @throws IOException if an error occurs while recieving the packet
     * @throws SocketHandlerException if the socket is not connected to a sender
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
