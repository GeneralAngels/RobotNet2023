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

    public boolean connect(String ip, int port) {
        try {
            sock = new Socket(ip, port);
            outStream = new DataOutputStream(sock.getOutputStream());
            return true;
        } catch (IOException e) {
            System.out.println("Failed to connect to listener:  " + e);
            return false;
        }
    }

    public boolean sendData(DataObject data) {
        if (outStream != null) {
            try {
                outStream.write(data.serialize());
                return true;
            }
            catch (IOException e) {
                System.out.println("An error has accured while sending data: " + e);
            }
        } else { // TODO: throw an exception
            System.out.println("Must first astablish a connection to listener before sending");
        }
        return false;

    }
}