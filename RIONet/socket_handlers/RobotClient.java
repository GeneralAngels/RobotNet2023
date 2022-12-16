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
    private DataInputStream inStream;

    private byte[] currPacket;

    public RobotClient(){
        ;
    }

    public void connect(String ip, int port) throws IOException {
        sock = new Socket(ip, port);
        outStream = new DataOutputStream(sock.getOutputStream());
        inStream = new DataInputStream(sock.getInputStream());
    }

    public DataObject getData(DataObjects type) throws IOException {  // TODO: fix the DataObject architecture
        currPacket = new byte[StructUtils.sizeOf(Formats.EXAMPLETASK)];
        int isReadSuccessFuly = inStream.read(currPacket);
        Object[] unpacked = StructUtils.unpack(NetworkConstants.unpackFormat, this.currPacket);
        return new ExampleTask((int)unpacked[0], (int)unpacked[1]);
    }
}