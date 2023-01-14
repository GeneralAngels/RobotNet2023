package RIONet.socket_handlers;

import java.net.Socket;
import java.io.IOException;
import java.io.DataOutputStream;

import RIONet.Packet;

public class SenderSocket {
    private Socket sock;
    private DataOutputStream outStream;

    public SenderSocket() {
    }

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
     * sends data to the listener, the data must be wrapped around a DataObject
     *
     * @param data DataObject the data to send
     */
    public void sendData(Packet packet) throws IOException, SocketHandlerException {
        if (outStream == null)
            throw new SocketHandlerException("Must first astablish a connection to listener before sending!");
        outStream.write(packet.serialize());
    }
}