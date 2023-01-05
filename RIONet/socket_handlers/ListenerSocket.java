package RIONet.socket_handlers;

import java.net.Socket;
import java.util.Arrays;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

import RIONet.data_objects.DataHeader;
import RIONet.data_objects.DataObject;
import RIONet.data_objects.ExampleData;
import RIONet.Constants.NetworkConstants;
import RIONet.socket_utils.StructUtils;

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
        DataObject packet;
        switch(header){
            case EXAMPLE_HEADER:
                byte[] raw = new byte[StructUtils.sizeOf(NetworkConstants.HeaderPacketFormats.get(DataHeader.EXAMPLE_HEADER))];
                inStream.read(raw);
                System.out.println(Arrays.toString(raw));
                packet = new ExampleData(raw);
                break;
            default:
                packet = null;
                break;
        }

        return packet;
    }
}
