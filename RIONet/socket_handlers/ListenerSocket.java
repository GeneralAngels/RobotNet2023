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
        int ibodyLength = NetworkConstants.HeaderPacketSizes.get(header)[0];
        int dbodyLength = NetworkConstants.HeaderPacketSizes.get(header)[0];

        int[] ibody = new int[ibodyLength];
        double[] dbody = new double[dbodyLength];
        for (int i = 0; i < ibodyLength; i++) {
            ibody[i] = (int) inStream.readInt();
        }
        for (int i = ibodyLength; i < dbodyLength + ibodyLength; i++) {
            dbody[i] = (double) inStream.readDouble();
        }
        return new DataObject(header, ibody, dbody);
    }
}
