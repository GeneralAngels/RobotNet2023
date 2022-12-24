package RIONet.socket_handlers;

import java.net.Socket;

import RIONet.data_objects.DataObject;

import java.io.IOException;
import java.io.DataOutputStream;

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
     * @return boolean whether the connection was succesful
     */
    public boolean connect(String ip, int port) throws IOException {
        sock = new Socket(ip, port);
        outStream = new DataOutputStream(sock.getOutputStream());
        return true;
    }

    /**
     * sends data to the listener, the data must be wrapped around a DataObject
     * 
     * @param data DataObject the data to send
     * @return boolean whether the data was sent
     */
    public void sendData(DataObject data) throws IOException {
        if (outStream != null) {
            outStream.write(data.serialize());
        } else { // TODO: throw an exception
            System.out.println("Must first astablish a connection to listener before sending");
        }
    }
}