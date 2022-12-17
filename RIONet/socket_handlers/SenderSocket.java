package RIONet.socket_handlers;

import java.net.Socket;
import java.io.IOException;
import java.io.DataOutputStream;

import RIONet.data_objects.RobotUpdate;

public class SenderSocket {
    private Socket sock;
    private DataOutputStream outStream;

    public SenderSocket() {
        ;
    }

    public void connect(String ip, int port) throws IOException {
        sock = new Socket(ip, port);
        outStream = new DataOutputStream(sock.getOutputStream());
    }

    public void sendUpdate(RobotUpdate update) throws IOException, Exception {
        if (outStream != null){
            outStream.write(update.serialize());
        }
        else {  //TODO: throw an exception
            System.out.println("Must first astablish a connection to listener before sending");
        }
        
    }
}