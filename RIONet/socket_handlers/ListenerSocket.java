package RIONet.socket_handlers;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

import RIONet.data_objects.DataHeader;
import RIONet.data_objects.DataObject;
import RIONet.Constants.NetworkConstants;

public class ListenerSocket { // TODO implement multiple clients connection

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inStream;

    public ListenerSocket(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    /**
     * accepts a sender connection
     */
    public void accept() throws IOException {
        clientSocket = serverSocket.accept();
        inStream = new DataInputStream(clientSocket.getInputStream());
    }

    /**
     * recieve a packet from senders wrapped around a DataObject
     * 
     * @return DataObject the wrapped packet
     */
    public DataObject getData() throws IOException, SocketHandlerException {
        if (clientSocket == null)
            throw new SocketHandlerException("Must first astablish a connection to sender before recieving data!");

        DataHeader header = DataHeader.values()[inStream.readShort()];
        int bodyLength = NetworkConstants.HeaderPacketSizes.get(header);
        int[] body = new int[bodyLength];
        for (int i = 0; i < bodyLength; i++) {
            body[i] = (int) inStream.readInt();
        }
        System.out.println(header.name());
        return new DataObject(header, body);
    }
}
