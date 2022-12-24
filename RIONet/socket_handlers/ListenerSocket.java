package RIONet.socket_handlers;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

import RIONet.data_objects.DataHeader;
import RIONet.data_objects.DataObject;
import RIONet.Constants.NetworkConstants;

public class ListenerSocket {

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
    public DataObject getData() throws IOException {
        if (inStream != null) {
            DataHeader header = DataHeader.values()[inStream.readShort()];
            int bodyLength = NetworkConstants.HeaderPacketSizes.get(header);
            int[] body = new int[bodyLength];
            for (int i = 0; i < bodyLength; i++) {
                body[i] = (int) inStream.readInt();
            }

            return new DataObject(header, body);
        } else { // TODO: throw an exception 
            System.out.println("Must first astablish a connection to sender before recieving data");
            return null;
        }
    }
}
