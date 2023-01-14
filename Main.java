import java.io.DataInputStream;
import java.io.IOException;
import java.lang.Thread;
import java.util.Arrays;

import RIONet.socket_handlers.SenderSocket;
import RIONet.socket_handlers.SocketHandlerException;
import RIONet.thread_handlers.ListenerThread;
import RIONet.Packet;
import RIONet.PacketBuilder;
import RIONet.socket_utils.StructUtils;

public class Main {
        public static void main(String[] args) {
                // listener();
                // sender();

                PacketBuilder builder = new PacketBuilder("packets");
                Packet packet = builder.buildFromHeader("EXAMPLE_PACKET");
                packet.setField("ifield1", 2);
                packet.setField("dfield2", 4.8);
                System.out.println(packet);
                byte[] ser = packet.serialize();
                System.out.println(Arrays.toString(ser));

                DataInputStream dis = new DataInputStream(new java.io.ByteArrayInputStream(ser));
                try {
                        short header_length = (short) StructUtils.unpack("h", dis.readNBytes(2))[0];
                        System.out.println(header_length);
                        String header = (String) StructUtils.unpack(header_length + "s",
                                        dis.readNBytes(header_length))[0];
                        System.out.println(header);

                        byte[] rest = dis.readNBytes(builder.sizeOf(header));
                        System.out.println(Arrays.toString(rest));
                        Packet packet2 = builder.buildFromRaw(header, rest);
                        System.out.println(packet2);
                } catch (Exception e) {
                        // TODO: handle exception
                }
        }

        public static void sender() {
                PacketBuilder builder = new PacketBuilder("packets");
                SenderSocket senderSocket = new SenderSocket();
                String ip = "127.0.0.1";
                int port = 6666;

                try {
                        senderSocket.connect(ip, port);
                } catch (IOException e) {
                        System.out.println(String.format("failed to connect to listener on (%s, %d): " + e, ip, port));
                }

                while (true) {
                        Packet packet = builder.buildFromHeader("EXAMPLE_PACKET");
                        packet.setField("ifield1", 2);
                        packet.setField("dfield2", 4.8);

                        try {
                                senderSocket.sendData(packet);
                                System.out.println("sent: " + packet.toString());
                        } catch (IOException e) {
                                System.out.println("faied to send packet to listener: " + e);
                        } catch (SocketHandlerException e) {
                                System.out.println(e);
                        }

                        try {
                                Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                }

        }

        public static void listener() {
                PacketBuilder builder = new PacketBuilder("packets");

                ListenerThread listenerThread = null;
                while (listenerThread == null) {
                        try {
                                listenerThread = new ListenerThread(6666, builder);
                                System.out.println("started listener");
                        } catch (IOException e) {
                                System.out.println(
                                                "An error has accured while trying to start a socket on port 6666" + e);
                        }
                }

                listenerThread.start();

                while (true) {
                        Packet packet = listenerThread.getPacket();
                        if (packet != null) {
                                System.out.println(packet.toString());
                                int ifield1 = packet.getField("ifield1");
                                double dfield2 = packet.getField("dfield2");

                                System.out.println("ifield1: " + ifield1);
                                System.out.println("dfield2: " + dfield2);
                        }
                }
        }
}