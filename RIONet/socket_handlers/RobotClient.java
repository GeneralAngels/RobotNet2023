package RIONet.socket_handlers;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.io.DataOutputStream;

import RIONet.Constants.NetworkConstants;
import RIONet.Constants.NetworkConstants.Formats;
import RIONet.data_objects.DataObject;
import RIONet.data_objects.DataObjects;
import RIONet.socket_utils.StructUtils;

import RIONet.data_objects.ExampleTask;

public class RobotClient {
    private Socket sock;
    private DataOutputStream outStream;

    private byte[] currPacket;

    public RobotClient(){
        ;
    }

    public void connect(String ip, int port) throws IOException {
        sock = new Socket(ip, port);
        DataOutputStream outStream = new DataOutputStream(sock.getOutputStream());
    }

    public DataObject getData(DataObjects type) throws IOException {  // TODO: fix the DataObject architecture
        currPacket = new byte[StructUtils.sizeOf(Formats.EXAMPLETASK)];
        int isReadSuccessFuly = outStream.read(this.currPacket);
        Object[] unpacked = StructUtils.unpack(NetworkConstants.unpackFormat, this.curPacket);
        for (Object o : unpacked) {
            System.out.print(o.toString() + ", ");
        }
        System.out.println();
        return new ExampleTask(0, 0);
    }
}