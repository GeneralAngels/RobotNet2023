package org.Networking;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

import org.Networking.Constants.NetworkConstants;

import org.Networking.StructUtils;

/** Add your docs here. */
public class Listener extends Thread {

    private DataInputStream inStream;
    private ServerSocket listenerSocket;
    private Socket PUSocket;

    private final byte[] packet = new byte[NetworkConstants.packetByteSize];

    public Listener() {
        try {
            listenerSocket = new ServerSocket(7777);
            System.out.println("PU server started");

            PUSocket = listenerSocket.accept();
            System.out.println("PU accepted");

            inStream = new DataInputStream(PUSocket.getInputStream());

        } catch (IOException e) {
            System.out.println("failed to establish server socket");
        }
    }

    public void run() {
        System.out.println("test");

        while (true) {
            try {
                int i = this.inStream.read(this.packet);
                Object[] unpacked = StructUtils.unpack(NetworkConstants.unpackFormat, this.packet);
                for (Object o: unpacked) {
                    System.out.print(o.toString() + ", ");
                }
                System.out.println();
            } catch (IOException e) {
                System.out.println("exception");
            }
        }
    }
}
