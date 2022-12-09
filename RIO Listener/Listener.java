package org.example;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

import org.example.Constants.NetworkConstants;

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
//                for (byte b: this.packet) {
//                    System.out.print(b);
//                }
//                System.out.println();
                String[] unpacked = unpack(NetworkConstants.unpackFormat, this.packet);
                for (String s: unpacked) {
                    System.out.print(s + ", ");
                }
                System.out.println();
            } catch (IOException e) {
                System.out.println("exception");
            }
        }
    }

    public static String[] unpack(String format, byte[] raw) {
        String[] result = new String[format.length()];

        int pos = 0;

        for(int i = 0; i < format.length(); i++){
            char type = format.charAt(i);

            switch (type) {
                case 'x':  // pad type
                    pos += 1;
                    break;
                case 'c': // char
                    char c = (char) raw[pos];
                    result[i] = Character.toString(c);
                    pos += 1;
                    break;
                case 'h': // short
                    ByteBuffer buffer = ByteBuffer.allocate(2);
                    buffer.order(ByteOrder.BIG_ENDIAN);
                    buffer.put(raw[pos]);
                    buffer.put(raw[pos + 1]);
                    short val = buffer.getShort(0);

                    result[i] = Short.toString(val);
                    pos += 2;
                    break;
                case 's': // string
                    StringBuilder s = new StringBuilder();

                    while (raw[pos] != (byte)0x00){
                        char chr = (char) raw[pos];
                        s.append(Character.toString(chr));
                        pos += 1;
                    }
                    result[i] = s.toString();
                    break;
                case 'd':
                    buffer = ByteBuffer.allocate(8);
                    buffer.order(ByteOrder.BIG_ENDIAN);
                    for (int k = 0; i < 8; i++) {
                        buffer.put(raw[pos + k]);
                    }
                    double d = buffer.getDouble();

                    result[i] = Double.toString(d);
                    pos += 8;
                    break;
            }

        }

        return result;
    }

    public static byte[] pack(String format, Object[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(structSize(format));
        for (int i = 0; i < format.length(); i++) {
            switch (format.charAt(i)) {
                case 'c' -> buffer.putChar((Character) data[i]);
                case 'h' -> buffer.putShort((Short) data[i]);
                case 's' -> {
                    String stringData = (String) data[i];
                    buffer.putChar((C) data[i]);
                }
                case 'd' -> buffer.putDouble((Double) data[i]);
            }
        }
    }

    public static int structSize(String format) {
        int size = 0;
        char[] chars = format.toCharArray();

        for (char c: chars) {
            switch (c) {
                case 'c', 's' -> size += 1;  // char, string (same as py)
                case 'h' -> size += 2;  // short
                case 'i', 'f' -> size += 4;  // int, float
                case 'l', 'd' -> size += 8;  // long, double
            }
        }
        return size;
    }
}
