package RIONet.socket_handlers;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import RIONet.data_objects.DataObject;


public class ListenerSocket {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inStream;

    private byte[] currPacket;

    public ListenerSocket(int port) { // should also accept a DataObject in constructor?

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Failed to establish server socket: " + e);
        }
    }

    public void accept() {
        try {
            clientSocket = serverSocket.accept();
            inStream = new DataInputStream(clientSocket.getInputStream());
        } catch (Exception e) {
            System.out.println("Failed to accept a client: " + e);
        }
    }

    public T getTask() throws IOException {
        if (inStream != null) {
            inStream.read(currPacket);
            return (T)T.deserialize(currPacket);
        } else { // TODO: throw an exception
            System.out.println("Must first astablish a connection to sender before recieving data");
            return null;
        }
    }
}
