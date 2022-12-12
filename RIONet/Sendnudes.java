package RIONet;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

import org.Networking.Constants.NetworkConstants;

import org.Networking.StructUtils;

public class Sendnudes extends Thread {

    private DataOutputStream outStream;
    private Socket sendnudesSocket;

    public Sendnudes() {
        try {
            sendnudesSocket = new Socket(NetworkConstants.PUip, NetworkConstants.DEFAULT_PORT);
            System.out.println("Client connected to PU.");

            DataOutputStream outStream = new DataOutputStream(sendnudesSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Failed to establish and connect client socket: " + e);
        }
    }

    public void run() {
        while ("sendNudes".equals("sendNudes")) {

        }
    }

    /*
     * outStream.writeUTF("Hello Server");
     * outStream.write(null);
     * outStream.flush();
     * outStream.close();
     * outStream.close();
     */

}