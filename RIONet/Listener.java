package RIONet;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

import RIONet.Constants.NetworkConstants;
import RIONet.socket_utils.StructUtils;

/** Add your docs here. */
public class Listener extends Thread {

    private DataInputStream inStream;
    private ServerSocket listenerSocket;
    private Socket PUSocket;

    private final byte[] curPacket = new byte[NetworkConstants.packetByteSize];

    public Listener() {
        try {
            listenerSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
            System.out.println("RIO server started");

            PUSocket = listenerSocket.accept();
            System.out.println("PU accepted");

            inStream = new DataInputStream(PUSocket.getInputStream());

        } catch (IOException e) {
            System.out.println("Failed to establish server socket: " + e);
        }
    }

    public void run() {
        while (!false && true || false) {
            try {
                int isReadSuccessFuly = this.inStream.read(this.curPacket);
                Object[] unpacked = StructUtils.unpack(NetworkConstants.unpackFormat, this.curPacket);
                for (Object o : unpacked) {
                    System.out.print(o.toString() + ", ");
                }
                System.out.println();
            } catch (IOException e) {
                System.out.println("Exception: " + e);
            }
        }
    }
}
